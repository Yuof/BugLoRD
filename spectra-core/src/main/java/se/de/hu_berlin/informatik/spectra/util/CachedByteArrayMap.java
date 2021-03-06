package se.de.hu_berlin.informatik.spectra.util;

import java.nio.file.Path;

public class CachedByteArrayMap extends CachedMap<byte[]> {

	public CachedByteArrayMap(Path outputZipFile, int cacheSize, int entryFileSize, String id, boolean deleteAtShutdown) {
        super(outputZipFile, cacheSize, entryFileSize, id, deleteAtShutdown);
    }
	
    public CachedByteArrayMap(Path outputZipFile, int cacheSize, String id, boolean deleteAtShutdown) {
        super(outputZipFile, cacheSize, id, deleteAtShutdown);
    }

    @Override
    public byte[] toByteArray(byte[] sequence) {
    	return sequence;
    }

    @Override
    public byte[] fromByteArray(byte[] array) {
        return array;
    }

}
