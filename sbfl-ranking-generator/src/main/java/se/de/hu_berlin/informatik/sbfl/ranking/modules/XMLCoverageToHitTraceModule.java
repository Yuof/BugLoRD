/**
 * 
 */
package se.de.hu_berlin.informatik.sbfl.ranking.modules;

import java.io.File;
import java.nio.file.Paths;

import se.de.hu_berlin.informatik.sbfl.spectra.modules.TraceFileModule;
import se.de.hu_berlin.informatik.stardust.provider.cobertura.CoberturaXMLProvider;
import se.de.hu_berlin.informatik.stardust.provider.cobertura.CoberturaCoverageWrapper;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Computes the hit trace for the wrapped Cobertura report and saves the
 * trace file to the hard drive.
 * 
 * @author Simon Heiden
 */
public class XMLCoverageToHitTraceModule extends AbstractProcessor<CoberturaCoverageWrapper, Object> {

	final private String outputdir;
	
	/**
	 * Creates a new {@link XMLCoverageToHitTraceModule} object with the given parameters.
	 * @param outputdir
	 * path to output directory
	 */
	public XMLCoverageToHitTraceModule(final String outputdir) {
		super();
		this.outputdir = outputdir;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public Object processItem(final CoberturaCoverageWrapper coverage) {
		computeHitTrace(coverage);
		return null;
	}
	
	/**
	 * Calculates a single hit trace from the given input xml file to output/inputfilename.trc.
	 * @param input
	 * path to Cobertura trace file in xml format
	 */
	private void computeHitTrace(final CoberturaCoverageWrapper coverage) {
		final CoberturaXMLProvider provider = new CoberturaXMLProvider();
		if (!provider.addData(coverage.getXmlCoverageFile().toString(), coverage.getIdentifier(), true)) {
			Log.err(this, "Could not add XML coverage file '%s'.", coverage.getXmlCoverageFile().toString());
			return;
		}

		try {
			new TraceFileModule<>(Paths.get(outputdir + File.separator + coverage.getXmlCoverageFile().getName().replace(':','_') + ".trc"))
			.submit(provider.loadSpectra());
		} catch (IllegalStateException e) {
			Log.err(this, e, "Providing the spectra failed.");
		}
	}

}