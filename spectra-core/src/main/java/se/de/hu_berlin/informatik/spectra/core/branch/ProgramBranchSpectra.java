package se.de.hu_berlin.informatik.spectra.core.branch;

import se.de.hu_berlin.informatik.spectra.core.INode;
import se.de.hu_berlin.informatik.spectra.core.ISpectra;
import se.de.hu_berlin.informatik.spectra.core.ITrace;
import se.de.hu_berlin.informatik.spectra.core.SourceCodeBlock;
import se.de.hu_berlin.informatik.spectra.core.hit.HitSpectra;
import se.de.hu_berlin.informatik.spectra.util.CachedIntArrayMap;
import se.de.hu_berlin.informatik.spectra.util.CachedMap;
import se.de.hu_berlin.informatik.spectra.util.CachedSourceCodeBlockMap;
import se.de.hu_berlin.informatik.spectra.util.SpectraFileUtils;
import se.de.hu_berlin.informatik.utils.files.FileUtils;

import java.nio.file.Path;

public class ProgramBranchSpectra<T> extends HitSpectra<T> {

    /*====================================================================================
     * CONSTRUCTORS
     *====================================================================================*/

    public ProgramBranchSpectra(Path spectraZipFile) {
        super(spectraZipFile);
    }
    
    CachedMap<SourceCodeBlock> statementMap;
    CachedMap<int[]> nodeSequenceMap;
    CachedMap<int[]> subTraceSequenceMap;
    
    public void addFromStatementSpectra(ISpectra<SourceCodeBlock, ? extends ITrace<SourceCodeBlock>> spectra, Path temporaryOutputDir) {
    	Path targetPath = temporaryOutputDir == null ? 
    			spectra.getPathToSpectraZipFile().getParent().resolve("branchMap.zip") :
    				temporaryOutputDir.resolve("branchMap.zip");
    	FileUtils.delete(targetPath);
    	
		statementMap = new CachedSourceCodeBlockMap(targetPath, 10000, SpectraFileUtils.STATEMENT_MAP_DIR, true);
    	for (INode<SourceCodeBlock> node : spectra.getNodes()) {
			statementMap.put(node.getIndex(), node.getIdentifier());
		}
    	
    	spectra.getIndexer().getSubTraceIdSequences().moveMapContentsTo(targetPath, SpectraFileUtils.BRANCH_SUB_TRACE_ID_SEQUENCES_DIR);
    	spectra.getIndexer().getNodeIdSequences().moveMapContentsTo(targetPath, SpectraFileUtils.BRANCH_NODE_ID_SEQUENCES_DIR);
    	
    	subTraceSequenceMap = new CachedIntArrayMap(targetPath, 10000, SpectraFileUtils.BRANCH_SUB_TRACE_ID_SEQUENCES_DIR, false);
		nodeSequenceMap = new CachedIntArrayMap(targetPath, 10000, SpectraFileUtils.BRANCH_NODE_ID_SEQUENCES_DIR, false);
		
//    	CachedMap<int[]> subTraceSequenceIdSequences = spectra.getIndexer().getSubTraceIdSequences();
//    	if (subTraceSequenceIdSequences != null) {
//    		subTraceSequenceMap = new CachedIntArrayMap(spectra.getPathToSpectraZipFile().getParent().resolve("branchMap.zip"),
//        			10000, SpectraFileUtils.BRANCH_SUB_TRACE_ID_SEQUENCES_DIR, true);
//    		for (Integer index : subTraceSequenceIdSequences.keySet()) {
//    			subTraceSequenceMap.put(index, subTraceSequenceIdSequences.get(index));
//			}
//    	}
//    	CachedMap<int[]> nodeIdSequences = spectra.getIndexer().getNodeIdSequences();
//    	if (nodeIdSequences != null) {
//    		nodeSequenceMap = new CachedIntArrayMap(spectra.getPathToSpectraZipFile().getParent().resolve("branchMap.zip"),
//        			10000, SpectraFileUtils.BRANCH_NODE_ID_SEQUENCES_DIR, true);
//    		for (Integer index : nodeIdSequences.keySet()) {
//    			nodeSequenceMap.put(index, nodeIdSequences.get(index));
//			}
//    	}
    }

	public CachedMap<SourceCodeBlock> getStatementMap() {
		return statementMap;
	}

	public void setStatementMap(CachedMap<SourceCodeBlock> statementMap) {
		this.statementMap = statementMap;
	}

	public CachedMap<int[]> getNodeSequenceMap() {
		return nodeSequenceMap;
	}

	public void setNodeSequenceMap(CachedMap<int[]> nodeSequenceMap) {
		this.nodeSequenceMap = nodeSequenceMap;
	}

	public CachedMap<int[]> getSubTraceSequenceMap() {
		return subTraceSequenceMap;
	}

	public void setSubTraceSequenceMap(CachedMap<int[]> subTraceSequenceMap) {
		this.subTraceSequenceMap = subTraceSequenceMap;
	}
    
    
//    /*====================================================================================
//     * PUBLIC
//     *====================================================================================*/
//
//    public INode<ProgramBranch> getBranchNode(int branchId) {
//
//        INode<ProgramBranch> branchNode;
//
//        branchNode = branchIdMap.get(branchId);
//
//        return branchNode;
//
//    }
//
//    /*====================================================================================
//     * PRIVATE
//     *====================================================================================*/
//
//    public void setBranchIdMap(Map<Integer, INode<ProgramBranch>> branchIdMap) {
//
//        /*====================================================================================*/
//        assert (branchIdMap != null);
//        /*====================================================================================*/
//
//        this.branchIdMap = ImmutableMap.copyOf(branchIdMap);
//
//    }
//
//    /*====================================================================================
//     * FIELDS
//     *====================================================================================*/
//
//    //branches in the execution traces are only identified by an integer id
//    //--> this map maps the id to the actual node representing the branch
//    private ImmutableMap<Integer, INode<ProgramBranch>> branchIdMap;

}
