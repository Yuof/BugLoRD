package se.de.hu_berlin.informatik.changechecker;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SignificanceLevel;
import ch.uzh.ifi.seal.changedistiller.model.entities.*;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import se.de.hu_berlin.informatik.changechecker.ChangeWrapper.ModificationType;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.files.processors.FileToStringListReader;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ChangeCheckerUtils {

    /**
     * Compares the given files and returns a list of changes with information
     * about all discovered changes, including line numbers, types and
     * significance level.
     *
     * @param left             the first file
     * @param right            the second file
     * @param removeNoise      whether to remove changes with no delta lines attached
     * @param removeNonChanges whether to remove changes that do not actually represent real changes
     * @return a list of changes, or null if an error occurred
     */
    public static List<ChangeWrapper> checkForChanges(File left, File right, boolean removeNoise, boolean removeNonChanges) {

        List<Delta<String>> deltas = getDeltas(left, right);
        if (deltas == null) {
            return null;
        }

        CompilationUnit compilationUnit = getCompilationUnitFromFile(left);
        if (compilationUnit == null) {
            return null;
        }
        String className = compilationUnit.getPackage().getName().getFullyQualifiedName() + "."
                + FileUtils.getFileNameWithoutExtension(left.getName());

        Map<Integer, TYPE> fineDeltas = new HashMap<>();
        Map<Integer, Integer> sortedInsertions = computeDeltasAndInsertedLines(deltas, fineDeltas);

        List<ChangeWrapper> lines = getChangeWrappers(left, right, className, deltas, fineDeltas, sortedInsertions);
        if (lines == null) {
            return null;
        }

//		List<Integer> allDeltaPositions = getLinesFromDeltas(deltas);
        updateChangeWrappersWithDeltas(
                className, new FileToStringListReader().submit(left.toPath()).getResult(), lines, fineDeltas);

        if (removeNoise) {
            removeChangesWithNoDeltaLines(lines);
        }

        if (removeNonChanges) {
            removeChangesWithType(lines, ModificationType.NO_CHANGE);
        }

        Collections.sort(lines);
        return lines;
    }

    /**
     * Returns the list of changes relevant to the given source code range.
     *
     * @param filePath           the file under consideration
     * @param start              the beginning line
     * @param end                the ending line
     * @param ignoreRefactorings whether to ignore changes that are refactorings
     * @param changesMap         the map of all existing changes
     * @return list of changes relevant to the given range; {@code null} if no changes match
     */
    public static List<ChangeWrapper> getModifications(String filePath, int start, int end,
                                                       boolean ignoreRefactorings, Map<String, List<ChangeWrapper>> changesMap) {
        //see if the respective file was changed
        List<ChangeWrapper> changes = changesMap.get(filePath);
        return getModifications(start, end, ignoreRefactorings, changes);
    }

    /**
     * Returns the list of changes relevant to the given source code range.
     *
     * @param start              the beginning line
     * @param end                the ending line
     * @param ignoreRefactorings whether to ignore changes that are refactorings
     * @param changes            a list of changes
     * @return list of changes relevant to the given block; {@code null} if no changes match
     */
    public static List<ChangeWrapper> getModifications(int start, int end,
                                                       boolean ignoreRefactorings, List<ChangeWrapper> changes) {
        List<ChangeWrapper> list = null;
        if (changes != null) {
            for (ChangeWrapper change : changes) {

                if (ignoreRefactorings) {
                    //no semantic change like changes to a comment or something like that? then proceed...
                    if (change.getModificationType() == ModificationType.NO_SEMANTIC_CHANGE) {
                        continue;
                    }
                    //no change at all?...
                    if (change.getModificationType() == ModificationType.NO_CHANGE) {
                        continue;
                    }
                }

                List<Integer> deltas = change.getIncludedDeltas();
                if (deltas == null) {
                    continue;
                }

                //is the ranked block part of a changed statement?
                for (int deltaLine : change.getIncludedDeltas()) {
                    if (start <= deltaLine && deltaLine <= end) {
                        if (list == null) {
                            list = new ArrayList<>(1);
                        }
                        list.add(change);
                        break;
                    }
                }
            }
        }
        return list;
    }

    public static void removeChangesWithNoDeltaLines(List<ChangeWrapper> changes) {
        changes.removeIf(element -> element.getIncludedDeltas() == null || element.getIncludedDeltas().isEmpty());
    }

    public static void removeChangesWithType(List<ChangeWrapper> changes, ModificationType typeToRemove) {
        changes.removeIf(element -> element.getModificationType() == typeToRemove);
    }

    public static Set<Integer> getAllChangeDeltas(List<ChangeWrapper> changeWrappers) {
        Set<Integer> result = new HashSet<>();
        for (ChangeWrapper change : changeWrappers) {
            result.addAll(change.getIncludedDeltas());
        }
        return result;
    }

    public static ModificationType getMostImportantType(List<ChangeWrapper> changes) {
        EnumSet<ChangeWrapper.ModificationType> types = getModificationTypes(changes);
        if (types.contains(ModificationType.CHANGE)) {
            return ModificationType.CHANGE;
        } else if (types.contains(ModificationType.DELETE)) {
            return ModificationType.DELETE;
        } else if (types.contains(ModificationType.INSERT)) {
            return ModificationType.INSERT;
        } else if (types.contains(ModificationType.NO_SEMANTIC_CHANGE)) {
            return ModificationType.NO_SEMANTIC_CHANGE;
        } else {
            return ModificationType.NO_CHANGE;
        }
    }

    public static SignificanceLevel getHighestSignificanceLevel(List<ChangeWrapper> changes) {
        SignificanceLevel significance = SignificanceLevel.NONE;
        for (ChangeWrapper change : changes) {
            if (change.getSignificance().value() > significance.value()) {
                significance = change.getSignificance();
            }
        }
        return significance;
    }

    public static EnumSet<ChangeWrapper.ModificationType> getModificationTypes(List<ChangeWrapper> changes) {
        EnumSet<ChangeWrapper.ModificationType> set = EnumSet.noneOf(ChangeWrapper.ModificationType.class);
        for (ChangeWrapper change : changes) {
            if (change.getModificationType() == ModificationType.INSERT) {
                set.add(ModificationType.INSERT);
                break;
            }
        }
        for (ChangeWrapper change : changes) {
            if (change.getModificationType() == ModificationType.CHANGE) {
                set.add(ModificationType.CHANGE);
                break;
            }
        }
        for (ChangeWrapper change : changes) {
            if (change.getModificationType() == ModificationType.DELETE) {
                set.add(ModificationType.DELETE);
                break;
            }
        }
        return set;
    }

    private static Map<Integer, Integer> computeDeltasAndInsertedLines(List<Delta<String>> deltas, Map<Integer, TYPE> fineDeltas) {
        Map<Integer, Integer> linesInserted = new HashMap<>();
        for (Delta<String> delta : deltas) {
            int pos = delta.getOriginal().getPosition() + 1; // == lineNumber-1
            // + 1
//			Log.out(ChangeChecker.class, "delta: " + (pos) + 
//					(delta.getType() == TYPE.INSERT ? 
//							", insert + " + delta.getRevised().getLines().size() : 
//								(delta.getType() == TYPE.CHANGE ?
//										", change + " + (delta.getRevised().getLines().size() - delta.getOriginal().getLines().size()) :
//											", delete - " + (delta.getOriginal().getLines().size()))));

//			 Log.out(ChangeChecker.class, "" + pos + ", " + delta.getRevised().getLines().size());
//			 for (String line : delta.getRevised().getLines()) {
//				 Log.out(ChangeChecker.class, "\t" + line);
//			 }

            // we insert lines AFTER the specified position!
            if (delta.getType() == TYPE.INSERT) {
                linesInserted.put(pos - 1, delta.getRevised().getLines().size());
                fineDeltas.put(pos, TYPE.INSERT);
            } else if (delta.getType() == TYPE.CHANGE) {
                //TODO: create an insert delta the line after the "real" changes?
                linesInserted.put(pos - 1 + delta.getOriginal().getLines().size(),
                        delta.getRevised().getLines().size() - delta.getOriginal().getLines().size());
                for (int i = 0; i < delta.getOriginal().getLines().size(); ++i) {
                    fineDeltas.put(pos + i, TYPE.CHANGE);
                }
            } else { //TODO: deletes?
                linesInserted.put(pos - 1, -delta.getOriginal().getLines().size());
                for (int i = 0; i < delta.getOriginal().getLines().size(); ++i) {
                    fineDeltas.put(pos + i, TYPE.DELETE);
                }
            }
        }

        return Misc.sortByKey(linesInserted);
    }

    private static List<ChangeWrapper> getChangeWrappers(File left, File right, String className,
                                                         List<Delta<String>> deltas, Map<Integer, TYPE> fineDeltas, Map<Integer, Integer> sortedInsertions) {
        List<SourceCodeChange> changes = getChangesWithChangeDistiller(left, right);

        CompilationUnit compilationUnit = getCompilationUnitFromFile(left);
        if (compilationUnit == null) {
            return null;
        }

        CompilationUnit compilationUnitRevised = getCompilationUnitFromFile(right);
        if (compilationUnitRevised == null) {
            return null;
        }

//		List<SourceCodeEntity> inserts = new ArrayList<>();
//		List<SourceCodeEntity> updates = new ArrayList<>();
//		for (SourceCodeChange change : changes) {
//			if (change instanceof Insert) {
//				inserts.add(change.getChangedEntity());
//			} if (change instanceof Move) {
//				inserts.add(((Move) change).getNewEntity());
//				updates.add(change.getChangedEntity());
//			} else {
//				updates.add(change.getChangedEntity());
//			}
//		}

        return getChangeWrappers(
                changes, sortedInsertions, className, left, compilationUnit, right, compilationUnitRevised);
    }

    private static List<ChangeWrapper> getChangeWrappers(List<SourceCodeChange> changes,
                                                         Map<Integer, Integer> sortedInsertions, String className, File left, CompilationUnit compilationUnit,
                                                         File right, CompilationUnit compilationUnitRevised) {
        List<String> leftLines = new FileToStringListReader().submit(left.toPath()).getResult();
        List<String> rightLines = new FileToStringListReader().submit(right.toPath()).getResult();

        List<ChangeWrapper> lines = new ArrayList<>();
        if (changes != null) {
            for (SourceCodeChange change : changes) {

                // see Javadocs for more information
                SourceCodeEntity parent = change.getParentEntity();
                SourceCodeEntity entity = change.getChangedEntity();

                ChangeType type = change.getChangeType();

                int parentStart = compilationUnit.getLineNumber(parent.getStartPosition());
                int parentEnd = compilationUnit.getLineNumber(parent.getEndPosition());
                int start = compilationUnit.getLineNumber(entity.getStartPosition());
                int end = compilationUnit.getLineNumber(entity.getEndPosition());

                if (change instanceof Insert) {
                    // Insert insert = (Insert) change;

                    // what if parent was inserted?
                    if (compilationUnit.getLineNumber(parentStart) < 0) {
                        //TODO skip if parent was inserted?
                        continue;
//						parentStart = compilationUnitRevised.getLineNumber(parent.getStartPosition());
//						// if inside of a comment or at a blank line, get the nearest actual line
//						// (necessary to skip possible attached comments)
//						parentStart = getNearestActualLineAfterPos(rightLines, parentStart, true);
//						parentEnd = compilationUnitRevised.getLineNumber(parent.getEndPosition());
//						int linesInsertedBefore = computeInsertedLineCount(sortedInsertions, parentStart); //TODO check for correctness
//						parentStart -= linesInsertedBefore;
//						parentEnd -= linesInsertedBefore;
                    }

                    addInsert(
                            className, compilationUnitRevised, sortedInsertions, leftLines, rightLines, lines, entity,
                            change, type, parentStart, parentEnd);

                } else if (change instanceof Move) {
                    Move move = (Move) change;

                    addChangeOrDelete(
                            className, compilationUnitRevised, sortedInsertions, leftLines, rightLines, lines, entity,
                            change, type, parentStart, parentEnd, start, end, ModificationType.DELETE);

                    parent = move.getNewParentEntity();
                    entity = move.getNewEntity();

                    parentStart = compilationUnitRevised.getLineNumber(parent.getStartPosition());
                    parentEnd = compilationUnitRevised.getLineNumber(parent.getEndPosition());
                    int linesInsertedCount = computeInsertedLineCount(sortedInsertions, parentStart);
                    parentStart -= linesInsertedCount;
                    parentEnd -= linesInsertedCount;

                    addInsert(
                            className, compilationUnitRevised, sortedInsertions, leftLines, rightLines, lines, entity,
                            change, type, parentStart, parentEnd);

                } else if (change instanceof Update) {
                    // Update update = (Update) change;

                    // what if parent was inserted?
                    if (compilationUnit.getLineNumber(parentStart) < 0) {
                        //TODO skip if parent was inserted?
                        continue;
//						parentStart = compilationUnitRevised.getLineNumber(parent.getStartPosition());
//						parentEnd = compilationUnitRevised.getLineNumber(parent.getEndPosition());
//						int linesInsertedBefore = computeInsertedLineCount(sortedInsertions, parentStart);
//						parentStart -= linesInsertedBefore;
//						parentEnd -= linesInsertedBefore;
                    }

                    addChangeOrDelete(
                            className, compilationUnitRevised, sortedInsertions, leftLines, rightLines, lines, entity,
                            change, type, parentStart, parentEnd, start, end, ModificationType.CHANGE);

                } else if (change instanceof Delete) {
                    // Delete delete = (Delete) change;

                    addChangeOrDelete(
                            className, compilationUnitRevised, sortedInsertions, leftLines, rightLines, lines, entity,
                            change, type, parentStart, parentEnd, start, end, ModificationType.DELETE);

                } else {

                    lines.add(
                            new ChangeWrapper(className, parentStart, parentEnd, start, end, entity.getType(),
                                    change.getChangeType(), change.getSignificanceLevel(), getModificationType(type, ModificationType.NO_SEMANTIC_CHANGE)));

                }
            }
        }

        return lines;
    }

    private static void addChangeOrDelete(String className, CompilationUnit compilationUnitRevised,
                                          Map<Integer, Integer> sortedInsertions, List<String> leftLines, List<String> rightLines,
                                          List<ChangeWrapper> lines, SourceCodeEntity entity, SourceCodeChange change, ChangeType type,
                                          int parentStart, int parentEnd, int start, int end, ModificationType modificationType) {
        // if inside of a comment or at a blank line, get the nearest actual line
        // (necessary to skip possible attached comments)
        start = getNearestActualLineAfterOrBeforePos(leftLines, start, true);

        switch (type) {
            case METHOD_RENAMING:
            case CLASS_RENAMING:
                int line = getNextLineContainingChar(leftLines, start, '{');
                if (line != -1) {
                    end = line;
                }
                break;
            default:
                break;
        }

        ModificationType resultModificationType = ModificationType.NO_CHANGE;

        int linesInsertedCount = computeLinesToBeInsertedInRight(sortedInsertions, start);
        for (int i = start; i <= end; ++i) {
//			Log.out(null, "%d, %d", i, i+linesInsertedCount);
            if (!lineIdentical(leftLines, rightLines, i, linesInsertedCount)) {
                resultModificationType = modificationType;
                break;
            }
        }

        lines.add(
                new ChangeWrapper(className, parentStart, parentEnd, start, end, entity.getType(),
                        change.getChangeType(), change.getSignificanceLevel(), getModificationType(type, resultModificationType)));
    }

    private static void addInsert(String className, CompilationUnit compilationUnitRevised,
                                  Map<Integer, Integer> sortedInsertions, List<String> leftLines, List<String> rightLines,
                                  List<ChangeWrapper> lines, SourceCodeEntity entity, SourceCodeChange change, ChangeType type,
                                  int parentStart, int parentEnd) {
        int start = compilationUnitRevised.getLineNumber(entity.getStartPosition());
        // if inside of a comment or at a blank line, get the nearest actual line
        // (necessary to skip possible attached comments)
        start = getNearestActualLineAfterOrBeforePos(rightLines, start, true);
//		end = compilationUnitRevised.getLineNumber(entity.getEndPosition());
        int linesInsertedCount = computeInsertedLineCount(sortedInsertions, start);
        int linesInsertedCountBefore = computeInsertedLineCount(sortedInsertions, start - 1);
        boolean sameLineInsert = linesInsertedCount == linesInsertedCountBefore;
        start -= linesInsertedCount;
        // start is now the line BEFORE the inserted element, if it is inserted after the line
        // OR the actual line of the element, if it was only a partly insertion
        // inserted elements should only correspond to one line in
        // the original source code
        int end = start;

//		if (sameLineInsert) {
//			Log.out(ChangeChecker.class, "%d, %d same line + %d...", start, start + linesInsertedCount, linesInsertedCount);
//		} else {
//			Log.out(ChangeChecker.class, "%d, %d not same line + %d...", start, start + linesInsertedCount, linesInsertedCount);
//		}

        ModificationType modificationType = ModificationType.INSERT;
        switch (type) {
            case PARAMETER_INSERT:
            case RETURN_TYPE_INSERT:
            case PARENT_INTERFACE_INSERT:
                modificationType = ModificationType.CHANGE;
                break;
            default:
                break;
        }

        ModificationType resultModificationType = ModificationType.NO_CHANGE;

        int linesInserted = computeLinesToBeInsertedInRight(sortedInsertions, start);
        for (int i = sameLineInsert ? start : start + 1;
             i + linesInserted <= compilationUnitRevised.getLineNumber(entity.getEndPosition()); ++i) {
//			Log.out(null, "%d, %d", i, i+linesInserted);
            if (!lineIdentical(leftLines, rightLines, i, linesInserted)) {
                resultModificationType = modificationType;
                break;
            }
        }

        ChangeWrapper changeWrapper;
        // check if the whole line or only a part was inserted
        if (sameLineInsert && !firstCharDifferent(leftLines, rightLines, start, linesInsertedCount)) {
//			end = start;
            // if inside of a comment or at a blank line, get the nearest actual line
            end = getNearestActualLineAfterOrBeforePos(leftLines, start, true);

            if (lineIdentical(leftLines, rightLines, start, linesInsertedCount)) {
                resultModificationType = ModificationType.NO_CHANGE;
            }

            changeWrapper = new ChangeWrapper(className, parentStart, parentEnd, start, end, entity.getType(),
                    change.getChangeType(), change.getSignificanceLevel(), getModificationType(type, resultModificationType));
            changeWrapper.addDelta(start);
        } else {
            // skip comments and whitespaces before the line
            start = getNearestActualLineBeforeOrAfterPos(leftLines, start, true);

//			end = start;
            // if inside of a comment or at a blank line, get the nearest actual line
            end = getNearestActualLineAfterOrBeforePos(leftLines, start + 1, true);

            changeWrapper = new ChangeWrapper(className, parentStart, parentEnd, start, end, entity.getType(),
                    change.getChangeType(), change.getSignificanceLevel(), getModificationType(type, resultModificationType));
            if (resultModificationType == ModificationType.CHANGE) {
                changeWrapper.addDelta(start);
            } else {
                changeWrapper.addDelta(end);
            }
        }

        lines.add(changeWrapper);
    }

    private static boolean firstCharDifferent(List<String> leftLines, List<String> rightLines, int start,
                                              int linesInsertedBefore) {
        String leftString = Misc.replaceWhitespacesInString(leftLines.get(start - 1), "");
        String rightString = Misc.replaceWhitespacesInString(rightLines.get(start - 1 + linesInsertedBefore), "");
//		Log.out(null, leftString);
//		Log.out(null, rightString);
        // check if the beginning of the line changed
        // => the insertion affects the whole line (probably...)
        if (leftString.length() > 0 && rightString.length() > 0) {
            return leftString.charAt(0) != rightString.charAt(0);
        } else return leftString.length() == 0 && rightString.length() == 0;
    }

    private static boolean lineIdentical(List<String> leftLines, List<String> rightLines, int start,
                                         int linesInsertedBefore) {
        String leftString = Misc.replaceWhitespacesInString(leftLines.get(start - 1), "");
        String rightString = Misc.replaceWhitespacesInString(rightLines.get(start - 1 + linesInsertedBefore), "");
//		Log.out(null, leftString);
//		Log.out(null, rightString);
        return leftString.equals(rightString);
    }

    private static int computeInsertedLineCount(Map<Integer, Integer> sortedInsertions, int lineInRevisedVersion) {
        int linesInsertedBefore = 0;
        for (Entry<Integer, Integer> entry : sortedInsertions.entrySet()) {
            if (entry.getKey() + linesInsertedBefore + entry.getValue() <= lineInRevisedVersion) {
                // mapped line no. + already inserted lines + lines inserted after mapped line <= specified line no.
                linesInsertedBefore += entry.getValue();
            } else if (entry.getKey() + linesInsertedBefore < lineInRevisedVersion) {
                // mapped line no. + already inserted lines < specified line no.
                linesInsertedBefore += lineInRevisedVersion - (entry.getKey() + linesInsertedBefore);
            } else {
                // mapped line no. + already inserted lines >= specified line no.
                break;
            }
        }
        return linesInsertedBefore;
    }

    private static int computeLinesToBeInsertedInRight(Map<Integer, Integer> sortedInsertions, int lineInOriginalVersion) {
        int linesInserted = 0;
        for (Entry<Integer, Integer> entry : sortedInsertions.entrySet()) {
            if (entry.getKey() < lineInOriginalVersion) {
                linesInserted += entry.getValue();
            } else {
                break;
            }
        }
        return linesInserted;
    }

    private static CompilationUnit getCompilationUnitFromFile(File left) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);

        // Parse the class as a compilation unit.
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        try {
            // give your java source here as char array
            parser.setSource(FileUtils.readFile2CharArray(left.toString()));
        } catch (IOException e) {
            Log.err(ChangeChecker.class, e, "Could not parse source file '%s'.", left);
            return null;
        }
        parser.setResolveBindings(true);

        // Return the compiled class as a compilation unit
        return (CompilationUnit) parser.createAST(null);
    }

    // private static boolean parentIsAChangeOrInsideOfChange(SourceCodeEntity
    // parent, List<SourceCodeChange> changes) {
    // int start = parent.getStartPosition();
    // int end = parent.getEndPosition();
    // for (SourceCodeChange change : changes) {
    // if (change.getChangedEntity().getStartPosition() <= start
    // && change.getChangedEntity().getEndPosition() >= end) {
    // return true;
    // }
    // }
    // return false;
    // }


    private static List<SourceCodeChange> getChangesWithChangeDistiller(File left, File right) {
        FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
        try {
            distiller.extractClassifiedSourceCodeChanges(left, right);
        } catch (Exception e) {
            /*
             * An exception most likely indicates a bug in ChangeDistiller.
             * Please file a bug report at
             * https://bitbucket.org/sealuzh/tools-changedistiller/issues and
             * attach the full stack trace along with the two files that you
             * tried to distill.
             */
            Log.err(ChangeChecker.class, "Error while change distilling. " + e.getMessage());
            return null;
        }

        return distiller.getSourceCodeChanges();
    }

    private static List<Delta<String>> getDeltas(File left, File right) {
        List<String> leftList = new FileToStringListReader().submit(left.toPath()).getResult();
        List<String> rightList = new FileToStringListReader().submit(right.toPath()).getResult();

        Patch<String> patch = DiffUtils.diff(leftList, rightList);

        List<Delta<String>> deltas = patch.getDeltas();
        if (deltas == null) {
            Log.err(ChangeChecker.class, "Error getting deltas.");
            return null;
        }

        return deltas;
    }

    private static void updateChangeWrappersWithDeltas(String className, List<String> lines,
                                                       List<ChangeWrapper> changes, Map<Integer, TYPE> fineDeltas) {
        List<Integer> usedDeltas = new ArrayList<>();
        for (ChangeWrapper change : changes) {

//			if (change.getModificationType() == ModificationType.INSERT) {
//				continue;
//			}

            List<Integer> matchingDeltas = getIncludedDeltas(lines, changes, fineDeltas, change);

            if (matchingDeltas.isEmpty()) {
                continue;
            } else {
                usedDeltas.addAll(matchingDeltas);
            }

            if (change.getIncludedDeltas() != null) {
                continue;
            }

            change.setDeltas(matchingDeltas);
        }

        List<Integer> unusedDeltas = new ArrayList<>();
        for (Entry<Integer, TYPE> entry : fineDeltas.entrySet()) {
//			if (entry.getValue() == TYPE.INSERT) {
//				continue;
//			}
            if (!usedDeltas.contains(entry.getKey())) {
                unusedDeltas.add(entry.getKey());
            }
        }

        if (!unusedDeltas.isEmpty()) {
            List<Integer> matchingDeltas = new ArrayList<>();
            for (int pos : unusedDeltas) {
                //only skip whitespaces here
                matchingDeltas.add(getNearestActualLineAfterOrBeforePos(lines, pos, false));
            }
            changes.add(
                    new ChangeWrapper(className, 1, lines.size(), 1, lines.size(), matchingDeltas, null, null,
                            SignificanceLevel.NONE, ModificationType.NO_SEMANTIC_CHANGE));
        }
    }

    private static List<Integer> getIncludedDeltas(List<String> lines, List<ChangeWrapper> changes,
                                                   Map<Integer, TYPE> fineDeltas, ChangeWrapper change) {
        List<Integer> matchingDeltas = new ArrayList<>();
        for (Entry<Integer, TYPE> entry : fineDeltas.entrySet()) {
//			if (entry.getValue() == TYPE.INSERT) {
//				continue;
//			}
            int lineNo = entry.getKey();
            if (change.getStart() <= lineNo && lineNo <= change.getEnd()) {
//				if (positionIsOnLowestLevel(changes, change, lineNo)) {
                // skip whitespaces and comment sections
                if (change.getModificationType() != ModificationType.NO_SEMANTIC_CHANGE &&
                        change.getModificationType() != ModificationType.NO_CHANGE) {
                    matchingDeltas.add(getNearestActualLineAfterOrBeforePos(lines, lineNo, true));
                } else {
                    matchingDeltas.add(lineNo);
                }

//					iterator.remove();
//				}
            }
        }
        return matchingDeltas;
    }

