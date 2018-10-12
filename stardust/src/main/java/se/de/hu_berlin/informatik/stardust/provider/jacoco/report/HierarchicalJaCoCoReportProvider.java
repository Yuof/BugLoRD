/*
 * This file is part of the "STARDUST" project. (c) Fabian Keller
 * <hello@fabian-keller.de> For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package se.de.hu_berlin.informatik.stardust.provider.jacoco.report;

import se.de.hu_berlin.informatik.stardust.localizer.SourceCodeBlock;
import se.de.hu_berlin.informatik.stardust.provider.AbstractHierarchicalSpectraProvider;
import se.de.hu_berlin.informatik.stardust.provider.loader.ICoverageDataLoader;
import se.de.hu_berlin.informatik.stardust.provider.loader.jacoco.report.HierarchicalJaCoCoReportLoader;
import se.de.hu_berlin.informatik.stardust.spectra.INode;
import se.de.hu_berlin.informatik.stardust.spectra.ISpectra;
import se.de.hu_berlin.informatik.stardust.spectra.ITrace;
import se.de.hu_berlin.informatik.stardust.spectra.hit.HitSpectra;

/**
 * Loads JaCoCo reports to {@link HitSpectra} objects where each covered line is
 * represented by one node and each file represents one trace in the resulting
 * spectra.
 */
public class HierarchicalJaCoCoReportProvider<K extends ITrace<SourceCodeBlock>>
		extends AbstractHierarchicalSpectraProvider<SourceCodeBlock, K, JaCoCoReportWrapper> {

	private ICoverageDataLoader<SourceCodeBlock, K, JaCoCoReportWrapper> loader;

	public HierarchicalJaCoCoReportProvider(ISpectra<SourceCodeBlock, K> lineSpectra,
			boolean fullSpectra) {
		super(lineSpectra, fullSpectra);

		loader = new HierarchicalJaCoCoReportLoader<SourceCodeBlock, K>(packageSpectra, classSpectra, methodSpectra) {

			@Override
			public SourceCodeBlock getIdentifier(String packageName, String sourceFilePath, String methodNameAndSig,
					int lineNumber) {
				return new SourceCodeBlock(packageName, sourceFilePath, methodNameAndSig, lineNumber);
			}
			
			@Override
			public int getNodeIndex(String sourceFilePath, int lineNumber) {
				SourceCodeBlock identifier = new SourceCodeBlock(null, sourceFilePath, null, lineNumber);
				INode<SourceCodeBlock> node = lineSpectra.getNode(identifier);
				if (node == null) {
					return -1;
				} else {
					return node.getIndex();
				}
			}

		};
	}

	@Override
	protected ICoverageDataLoader<SourceCodeBlock, K, JaCoCoReportWrapper> getLoader() {
		return loader;
	}

}
