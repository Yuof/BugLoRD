package se.de.hu_berlin.informatik.benchmark.ranking;

import java.util.Map;

/**
 * Holds all ranking information for a node.
 * @param <T>
 * the type of the node
 */
public class SimpleRankingMetric<T> implements RankingMetric<T> {

    /** The node the ranking metris belong to */
    private final T node;
    /** the best possible ranking of the node */
    private final int bestRanking;
    /** the worst possible ranking of the node */
    private final int worstRanking;
    /** The suspiciousness of the node */
    private final double suspiciousness;
    
    /** Holds the nodes with their corresponding suspiciousness */
    private final Map<T, Double> nodes;

    /**
     * Create the ranking metric for a certain node.
     *
     * @param node
     *            The node the ranking metric belongs to
     * @param bestRanking
     *            the best possible ranking of the node
     * @param worstRanking
     *            the worst possible ranking of the node
     * @param suspiciousness
     *            The suspiciousness of the node
     * @param nodes
     * 			  the nodes with their corresponding suspiciousness
     */
    protected SimpleRankingMetric(final T node, final int bestRanking, final int worstRanking,
            final double suspiciousness, Map<T, Double> nodes) {
        this.node = node;
        this.bestRanking = bestRanking;
        this.worstRanking = worstRanking;
        this.suspiciousness = suspiciousness;
        this.nodes = nodes;
    }

    /**
     * Returns the best possible ranking of the node
     *
     * @return bestRanking
     */
    @Override
    public int getBestRanking() {
        return bestRanking;
    }

    /**
     * Returns the worst possible ranking of the node
     *
     * @return worstRanking
     */
    @Override
    public int getWorstRanking() {
        return worstRanking;
    }

    /**
     * Returns the minimum wasted effort that is necessary to find this node with the current ranking.
     *
     * @return minWastedEffort
     */
    @Override
    public double getMinWastedEffort() {
        return Double.valueOf(bestRanking - 1) / Double.valueOf(nodes.size());
    }

    /**
     * Returns the maximum wasted effort that is necessary to find this node with the current ranking.
     *
     * @return maxWastedEffort
     */
    @Override
    public double getMaxWastedEffort() {
        return Double.valueOf(worstRanking - 1) / Double.valueOf(nodes.size());
    }

    /**
     * Returns the suspiciousness of the node.
     *
     * @return suspiciousness
     */
    public double getSuspiciousness() {
        return suspiciousness;
    }

	@Override
	public T getElement() {
		return node;
	}

	@Override
	public double getRankingValue() {
		return getSuspiciousness();
	}


}