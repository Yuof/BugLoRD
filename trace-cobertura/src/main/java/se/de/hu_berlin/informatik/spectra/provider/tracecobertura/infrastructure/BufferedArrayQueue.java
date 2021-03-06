package se.de.hu_berlin.informatik.spectra.provider.tracecobertura.infrastructure;

import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.coveragedata.Function;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.data.CoverageIgnore;
import se.de.hu_berlin.informatik.spectra.provider.tracecobertura.infrastructure.comptrace.ReplaceableCloneableIterator;

import java.io.*;
import java.util.*;

/**
 * Simple single linked queue implementation using fixed/variable size array nodes.
 *
 * @param <E> the type of elements held in the queue
 */
@CoverageIgnore
public class BufferedArrayQueue<E> extends AbstractQueue<E> implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 2334531021193748807L;

    // keep at most 3 (+1 with the last node) nodes in memory
    private static final int CACHE_SIZE = 3;

    private static final int ARRAY_SIZE = 1000;

    private int arrayLength = ARRAY_SIZE;

    public int getArrayLength() {
        return arrayLength;
    }

    private volatile long size = 0;

    private File output;
    private String filePrefix;
    private volatile int firstStoreIndex = 0;
    private volatile int currentStoreIndex = -1;
    private volatile int lastStoreIndex = -1;

    private volatile int firstNodeSize = 0;

    private volatile transient Node<E> lastNode;

    // cache all other nodes, if necessary
    private transient Map<Integer, Node<E>> cachedNodes = new HashMap<>();
    private transient List<Integer> cacheSequence = new LinkedList<>();

    private transient boolean deleteOnExit;

    private Type serializationType;

    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        sleep();
        stream.writeInt(serializationType.ordinal());
        stream.writeObject(output);
        stream.writeObject(filePrefix);