//	private static boolean positionIsOnLowestLevel(List<ChangeWrapper> changes, ChangeWrapper currentChange, int lineNo) {
//		SourceCodeEntity currentEntity = currentChange.getEntity();
//		for (ChangeWrapper change : changes) {
//			if (change == currentChange) {
//				continue;
//			}
//			
//			
//			// is the line in the change?
//			if (change.getStart() <= lineNo && lineNo <= change.getEnd()) {
//				SourceCodeEntity entity = change.getEntity();
//				if ((currentEntity.getStartPosition() < entity.getStartPosition() 
//						&& entity.getEndPosition() <= currentEntity.getEndPosition())
//						|| (currentEntity.getStartPosition() <= entity.getStartPosition() 
//								&& entity.getEndPosition() < currentEntity.getEndPosition())) {
//					return false;
//				}
//			}
////			// is the line in the change?
////			if (change.getStart() <= lineNo && lineNo <= change.getEnd()) {
////				if ((currentChange.getStart() < change.getStart() && change.getEnd() <= currentChange.getEnd())
////						|| (currentChange.getStart() <= change.getStart()
////								&& change.getEnd() < currentChange.getEnd())) {
////					return false;
////				}
////			}
//		}
//		return true;
//	}

    private static int getNextLineContainingChar(List<String> lines, int startLine, char c) {
        if (startLine < 1) {
            // set to the first line
            startLine = 1;
        }
        for (int pos = startLine - 1; pos < lines.size(); ++pos) {
            String line = lines.get(pos);
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) == c) {
                    return pos + 1;
                }
            }
        }
        // no actual line could be found...
        return -1;
    }

    private static int getNearestActualLineAfterOrBeforePos(List<String> lines,
                                                            int pos, boolean skipComments) {
        int result = getNearestActualLineAfterPos(lines, pos, skipComments);
        if (result != -1) {
            return result;
        } else {
            return getNearestActualLineBeforePos(lines, pos, skipComments);
        }
    }

    private static int getNearestActualLineBeforeOrAfterPos(List<String> lines,
                                                            int pos, boolean skipComments) {
        int result = getNearestActualLineBeforePos(lines, pos, skipComments);
        if (result != -1) {
            return result;
        } else {
            return getNearestActualLineAfterPos(lines, pos, skipComments);
        }
    }

    private static int getNearestActualLineAfterPos(List<String> lines,
                                                    int pos, boolean skipComments) {
//		Log.out(ChangeChecker.class, "original pos: %d", pos);
        //skip empty lines and comments...
        return getNextActualLine(lines, pos, isInsideOfCommentBlock(lines, pos), skipComments);
    }

    private static int getNearestActualLineBeforePos(List<String> lines,
                                                     int pos, boolean skipComments) {
//		Log.out(ChangeChecker.class, "original pos: %d", pos);
        //skip empty lines and comments...
        return getPreviousActualLine(lines, pos, isInsideOfCommentBlock(lines, pos + 1), skipComments);
    }

    private static boolean isInsideOfCommentBlock(List<String> lines, int lineNumber) {
        return isInsideOfCommentBlock(lines, lineNumber, 1, false);
    }

    private static boolean isInsideOfCommentBlock(List<String> lines, int lineNumber, int startLine,
                                                  boolean insideOfBlockComment) {
        if (startLine < 1) {
            return false;
        }
        boolean insideOfString = false;
        boolean readSlash = false;
        boolean readBackSlash = false;
        boolean readStar = false;
        int counter = 0;
        for (int pos = startLine - 1; pos < lines.size(); ++pos) {
            String line = lines.get(pos);
            ++counter;
            if (counter == lineNumber) {
                break;
//				// maybe inside a line comment?
//				if (line.matches("^[\\s]*//.*")) {
//					return true;
//				}
            }
            for (int i = 0; i < line.length(); ++i) {
                if (insideOfBlockComment) {
                    if (line.charAt(i) == '*') {
                        readStar = true;
                    } else if (readStar && line.charAt(i) == '/') {
                        readStar = false;
                        insideOfBlockComment = false;
                    } else {
                        readStar = false;
                    }
                } else if (insideOfString) {
                    if (line.charAt(i) == '\\') {
                        readBackSlash = true;
                    } else if (line.charAt(i) == '"' && !readBackSlash) {
                        insideOfString = false;
                        readBackSlash = false;
                    } else {
                        readBackSlash = false;
                    }
                } else if (readSlash) {
                    if (line.charAt(i) == '/') {
                        // line comment
                        readSlash = false;
                        break;
                    } else if (line.charAt(i) == '"') {
                        insideOfString = true;
                        readSlash = false;
                    } else if (line.charAt(i) == '*') {
                        insideOfBlockComment = true;
                        readSlash = false;
                    } else {
                        readSlash = false;
                    }
                } else if (line.charAt(i) == '/') {
                    readSlash = true;
                } else if (line.charAt(i) == '"') {
                    insideOfString = true;
                }
            }
        }
        return insideOfBlockComment;
    }

    private static int getNextActualLine(List<String> lines, int startLine,
                                         boolean insideOfBlockComment, boolean skipComments) {
        if (startLine < 1) {
            // set to the first line
            startLine = 1;
        }
        boolean readSlash = false;
        boolean readStar = false;
        for (int pos = startLine - 1; pos < lines.size(); ++pos) {
            String line = lines.get(pos);
            if (skipComments) {
                for (int i = 0; i < line.length(); ++i) {
                    if (insideOfBlockComment) {
                        if (line.charAt(i) == '*') {
                            readStar = true;
                        } else if (readStar && line.charAt(i) == '/') {
                            readStar = false;
                            insideOfBlockComment = false;
                        } else {
                            readStar = false;
                        }
                    } else if (readSlash) {
                        if (line.charAt(i) == '/') {
                            // line comment
                            readSlash = false;
                            break;
                        } else if (line.charAt(i) == '*') {
                            insideOfBlockComment = true;
                            readSlash = false;
                        } else {
                            return pos + 1;
                        }
                    } else if (line.charAt(i) == '/') {
                        readSlash = true;
                    } else if (Character.isWhitespace(line.charAt(i))) {
                        // skip whitespaces
                        continue;
                    } else {
                        return pos + 1;
                    }
                }
            } else {
                for (int i = 0; i < line.length(); ++i) {
                    if (Character.isWhitespace(line.charAt(i))) {
                        // skip whitespaces
                        continue;
                    } else {
                        return pos + 1;
                    }
                }
            }
        }
        // no actual line could be found...
        return -1;
    }

    private static int getPreviousActualLine(List<String> lines, int startLine,
                                             boolean insideOfBlockComment, boolean skipComments) {
        if (startLine < 1) {
            // no line number smaller than zero
            return -1;
        }
        if (startLine > lines.size()) {
            startLine = lines.size();
        }
        boolean readSlash = false;
        boolean readStar = false;
        boolean readSomeOtherElements = false;
        for (int pos = startLine - 1; pos >= 0; --pos) {
            String line = lines.get(pos);
            if (skipComments) {
                for (int i = line.length() - 1; i >= 0; --i) {
                    if (insideOfBlockComment) {
                        if (line.charAt(i) == '*') {
                            readStar = true;
                        } else if (readStar && line.charAt(i) == '/') {
                            readStar = false;
                            insideOfBlockComment = false;
                        } else {
                            readStar = false;
                        }
                    } else if (readSlash) {
                        if (line.charAt(i) == '/') {
                            // line comment...
                            // elements right of the line comment are ignored
                            readSomeOtherElements = false;
                        } else if (line.charAt(i) == '*') {
                            // this does not necessarily have to be the end of a
                            // block comment, but usually, it is...
                            // something like 
                            // > double x = 5 *//
                            // >		4;
                            // will be falsely recognized as inside of a block comment...
                            insideOfBlockComment = true;
                            readSlash = false;
                        } else {
                            readSomeOtherElements = true;
                            readSlash = false;
                        }
                    } else if (line.charAt(i) == '/') {
                        readSlash = true;
                    } else if (Character.isWhitespace(line.charAt(i))) {
                        // skip whitespaces
                        continue;
                    } else {
                        readSomeOtherElements = true;
                    }
                }

                // this exists to cover line comments, which we normally only detect
                // after reading some other elements (reading from right to left)
                if (readSomeOtherElements) {
                    return pos + 1;
                }
            } else {
                for (int i = 0; i < line.length(); ++i) {
                    if (Character.isWhitespace(line.charAt(i))) {
                        // skip whitespaces
                        continue;
                    } else {
                        return pos + 1;
                    }
                }
            }
        }
        // no actual line could be found...
        return -1;
    }

    private static ModificationType getModificationType(ChangeType type, ModificationType oldType) {
        if (oldType == ModificationType.NO_CHANGE) {
            return oldType;
        }
        ModificationType modification_type = oldType;
        switch (type) {
            // case ADDITIONAL_CLASS:
            // case ADDITIONAL_FUNCTIONALITY:
            // case ADDITIONAL_OBJECT_STATE:
            // case ALTERNATIVE_PART_INSERT:
            // case PARAMETER_INSERT:
            // case PARENT_CLASS_INSERT:
            // case PARENT_INTERFACE_INSERT:
            // case RETURN_TYPE_INSERT:
            // case STATEMENT_INSERT:
            // modification_type = ModificationType.INSERT;
            // break;
            //
            // case ALTERNATIVE_PART_DELETE:
            // case PARAMETER_DELETE:
            // case PARENT_CLASS_DELETE:
            // case PARENT_INTERFACE_DELETE:
            // case REMOVED_CLASS:
            // case REMOVED_FUNCTIONALITY:
            // case REMOVED_OBJECT_STATE:
            // case RETURN_TYPE_DELETE:
            // case STATEMENT_DELETE:
            // modification_type = ModificationType.DELETE;
            // break;
            //
            // case UNCLASSIFIED_CHANGE:
            // case ATTRIBUTE_TYPE_CHANGE:
            // case CONDITION_EXPRESSION_CHANGE:
            // case DECREASING_ACCESSIBILITY_CHANGE:
            // case INCREASING_ACCESSIBILITY_CHANGE:
            // case PARAMETER_ORDERING_CHANGE:
            // case PARAMETER_TYPE_CHANGE:
            // case PARENT_CLASS_CHANGE:
            // case PARENT_INTERFACE_CHANGE:
            // case RETURN_TYPE_CHANGE:
            // case STATEMENT_ORDERING_CHANGE:
            // case STATEMENT_PARENT_CHANGE:
            // case STATEMENT_UPDATE:
            // modification_type = ModificationType.CHANGE;
            // break;

//		case STATEMENT_ORDERING_CHANGE:
            case STATEMENT_PARENT_CHANGE:
            case PARAMETER_ORDERING_CHANGE:

            case COMMENT_INSERT:
            case DOC_INSERT:
            case COMMENT_DELETE:
            case DOC_DELETE:
            case REMOVING_ATTRIBUTE_MODIFIABILITY:
            case REMOVING_CLASS_DERIVABILITY:
            case REMOVING_METHOD_OVERRIDABILITY:
//		case METHOD_RENAMING:
//		case CLASS_RENAMING:
//		case ATTRIBUTE_RENAMING:
//		case PARAMETER_RENAMING:
            case ADDING_ATTRIBUTE_MODIFIABILITY:
            case ADDING_CLASS_DERIVABILITY:
            case ADDING_METHOD_OVERRIDABILITY:
            case COMMENT_MOVE:
            case COMMENT_UPDATE:
            case DOC_UPDATE:
                modification_type = ModificationType.NO_SEMANTIC_CHANGE;
                break;
            default:
                // modification_type = ModificationType.NO_SEMANTIC_CHANGE;
                break;
        }
        return modification_type;
    }

}
