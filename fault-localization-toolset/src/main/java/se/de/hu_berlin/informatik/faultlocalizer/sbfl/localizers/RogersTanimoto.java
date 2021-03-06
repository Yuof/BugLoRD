/*
 * This file is part of the "STARDUST" project.
 *
 * (c) Fabian Keller <hello@fabian-keller.de>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package se.de.hu_berlin.informatik.faultlocalizer.sbfl.localizers;

import se.de.hu_berlin.informatik.faultlocalizer.sbfl.AbstractFaultLocalizer;
import se.de.hu_berlin.informatik.spectra.core.ComputationStrategies;
import se.de.hu_berlin.informatik.spectra.core.INode;

/**
 * Rogers-Tanimoto fault localizer $\frac{\EF+\NP}{\EF+\NP+2(\NF+\EP)}$
 *
 * @param <T> type used to identify nodes in the system
 */
public class RogersTanimoto<T> extends AbstractFaultLocalizer<T> {

    /**
     * Create fault localizer
     */
    public RogersTanimoto() {
        super();
    }

    @Override
    public double suspiciousness(final INode<T> node, ComputationStrategies strategy) {
        double numerator = node.getEF(strategy) + node.getNP(strategy);
        if (numerator == 0) {
            return 0;
        }
        return numerator / (node.getEF(strategy) + node.getNP(strategy) + 2.0d * (node.getNF(strategy) + node.getEP(strategy)));
    }

}