//        stream.writeObject(lastNode);
        stream.writeInt(firstStoreIndex);
        stream.writeInt(currentStoreIndex);
        stream.writeInt(lastStoreIndex);
        stream.writeInt(firstNodeSize);
        stream.writeLong(size);
        stream.writeInt(arrayLength);
    }

    private volatile transient boolean locked = false;

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    // stores all nodes on disk
    public void sleep() {
        for (Node<E> node : cachedNodes.values()) {
            // stores cached nodes, if modified (i.e., if elements were added/removed)
            if (node.modified) {
                store(node);
            }
            node.cleanup();
        }
        cachedNodes.clear();
        cacheSequence.clear();
        // store the last node, too
        if (lastNode != null && lastNode.modified) {
            store(lastNode);
        }
        lastNode = null;
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        serializationType = Type.values()[stream.readInt()];
        output = (File) stream.readObject();
        filePrefix = (String) stream.readObject();
//        lastNode = (Node<E>) stream.readObject();
        firstStoreIndex = stream.readInt();
        currentStoreIndex = stream.readInt();
        lastStoreIndex = stream.readInt();
        firstNodeSize = stream.readInt();
        size = stream.readLong();
        arrayLength = stream.readInt();

        cachedNodes = new HashMap<>();
        cacheSequence = new LinkedList<>();
        // always delete files from deserialized object TODO
        deleteOnExit = true;
    }

    public BufferedArrayQueue(File outputDir, String filePrefix, boolean deleteOnExit, Type serializationType) {
        this.deleteOnExit = deleteOnExit;
        this.serializationType = serializationType;
        check(outputDir);
        this.output = outputDir;
        this.filePrefix = Objects.requireNonNull(filePrefix);
        initialize();
    }

    private void initialize() {
        firstStoreIndex = 0;
        lastStoreIndex = -1;
        if (lastNode != null) {
            for (int j = lastNode.startIndex; j < lastNode.endIndex; ++j) {
                lastNode.items[j] = null;
            }
            delete(lastNode.storeIndex);
//			lastNode = null;
            lastNode.modified = false;
            lastNode.storeIndex = 0;
            lastNode.startIndex = 0;
            lastNode.endIndex = 0;
            currentStoreIndex = 0;
        } else {
            currentStoreIndex = -1;
        }
        firstNodeSize = 0;
        size = 0;
    }

    public BufferedArrayQueue(File outputDir, String filePrefix, Type serializationType) {
        this(outputDir, filePrefix, true, serializationType);
    }

    private void check(File outputDir) {
        Objects.requireNonNull(outputDir);
        if (outputDir.isFile()) {
            throw new IllegalStateException("Output directory is an existing file: " + outputDir);
        }
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IllegalStateException("Could not create output directory: " + outputDir);
        }
    }

    public BufferedArrayQueue(File output, String filePrefix, int nodeArrayLength, boolean deleteOnExit, Type serializationType) {
        this(output, filePrefix, deleteOnExit, serializationType);
        this.arrayLength = nodeArrayLength < 1 ? 1 : nodeArrayLength;
    }

    public BufferedArrayQueue(File output, String filePrefix, int nodeArrayLength, Type serializationType) {
        this(output, filePrefix, nodeArrayLength, true, serializationType);
    }

    public Type getSerializationType() {
        return serializationType;
    }

    public int getNodeSize() {
        return arrayLength;
    }

    public File getOutputDir() {
        return output;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    /**
     * Creates a new node if the last node is null or full.
     * Increases the size variable.
     * Stores full nodes to the disk.
     */
    private void linkLast(E e) {
        final Node<E> l = loadLast();
        final Node<E> newNode = new Node<>(e, arrayLength, ++currentStoreIndex);
        lastNode = newNode;
        if (l == null) {
            // no nodes did exist, previously
            ++firstNodeSize;
        } else {
            // we don't actually use pointers!
//            l.next = newNode;
            ++lastStoreIndex;
            putInCache(l.storeIndex, l);
//        	store(l);
//        	// we can now remove the node from memory
//        	l.items = null;
        }
        ++size;
    }

    public static enum Type {
        INTEGER,
        LONG,
        OTHER
    }

    private void store(Node<E> node) {
        String filename = getFileName(node.storeIndex);
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            Object[] array = null;
            if (node.hasFreeSpace()) {
                array = new Object[node.endIndex];
                System.arraycopy(node.items, 0, array, 0, node.endIndex);
            } else {
                array = node.items;
            }

            outputStream.writeInt(node.startIndex);
            outputStream.writeInt(node.endIndex);

            switch (serializationType) {
                case INTEGER:
                    for (int i = node.startIndex; i < node.endIndex; ++i) {
                        outputStream.writeInt((int) array[i]);
                    }
                    break;
                case LONG:
                    for (int i = node.startIndex; i < node.endIndex; ++i) {
                        outputStream.writeLong((long) array[i]);
                    }
                    break;
                case OTHER:
                    for (int i = node.startIndex; i < node.endIndex; ++i) {
                        outputStream.writeObject(array[i]);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

            // file can not be removed, due to serialization! TODO
            if (deleteOnExit) {
                new File(filename).deleteOnExit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void putInCache(int storeIndex, Node<E> node) {
        // last node?
        if (storeIndex > lastStoreIndex) {
            lastNode = node;
            return;
        }
        // already in the cache?
        if (cachedNodes.containsKey(storeIndex)) {
            return;
        }
        // remove a cached node if the cache is full
        if (cachedNodes.size() >= CACHE_SIZE) {
            // remove the node from the cache that has been added first
            uncache(cacheSequence.remove(0));
        }
        cachedNodes.put(storeIndex, node);
        cacheSequence.add(storeIndex);
    }

    // should check for modifications and possibly write to the disk
    private void uncache(int storeIndex) {
        Node<E> node = cachedNodes.remove(storeIndex);
        if (node != null) {
            if (node.modified) {
                store(node);
            }
        }
    }

    /**
     * Removes the first node, if it only contains one item.
     * Decreases the size variable.
     */
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        @SuppressWarnings("unchecked") final E element = (E) f.items[f.startIndex];
//        final Node<E> next = f.next;
        if (storedNodeExists()) {
            // there exists a stored node on disk;
//            f.items = null; // help GC
            uncacheAndDelete(firstStoreIndex);
            ++firstStoreIndex;
            --size;
            if (storedNodeExists()) {
                firstNodeSize = arrayLength;
            } else {
                firstNodeSize = (int) size;
            }
        } else {
//        	// there exists only the last node
//        	// keep one node/array to avoid having to allocate a new one
//        	lastNode.startIndex = 0;
//        	lastNode.endIndex = 0;
            // no further nodes
            initialize();
        }
        return element;
    }

    private void uncacheAndDelete(int storeIndex) {
        uncacheNoStore(storeIndex);
        delete(storeIndex);
    }

    private void uncacheNoStore(int storeIndex) {
        if (cachedNodes.containsKey(storeIndex)) {
            Node<E> node = cachedNodes.get(storeIndex);
            node.cleanup();
            cachedNodes.remove(storeIndex);
            cacheSequence.remove((Integer) storeIndex);
        }
    }

    private void delete(int storeIndex) {
        String filename = getFileName(storeIndex);
        // stored node should be deleted
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    private String getFileName(int storeIndex) {
        return output.getAbsolutePath() + File.separator + filePrefix + "-" + storeIndex + ".rry";
    }

    private Node<E> loadFirst() {
        if (storedNodeExists()) {
            return load(firstStoreIndex);
        } else {
            return loadLast();
        }
    }

    private Node<E> loadLast() {
        if (lastNode == null) {
            lastNode = load(lastStoreIndex + 1);
            return lastNode;
        } else {
            return lastNode;
        }
    }

    private Node<E> load(int storeIndex) {
        if (lastNode != null && storeIndex == lastNode.storeIndex) {
            return lastNode;
        }

        // only cache nodes that are not the last node
        if (cachedNodes.containsKey(storeIndex)) {
            // already cached
            return cachedNodes.get(storeIndex);
        }

        String filename = getFileName(storeIndex);
        if (!(new File(filename).exists())) {
            return null;
        }

        Node<E> loadedNode;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            Object[] items = new Object[arrayLength];

            int startIndex = inputStream.readInt();
            int endIndex = inputStream.readInt();

            switch (serializationType) {
                case INTEGER:
                    for (int i = startIndex; i < endIndex; ++i) {
                        items[i] = inputStream.readInt();
                    }
                    break;
                case LONG:
                    for (int i = startIndex; i < endIndex; ++i) {
                        items[i] = inputStream.readLong();
                    }
                    break;
                case OTHER:
                    for (int i = startIndex; i < endIndex; ++i) {
                        items[i] = inputStream.readObject();
                    }
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

//			if (items.length < arrayLength) {
//				Object[] realArray = new Object[arrayLength];
//				System.arraycopy(items, 0, realArray, 0, items.length);
//				items = realArray;
//			}
            loadedNode = new Node<>(items, startIndex, endIndex, storeIndex, arrayLength);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }

        if (storeIndex <= lastStoreIndex) {
            putInCache(storeIndex, loadedNode);
        } else {
            lastNode = loadedNode;
        }
        return loadedNode;
    }


    @Override
    public int size() {
        if (size <= Integer.MAX_VALUE) {
            return (int) size;
        } else {
            throw new IllegalStateException("Size too large!");
        }
    }

    public long longSize() {
        return size;
    }

    public boolean add(E e) {
        if (locked) {
            throw new IllegalStateException("Tried to add value " + e + " while being locked.");
        }

        loadLast();
        if (lastNode != null && lastNode.hasFreeSpace()) {
            lastNode.add(e);
            ++size;
            if (firstStoreIndex == lastNode.storeIndex) {
                ++firstNodeSize;
            }
//    		System.out.println(e + " -> added " + lastNode.storeIndex + ", " + lastNode.endIndex);
        } else {
            linkLast(e);
//    		System.out.println(e + " -> linked " + lastNode.storeIndex + ", " + lastNode.endIndex);
        }
        return true;
    }

    private void clearCache() {
        for (Node<E> node : cachedNodes.values()) {
            node.cleanup();
        }
        cachedNodes.clear();
        cacheSequence.clear();
    }

    public void clear() {
        if (locked) {
            throw new IllegalStateException("Tried to clear queue while being locked.");
        }

        clearCache();
        // delete possibly stored nodes
        for (; storedNodeExists(); ++firstStoreIndex) {
            delete(firstStoreIndex);
        }
        // delete potentially stored last node
        delete(lastStoreIndex + 1);

        initialize();
    }

    private boolean storedNodeExists() {
        return firstStoreIndex <= lastStoreIndex;
    }

    /**
     * Same as {@link #clear()}, but only removes the first {@code count} elements.
     *
     * @param count the number of elements to clear from the list
     */
    public void clear(long count) {
        if (count >= size) {
            clear();
            return;
        }
        if (locked) {
            throw new IllegalStateException("Tried to clear queue while being locked.");
        }

        Node<E> x = null;
        if (storedNodeExists()) {
            // do not load nodes, if possible
            while (count >= firstNodeSize) {
                // we can just delete the file without looking at the node
                uncacheAndDelete(firstStoreIndex);
                ++firstStoreIndex;
                count -= firstNodeSize;
                size -= firstNodeSize;
                // still a node on disk?
                if (storedNodeExists()) {
                    firstNodeSize = arrayLength;
                } else {
                    break;
                }
            }

            // still elements left to clear?
            if (count > 0) {
                if (storedNodeExists()) {
                    x = load(firstStoreIndex);
                } else {
                    loadLast();
                    // no node left on disk, so continue with the last node
                    firstNodeSize = lastNode.endIndex - lastNode.startIndex;
                    x = lastNode;
                }
            }
        } else {
            loadLast();
            x = lastNode;
        }

        if (count > 0 && x != null) {
            // remove elements from first node
            x.clear((int) count);
            firstNodeSize -= count;
        }

        size -= count;
    }

    /**
     * Same as {@link #clear()}, but only removes the last {@code count} elements.
     *
     * @param count the number of elements to clear from the list
     */
    public void clearLast(long count) {
        if (count >= size) {
            clear();
            return;
        }
        if (locked) {
            throw new IllegalStateException("Tried to clear queue while being locked.");
        }

        loadLast();
        int removedElements = lastNode.size();
        int storeIndex = lastNode.storeIndex;

        if (count >= removedElements) {
            // we can delete the last node's file
            uncacheAndDelete(storeIndex);
            --lastStoreIndex;
            --currentStoreIndex;
            count -= removedElements;
            size -= removedElements;
            lastNode = null;
        }

        // check if we can delete entire nodes without actually loading the respective nodes
        while (count >= arrayLength) {
            // we can just delete the respective file
            uncacheAndDelete(--storeIndex);
            --lastStoreIndex;
            --currentStoreIndex;
            count -= arrayLength;
            size -= arrayLength;
        }

        // ensure that the last node is not cached
        loadLast();
        removeFromCache(lastNode.storeIndex);

        // still elements left to clear?
        if (count > 0) {
            // it holds that count < arrayLength
            lastNode.clearLast((int) count);
            if (!storedNodeExists()) {
                // no node left on disk, so continue with the last node
                firstNodeSize = lastNode.endIndex - lastNode.startIndex;
            }
        }

        size -= count;
    }

    private void removeFromCache(int storeIndex) {
        if (cachedNodes.containsKey(storeIndex)) {
            cachedNodes.remove(storeIndex);
            cacheSequence.remove((Integer) storeIndex);
        }
    }

    /**
     * Same as {@link #clear()}, but removes {@code count} elements,
     * starting from {@code startIndex}.
     *
     * @param startIndex the index to start clearing from
     * @param count      the number of elements to clear from the list
     */
    public void clearFrom(long startIndex, long count) {
        if (count == 0) {
            return;
        }
        if (count >= size) {
            clear();
            return;
        }
        if (startIndex + count >= size) {
            clearLast(size - startIndex);
            return;
        }
        if (locked) {
            throw new IllegalStateException("Tried to clear queue while being locked.");
        }

        int startNodeIndex = -1;
        int startItemIndex = (int) startIndex;
        Node<E> startNode = null;
        // we can compute the store index using the size of the 
        // first node and the constant size of each array node
        if (startIndex < firstNodeSize) {
            startNode = loadFirst();
            if (startNode == null || startNode.startIndex >= startNode.endIndex)
                throw new NoSuchElementException();
            startNodeIndex = startNode.storeIndex;
        } else {
            int i = (int) (startIndex - firstNodeSize);
            startNodeIndex = firstStoreIndex + 1 + (i / arrayLength);
            startItemIndex = i % arrayLength;
            startNode = load(startNodeIndex);
        }

        long endItemIndex = startIndex + count;

        // shift the remaining elements to the left
        ReplaceableCloneableIterator<E> iterator = iterator(endItemIndex);
        while (iterator.hasNext()) {
            // since we keep a reference to the node here, we might make changes to it,
            // but we might also have remove the node from the cache earlier
            startNode.set(startItemIndex++, iterator.next());
            if (startNode.startIndex + startItemIndex >= startNode.endIndex) {
                // ensure that the node is in the cache after being modified
                putInCache(startNode.storeIndex, startNode);
                startItemIndex = 0;
                startNode = load(++startNodeIndex);
            }
        }
        // ensure that the node is in the cache after being modified
        putInCache(startNode.storeIndex, startNode);

        // now just remove the duplicate entries at the end
        clearLast(count);
    }

    @SuppressWarnings("unchecked")
    public E peekLast() {
        final Node<E> f = loadLast();
        if (f == null || f.startIndex >= f.endIndex)
            return null;
        return (E) f.items[f.endIndex - 1];
    }

    @SuppressWarnings("unchecked")
    public E lastElement() {
        final Node<E> f = loadLast();
        if (f == null || f.startIndex >= f.endIndex)
            if (f == null)
                throw new NoSuchElementException("size: " + size);
            else
                throw new NoSuchElementException("startindex: " + (f.startIndex) + ", endindex: " + (f.endIndex) + ", size: " + size);
        return (E) f.items[f.endIndex - 1];
    }

    // Queue operations

    @SuppressWarnings("unchecked")
    @Override
    public E peek() {
        final Node<E> f = loadFirst();
        if (f == null || f.startIndex >= f.endIndex)
            return null;
        return (E) f.items[f.startIndex];
    }

    @SuppressWarnings("unchecked")
    @Override
    public E element() {
        final Node<E> f = loadFirst();
        if (f == null || f.startIndex >= f.endIndex)
            if (f == null)
                throw new NoSuchElementException("size: " + size);
            else
                throw new NoSuchElementException("startindex: " + (f.startIndex) + ", endindex: " + (f.endIndex) + ", size: " + size);
        return (E) f.items[f.startIndex];
    }

    public E poll() {
        if (locked) {
            throw new IllegalStateException("Tried to remove element while being locked.");
        }

        final Node<E> f = loadFirst();
        if (f == null || f.startIndex >= f.endIndex)
            return null;
        return removeFirst(f);
    }

    public E remove() {
        if (locked) {
            throw new IllegalStateException("Tried to remove element while being locked.");
        }

        final Node<E> f = loadFirst();
        if (f == null || f.startIndex >= f.endIndex)
            if (f == null)
                throw new NoSuchElementException("size: " + size);
            else
                throw new NoSuchElementException("startindex: " + (f.startIndex) + ", endindex: " + (f.endIndex) + ", size: " + size);
        return removeFirst(f);
    }

    /*
     * Removes the first element.
     */
    private E removeFirst(Node<E> f) {
        if (f.startIndex < f.endIndex - 1) {
            --size;
            --firstNodeSize;
            return f.remove();
        } else {
            return unlinkFirst(f);
        }
    }

    public boolean offer(E e) {
        return add(e);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ReplaceableCloneableIterator<E> iterator() {
        return new MyBufferedIterator();
    }

    public ReplaceableCloneableIterator<E> iterator(final long position) {
        return new MyBufferedIterator(position);
    }

    public final class MyBufferedIterator implements ReplaceableCloneableIterator<E> {

        int storeIndex;
        int index;

        MyBufferedIterator(long i) {
            setToPosition(i);
        }

        public void setToPosition(long i) {
            // we can compute the store index using the size of the 
            // first node and the constant size of each array node
            if (i < firstNodeSize) {
                storeIndex = firstStoreIndex;
                Node<E> node = load(storeIndex);
                index = (int) (i + node.startIndex);
            } else {
                i -= firstNodeSize;
                storeIndex = (int) (firstStoreIndex + 1 + (i / arrayLength));
                index = (int) (i % arrayLength);
            }
        }

        MyBufferedIterator() {
            if (storedNodeExists()) {
                storeIndex = firstStoreIndex;
                Node<E> node = load(storeIndex);
                index = node.startIndex;
            } else if (loadLast() != null) {
                storeIndex = lastNode.storeIndex;
                index = lastNode.startIndex;
            } else {
                storeIndex = -1;
                index = -1;
            }
        }

        // clone constructor
        private MyBufferedIterator(MyBufferedIterator iterator) {
            storeIndex = iterator.storeIndex;
            index = iterator.index;
        }

        public MyBufferedIterator clone() {
            return new MyBufferedIterator(this);
        }

        public boolean hasNext() {
            if (storeIndex < 0) {
                return false;
            }
            Node<E> node = load(storeIndex);
            return node != null && index < node.endIndex;
        }

        public E next() {
            Node<E> currentNode = load(storeIndex);
            @SuppressWarnings("unchecked")
            E temp = (E) currentNode.items[index++];
            if (index >= currentNode.endIndex) {
                if (storeIndex < lastStoreIndex) {
                    ++storeIndex;
                } else if (loadLast() != null) {
                    if (storeIndex >= lastNode.storeIndex) {
                        // already at the last node
                        storeIndex = -1;
                    } else {
                        // process the last node next
                        storeIndex = lastNode.storeIndex;
                    }
                } else {
                    // should really not happen...
                    storeIndex = -1;
                }
                index = 0;
            }
            return temp;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E processNextAndReplaceWithResult(Function<E, E> function) {
            Node<E> currentNode = load(storeIndex);
            @SuppressWarnings("unchecked")
            E temp = (E) currentNode.items[index];
            // replace item with function's result; otherwise the same as next()
            currentNode.items[index] = function.apply(temp);
            index++;
            if (index >= currentNode.endIndex) {
                if (storeIndex < lastStoreIndex) {
                    ++storeIndex;
                } else if (loadLast() != null) {
                    if (storeIndex >= lastNode.storeIndex) {
                        // already at the last node
                        storeIndex = -1;
                    } else {
                        // process the last node next
                        storeIndex = lastNode.storeIndex;
                    }
                } else {
                    // should really not happen...
                    storeIndex = -1;
                }
                index = 0;
            }
            return temp;
        }

        @SuppressWarnings("unchecked")
        public E peek() {
            Node<E> currentNode = load(storeIndex);
            return (E) currentNode.items[index];
        }

//		public void remove() {
//			throw new UnsupportedOperationException();
//		}
    }

    private static class Node<E> implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 9086365169265922352L;

        private volatile transient boolean modified = false;

        // index to store/load this node
        private volatile int storeIndex;

        private Object[] items;
        // points to first actual item slot
        private volatile int startIndex = 0;
        // points to last free slot
        private volatile int endIndex = 1;

        private int arrayLength;
        // pointer to next array node

        Node(E element, int arrayLength, int storeIndex) {
            this.arrayLength = arrayLength;
            if (arrayLength < 1) {
                throw new IllegalStateException();
            }
            this.items = new Object[arrayLength];
            items[0] = element;
            this.storeIndex = storeIndex;
            // ensure that new nodes are stored (if not empty)
            this.modified = true;
        }

        public void clearLast(int count) {
            for (int i = endIndex - 1; i >= endIndex - count; --i) {
                this.items[i] = null;
            }
            this.endIndex -= count;
            this.modified = true;
        }

        public void clear(int count) {
            for (int i = startIndex; i < startIndex + count; ++i) {
                this.items[i] = null;
            }
            this.startIndex += count;
            this.modified = true;
        }

        public void cleanup() {
            for (int i = startIndex; i < endIndex; ++i) {
                this.items[i] = null;
            }
            this.items = null;
        }

        public int size() {
            return endIndex - startIndex;
        }

        public Node(Object[] items, int startIndex, int endIndex, int storeIndex, int arrayLength) {
            this.items = items;
            this.endIndex = endIndex;
            this.startIndex = startIndex;
            this.storeIndex = storeIndex;
            this.arrayLength = arrayLength;
        }

        // removes the first element
        public E remove() {
            this.modified = true;
            @SuppressWarnings("unchecked")
            E temp = (E) items[startIndex];
            items[startIndex++] = null;
            return temp;
        }

        // adds an element to the end
        public void add(E e) {
            this.modified = true;
            if (items.length < arrayLength) {
                extendArray();
            }
            items[endIndex++] = e;
        }

        private void extendArray() {
            Object[] temp = items;
            items = new Object[arrayLength];
            System.arraycopy(temp, startIndex, items, startIndex, temp.length - startIndex);
        }

        // checks whether an element can be added to the node's array
        boolean hasFreeSpace() {
            return endIndex < arrayLength;
        }

        @SuppressWarnings("unchecked")
        public E get(int i) {
            return (E) items[i + startIndex];
        }

        public void set(int i, E value) {
            this.modified = true;
            if (items.length <= i + startIndex) {
                extendArray();
            }
            items[i + startIndex] = value;
        }

    }

    @Override
    public void finalize() throws Throwable {
        // due to serialization, we need to rely on the user to clear queues manually TODO
        if (deleteOnExit) {
            this.clear();
        }
        super.finalize();
    }

    public E get(long i) {
        // we can compute the store index using the size of the 
        // first node and the constant size of each array node
        if (i < firstNodeSize) {
            final Node<E> f = loadFirst();
            if (f == null || f.startIndex >= f.endIndex)
                throw new NoSuchElementException();
            return f.get((int) i);
        } else {
            i -= firstNodeSize;
            int storeIndex = (int) (firstStoreIndex + 1 + (i / arrayLength));
            int itemIndex = (int) (i % arrayLength);
            final Node<E> f = load(storeIndex);
            if (f == null || itemIndex >= f.endIndex)
                throw new NoSuchElementException("index: " + (i + firstNodeSize) + ", size: " + size);
            return f.get(itemIndex);
        }
    }

    private void set(long i, E value) {
        if (locked) {
            throw new IllegalStateException("Tried to set value at index " + i + " to " + value + " while being locked.");
        }
        // we can compute the store index using the size of the 
        // first node and the constant size of each array node
        if (i < firstNodeSize) {
            final Node<E> f = loadFirst();
            if (f == null || f.startIndex >= f.endIndex)
                throw new NoSuchElementException();
            f.set((int) i, value);
        } else {
            i -= firstNodeSize;
            int storeIndex = (int) (firstStoreIndex + 1 + (i / arrayLength));
            int itemIndex = (int) (i % arrayLength);
            final Node<E> f = load(storeIndex);
            if (f == null || itemIndex >= f.endIndex)
                throw new NoSuchElementException();
            f.set(itemIndex, value);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[ ");
        ReplaceableCloneableIterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next()).append(", ");
        }
        builder.setLength(builder.length() > 2 ? builder.length() - 2 : builder.length());
        builder.append(" ]");
        return builder.toString();
    }

    public boolean isDeleteOnExit() {
        return deleteOnExit;
    }

    public E getAndReplaceWith(long i, Function<E, E> function) {
        E previous = get(i);
        set(i, function.apply(previous));
        return previous;
    }

    public void deleteOnExit() {
        deleteOnExit = true;
    }

}
