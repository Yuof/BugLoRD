/*
 * This file is part of the "STARDUST" project. (c) Fabian Keller
 * <hello@fabian-keller.de> For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package se.de.hu_berlin.informatik.spectra.provider.loader.cobertura.xml;

import se.de.hu_berlin.informatik.spectra.core.ISpectra;
import se.de.hu_berlin.informatik.spectra.core.count.CountTrace;
import se.de.hu_berlin.informatik.spectra.core.hit.HierarchicalHitSpectra;

public abstract class HierarchicalCoberturaCountXMLLoader<T, K extends CountTrace<T>>
		extends CoberturaCountXMLLoader<T, K> {

	private HierarchicalHitSpectra<String, T> methodSpectra;
	private HierarchicalHitSpectra<String, String> classSpectra;
	private HierarchicalHitSpectra<String, String> packageSpectra;

	public HierarchicalCoberturaCountXMLLoader(HierarchicalHitSpectra<String, String> packageSpectra,
			HierarchicalHitSpectra<String, String> classSpectra, HierarchicalHitSpectra<String, T> methodSpectra) {
		this.methodSpectra = methodSpectra;
		this.classSpectra = classSpectra;
		this.packageSpectra = packageSpectra;
	}

	@Override
	protected void onNewClass(String packageName, String classFilePath, K currentTrace) {
		super.onNewClass(packageName, classFilePath, currentTrace);
		packageSpectra.setParent(packageName, classFilePath);
	}

	@Override
	protected void onNewMethod(String packageName, String classFilePath, String methodName, K currentTrace) {
		super.onNewMethod(packageName, classFilePath, methodName, currentTrace);
		classSpectra.setParent(classFilePath, methodName);
	}

	@Override
	protected void onNewLine(String packageName, String classFilePath, String methodName, T lineIdentifier,
			ISpectra<T, K> lineSpectra, K currentTrace, boolean fullSpectra, long numberOfHits) {
		super.onNewLine(
				packageName, classFilePath, methodName, lineIdentifier, lineSpectra, currentTrace, fullSpectra,
				numberOfHits);
		methodSpectra.setParent(methodName, lineIdentifier);
	}

}