/**
 * 
 */
package se.de.hu_berlin.informatik.rankingplotter.modules;

import java.util.List;

import se.de.hu_berlin.informatik.changechecker.ChangeWrapper;
import se.de.hu_berlin.informatik.rankingplotter.plotter.RankingFileWrapper;
import se.de.hu_berlin.informatik.rankingplotter.plotter.datatables.SinglePlotStatisticsCollection;
import se.de.hu_berlin.informatik.rankingplotter.plotter.datatables.SinglePlotStatisticsCollection.StatisticsCategories;
import se.de.hu_berlin.informatik.rankingplotter.plotter.datatables.StatisticsCollection;
import se.de.hu_berlin.informatik.stardust.localizer.SourceCodeBlock;
import se.de.hu_berlin.informatik.utils.experiments.ranking.RankingMetric;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Module that takes a {@link List} of {@link RankingFileWrapper} objects, adds the data points
 * to the corresponding data tables and returns a {@link StatisticsCollection}.
 * 
 * @author Simon Heiden
 */
public class DataAdderModule extends AbstractProcessor<List<RankingFileWrapper>, SinglePlotStatisticsCollection> {

	private final String localizer;
	
	/**
	 * Creates a new {@link DataAdderModule} object with the given parameters.
	 * @param localizer
	 * the current localizer
	 */
	public DataAdderModule(String localizer) {
		super();
		this.localizer = localizer;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public SinglePlotStatisticsCollection processItem(List<RankingFileWrapper> rankingFiles) {
		//sort the ranking wrappers
		rankingFiles.sort(null);
		
		//add data and labels for plotting
		SinglePlotStatisticsCollection tables = new SinglePlotStatisticsCollection(localizer);

		for (final RankingFileWrapper item : rankingFiles) {
			double sbflPercentage = item.getSBFLPercentage();

			if (item.getRanking() != null) {
				for (SourceCodeBlock entry : item.getRanking().getMarkedElements()) {
					RankingMetric<SourceCodeBlock> metric = item.getRanking().getRankingMetrics(entry);
					List<ChangeWrapper> changes = item.getRanking().getMarker(entry);
					
					for (ChangeWrapper change : changes) {
						StatisticsCategories category;
						switch (change.getSignificance()) {
						case CRUCIAL:
							category = StatisticsCategories.SIGNIFICANCE_CRUCIAL;
							break;
						case HIGH:
							category = StatisticsCategories.SIGNIFICANCE_HIGH;
							break;
						case LOW:
							category = StatisticsCategories.SIGNIFICANCE_LOW;
							break;
						case MEDIUM:
							category = StatisticsCategories.SIGNIFICANCE_MEDIUM;
							break;
						case NONE:
							category = StatisticsCategories.SIGNIFICANCE_NONE;
							break;
						default:
							category = StatisticsCategories.UNKNOWN;
							break;
						}

						tables.addValuePair(category, sbflPercentage, Double.valueOf(metric.getRanking()), 
								Double.valueOf(metric.getBestRanking()), Double.valueOf(metric.getWorstRanking()));

						switch (change.getModificationType()) {
						case CHANGE:
							category = StatisticsCategories.MOD_CHANGE;
							break;
						case INSERT:
							category = StatisticsCategories.MOD_INSERT;
							break;
						case DELETE:
							category = StatisticsCategories.MOD_DELETE;
							break;
						case NO_SEMANTIC_CHANGE:
							category = StatisticsCategories.MOD_UNKNOWN;
							break;
						default:
							category = StatisticsCategories.UNKNOWN;
							break;
						}

						tables.addValuePair(category, sbflPercentage, Double.valueOf(metric.getRanking()), 
								Double.valueOf(metric.getBestRanking()), Double.valueOf(metric.getWorstRanking()));

						tables.addValuePair(StatisticsCategories.ALL, sbflPercentage, Double.valueOf(metric.getRanking()), 
								Double.valueOf(metric.getBestRanking()), Double.valueOf(metric.getWorstRanking()));
					}
				}
				
				item.throwAwayRanking();
			}
		}

		return tables;
	}

	
}
