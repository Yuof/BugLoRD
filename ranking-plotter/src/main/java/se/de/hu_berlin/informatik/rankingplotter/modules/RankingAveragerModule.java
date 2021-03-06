package se.de.hu_berlin.informatik.rankingplotter.modules;

import se.de.hu_berlin.informatik.rankingplotter.plotter.Plotter.ParserStrategy;
import se.de.hu_berlin.informatik.rankingplotter.plotter.RankingFileWrapper;
import se.de.hu_berlin.informatik.rankingplotter.plotter.datatables.AveragePlotStatisticsCollection;
import se.de.hu_berlin.informatik.rankingplotter.plotter.datatables.AveragePlotStatisticsCollection.StatisticsCategories;
import se.de.hu_berlin.informatik.rankingplotter.plotter.datatables.StatisticsCollection;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Module that takes a {@link List} of {@link RankingFileWrapper} objects,  adds the data points
 * to the corresponding data tables, produces a label map and returns a {@link StatisticsCollection}.
 *
 * @author Simon Heiden
 */
public class RankingAveragerModule extends AbstractProcessor<RankingFileWrapper, AveragePlotStatisticsCollection> {

    //percentage -> project -> id -> ranking wrapper
    private Map<Double, Map<String, Map<Integer, RankingFileWrapper>>> percentageToProjectToBugToRanking;

    private Map<Double, RankingFileWrapper> averagedRankingsMap;

    private final String rankingIdentifier1;
    private final String rankingIdentifier2;

    /**
     * Creates a new {@link RankingAveragerModule} object with the given parameters.
     *
     * @param rankingIdentifier1 the first ranking identifier
     * @param rankingIdentifier2 the second ranking identifier
     */
    public RankingAveragerModule(String rankingIdentifier1, String rankingIdentifier2) {
        super();
        this.rankingIdentifier1 = rankingIdentifier1;
        this.rankingIdentifier2 = rankingIdentifier2;
        reset();
    }

    private void reset() {
        percentageToProjectToBugToRanking = new HashMap<>();
        averagedRankingsMap = new HashMap<>();
    }

    /* (non-Javadoc)
     * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
     */
    @Override
    public AveragePlotStatisticsCollection processItem(RankingFileWrapper item) {
        item.throwAwayRanking();

        //add the minimum rank of this item to the map
        percentageToProjectToBugToRanking
                .computeIfAbsent(item.getSBFLPercentage(), k -> new HashMap<>())
                .computeIfAbsent(item.getProject(), k -> new HashMap<>())
                .put(item.getBugId(), item);

        RankingFileWrapper averageHolder = averagedRankingsMap
                .computeIfAbsent(item.getSBFLPercentage(), k -> new RankingFileWrapper(
                        "", 0, null, item.getSBFLPercentage(), null, ParserStrategy.NO_CHANGE));

        updateValues(averageHolder, item);

        return null;
    }

