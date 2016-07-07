/**
 * 
 */
package se.de.hu_berlin.informatik.defects4j.frontend;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import se.de.hu_berlin.informatik.combranking.CombineSBFLandNLFLRanking;
import se.de.hu_berlin.informatik.javatokenizer.tokenize.Tokenize;
import se.de.hu_berlin.informatik.javatokenizer.tokenizelines.TokenizeLines;
import se.de.hu_berlin.informatik.utils.fileoperations.ListToFileWriterModule;
import se.de.hu_berlin.informatik.utils.fileoperations.SearchForFilesOrDirsModule;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.ModuleLinker;

/**
 * Builds a local language model,
 * collects sentences from the source files based on the SBFL ranking files,
 * queries these sentences to the local and global language model,
 * and combines the generated rankings.
 * 
 * @author SimHigh
 */
public class QueryAndCombine {
	
private final static String SEP = File.separator;
	
	/**
	 * Parses the options from the command line.
	 * @param args
	 * the application's arguments
	 * @return
	 * an {@link OptionParser} object that provides access to all parsed options and their values
	 */
	private static OptionParser getOptions(String[] args) {
//		final String tool_usage = "QueryAndCombine -p project -b bugID"; 
		final String tool_usage = "QueryAndCombine";
		final OptionParser options = new OptionParser(tool_usage, args);

        options.add(Prop.OPT_PROJECT, "project", true, "A project of the Defects4J benchmark. "
        		+ "Should be either 'Lang', 'Chart', 'Time', 'Closure' or 'Math'.", true);
        options.add(Prop.OPT_BUG_ID, "bugID", true, "A number indicating the id of a buggy project version. "
        		+ "Value ranges differ based on the project.", true);
        
        options.parseCommandLine();
        
        return options;
	}
	
	
	/**
	 * @param args
	 * -p project -b bugID
	 */
	public static void main(String[] args) {
		
		OptionParser options = getOptions(args);	
		
		String project = options.getOptionValue(Prop.OPT_PROJECT);
		String id = options.getOptionValue(Prop.OPT_BUG_ID);
		int parsedID = Integer.parseInt(id);
		
		Prop.validateProjectAndBugID(project, parsedID, true);
		
		String buggyID = id + "b";
		String fixedID = id + "f";
		
		//this is important!!
		Prop.loadProperties(project, buggyID, fixedID);
		
		File executionBuggyVersionDir = Paths.get(Prop.executionBuggyWorkDir).toFile();
		executionBuggyVersionDir.mkdirs();
		File archiveBuggyWorkDir = Paths.get(Prop.archiveBuggyWorkDir).toFile();
		
		if (!archiveBuggyWorkDir.exists()) {
			Misc.abort("Archive buggy project version directory doesn't exist: '" + Prop.archiveBuggyWorkDir + "'.");
		}
		
		/* #====================================================================================
		 * # tokenize java source files and build local LM
		 * #==================================================================================== */
		String buggyMainSrcDir = Prop.executeCommandWithOutput(archiveBuggyWorkDir, false, 
				Prop.defects4jExecutable, "export", "-p", "dir.src.classes");
		Misc.out("main source directory: <" + buggyMainSrcDir + ">");
		
		File localLMDir = Paths.get(executionBuggyVersionDir.toString(), "_localLM").toFile();
		localLMDir.mkdirs();
		String tokenOutputDir = localLMDir + SEP + "tokens";
		Tokenize.tokenizeDefects4JElement(Prop.archiveBuggyWorkDir + SEP + buggyMainSrcDir, tokenOutputDir);
		
		//generate a file that contains a list of all token files (needed by SRILM)
		new ModuleLinker().link(
				new SearchForFilesOrDirsModule("**/*.{tkn}", false, true, true),
				new ListToFileWriterModule<List<Path>>(Paths.get(tokenOutputDir, "list"), true))
		.submit(Paths.get(tokenOutputDir));
		
		//make batch counts with SRILM
		String countsDir = localLMDir + SEP + "counts";
		Paths.get(countsDir).toFile().mkdirs();
		Prop.executeCommand(localLMDir, Prop.sriLMmakeBatchCountsExecutable, 
				tokenOutputDir + SEP + "list", "10", "/bin/cat", countsDir, "-order", "10", "-unk");
		
		//merge batch counts with SRILM
		Prop.executeCommand(localLMDir, Prop.sriLMmergeBatchCountsExecutable, countsDir);
		
		//estimate language model of order 10 with SRILM
		String localLM = localLMDir + SEP + "temp.arpa";
		Prop.executeCommand(localLMDir, Prop.sriLMmakeBigLMExecutable, "-read", 
				countsDir + SEP + "*.gz", "-lm", localLM, "-order", "10", "-unk");
		
		//build binary with kenLM
		String localLMbinary = archiveBuggyWorkDir + SEP + "local.binary";
		Prop.executeCommand(executionBuggyVersionDir, Prop.kenLMbuildBinaryExecutable,
				localLM, localLMbinary);
		
		//delete the temporary local LM files (only binary is being kept)
		Misc.delete(localLMDir);
		
		/* #====================================================================================
		 * # generate the sentences and query them to the language models
		 * #==================================================================================== */
		List<Path> rankingFiles = new SearchForFilesOrDirsModule("**/*.{rnk}", false, true, true)
				.submit(Paths.get(Prop.archiveBuggyWorkDir))
				.getResult();
		
		List<Path> traceFiles = new SearchForFilesOrDirsModule("**/*.{trc}", false, true, true)
		.submit(Paths.get(Prop.archiveBuggyWorkDir))
		.getResult();
		
		String traceFile = null;
		boolean foundSingleTraceFile = false;
		String sentenceOutput = executionBuggyVersionDir + SEP + ".sentences";
		String globalRankingFile = executionBuggyVersionDir + SEP + ".global";
		String localRankingFile = executionBuggyVersionDir + SEP + ".local";
		
		//if a single trace file has been found, then compute the global and local rankings only once
		if (traceFiles.size() == 1) {
			foundSingleTraceFile = true;
			traceFile = traceFiles.get(0).toAbsolutePath().toString();
			
			TokenizeLines.tokenizeLinesDefects4JElement(archiveBuggyWorkDir + SEP + buggyMainSrcDir,
					traceFile, sentenceOutput, "10");
			
			Prop.executeCommand(executionBuggyVersionDir, "/bin/sh", "-c", Prop.kenLMqueryExecutable 
					+ " -n -c " + Prop.globalLM + " < " + sentenceOutput + " > " + globalRankingFile);
			
			Prop.executeCommand(executionBuggyVersionDir, "/bin/sh", "-c", Prop.kenLMqueryExecutable 
					+ " -n -c " + localLMbinary + " < " + sentenceOutput + " > " + localRankingFile);
		}
		
		//iterate over all ranking files
		for (Path rankingFile : rankingFiles) {
			//if none or multiple trace files have been found, use the respective SBFL files
			//instead of a trace file. This queries the sentences to the LMs for each ranking file...
			if (!foundSingleTraceFile) {
				traceFile = rankingFile.toAbsolutePath().toString();

				TokenizeLines.tokenizeLinesDefects4JElement(archiveBuggyWorkDir + SEP + buggyMainSrcDir,
						traceFile, sentenceOutput, "10");

				Prop.executeCommand(executionBuggyVersionDir, "/bin/sh", "-c", Prop.kenLMqueryExecutable 
						+ " -n -c " + Prop.globalLM + " < " + sentenceOutput + " > " + globalRankingFile);
				
				Prop.executeCommand(executionBuggyVersionDir, "/bin/sh", "-c", Prop.kenLMqueryExecutable 
						+ " -n -c " + localLMbinary + " < " + sentenceOutput + " > " + localRankingFile);
			}
			
			//combine the rankings
			String rankingDir = Prop.archiveBuggyWorkDir + SEP + "ranking";
			String[] gp = { "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", 
					"50", "55", "60", "65", "70", "75", "80", "85", "90", "95" };
			String[] lp = { "100" };
			CombineSBFLandNLFLRanking.combineSBFLandNLFLRankingsForDefects4JElement(
					rankingFile.toAbsolutePath().toString(), traceFile,
					globalRankingFile, localRankingFile, rankingDir, gp, lp);
		}
		
		//delete the local LM binary, since it isn't needed any more
		Misc.delete(Paths.get(localLMbinary));
		
	}
	
}