    /* (non-Javadoc)
     * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#getResultFromCollectedItems()
     */
    public AveragePlotStatisticsCollection getResultFromCollectedItems() {

        AveragePlotStatisticsCollection tables = new AveragePlotStatisticsCollection(
                rankingIdentifier1 + "_" + rankingIdentifier2, percentageToProjectToBugToRanking);

        boolean addedSometing = false;
        //add the data points to the tables
        for (final RankingFileWrapper averagedRanking : Misc.sortByValueToValueList(averagedRankingsMap)) {
            addedSometing = true;
            double sbflPercentage = averagedRanking.getSBFLPercentage();

            tables.addValuePair(StatisticsCategories.HIT_AT_1, sbflPercentage,
                    Double.valueOf(averagedRanking.getHitAtXMap().get(1)));
            tables.addValuePair(StatisticsCategories.HIT_AT_5, sbflPercentage,
                    Double.valueOf(averagedRanking.getHitAtXMap().get(5)));
            tables.addValuePair(StatisticsCategories.HIT_AT_10, sbflPercentage,
                    Double.valueOf(averagedRanking.getHitAtXMap().get(10)));
            tables.addValuePair(StatisticsCategories.HIT_AT_20, sbflPercentage,
                    Double.valueOf(averagedRanking.getHitAtXMap().get(20)));
            tables.addValuePair(StatisticsCategories.HIT_AT_30, sbflPercentage,
                    Double.valueOf(averagedRanking.getHitAtXMap().get(30)));
            tables.addValuePair(StatisticsCategories.HIT_AT_50, sbflPercentage,
                    Double.valueOf(averagedRanking.getHitAtXMap().get(50)));
            tables.addValuePair(StatisticsCategories.HIT_AT_100, sbflPercentage,
                    Double.valueOf(averagedRanking.getHitAtXMap().get(100)));

            if (averagedRanking.getMinRankSum() > 0) {
                tables.addValuePair(StatisticsCategories.MEAN_FIRST_RANK, sbflPercentage,
                        averagedRanking.getMeanFirstRank());
            }
            if (averagedRanking.getAll() > 0) {
                tables.addValuePair(StatisticsCategories.MEAN_RANK, sbflPercentage,
                        averagedRanking.getMeanRank());
            }
            if (averagedRanking.getAllMinRanks() != null) {
                tables.addValuePair(StatisticsCategories.MEDIAN_FIRST_RANK, sbflPercentage,
                        averagedRanking.getMedianFirstRank());
            }
            if (averagedRanking.getAllRanks() != null) {
                tables.addValuePair(StatisticsCategories.MEDIAN_RANK, sbflPercentage,
                        averagedRanking.getMedianRank());
            }


            if (averagedRanking.getModChanges() > 0) {
                tables.addValuePair(StatisticsCategories.MOD_CHANGE, sbflPercentage,
                        averagedRanking.getModChangesAverage());
            }

            if (averagedRanking.getModDeletes() > 0) {
                tables.addValuePair(StatisticsCategories.MOD_DELETE, sbflPercentage,
                        averagedRanking.getModDeletesAverage());
            }

            if (averagedRanking.getModUnknowns() > 0) {
                tables.addValuePair(StatisticsCategories.MOD_UNKNOWN, sbflPercentage,
                        averagedRanking.getModUnknownsAverage());
            }

            if (averagedRanking.getModInserts() > 0) {
                tables.addValuePair(StatisticsCategories.MOD_INSERT, sbflPercentage,
                        averagedRanking.getModInsertsAverage());
            }

            tables.addValuePair(StatisticsCategories.ALL, sbflPercentage,
                    (double) averagedRanking.getAll());

        }

        reset();

        if (addedSometing) {
            return tables;
        } else {
            return null;
        }
    }

    private static void updateValues(RankingFileWrapper ar, RankingFileWrapper item) {
        if (item.getMinRank() != null) {
            ar.addToMinRankSum(item.getMinRank());
            ar.addToMinRankCount(1);
        }

        for (Entry<Integer, Integer> entry : item.getHitAtXMap().entrySet()) {
            int key = entry.getKey();
            ar.getHitAtXMap().put(key, entry.getValue() + ar.getHitAtXMap().get(key));
        }

        ar.addToAllRankings(item.getChangedLinesRankings());

        ar.addToAllSum(item.getAllSum());
        ar.addToAll(item.getAll());


        ar.addToModChangesSum(item.getModChangesSum());
        ar.addToModChanges(item.getModChanges());

        ar.addToModDeletesSum(item.getModDeletesSum());
        ar.addToModDeletes(item.getModDeletes());

        ar.addToModInsertsSum(item.getModInsertsSum());
        ar.addToModInserts(item.getModInserts());

        ar.addToModUnknownsSum(item.getModUnknownsSum());
        ar.addToModUnknowns(item.getModUnknowns());
    }

}
