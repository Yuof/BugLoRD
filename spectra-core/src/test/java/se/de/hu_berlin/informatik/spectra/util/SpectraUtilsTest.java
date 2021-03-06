package se.de.hu_berlin.informatik.spectra.util;

import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import se.de.hu_berlin.informatik.spectra.core.INode.CoverageType;
import se.de.hu_berlin.informatik.spectra.core.ITrace;
import se.de.hu_berlin.informatik.spectra.core.Node.NodeType;
import se.de.hu_berlin.informatik.spectra.core.SourceCodeBlock;
import se.de.hu_berlin.informatik.spectra.core.hit.HitSpectra;
import se.de.hu_berlin.informatik.spectra.core.hit.HitTrace;
import se.de.hu_berlin.informatik.spectra.provider.cobertura.CoberturaSpectraProviderFactory;
import se.de.hu_berlin.informatik.spectra.provider.cobertura.xml.CoberturaXMLProvider;
import se.de.hu_berlin.informatik.spectra.test.data.SimpleSpectraProvider2;
import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;

/**
 * @author SimHigh
 */
public class SpectraUtilsTest extends TestSettings {

    /**
     *
     */
    @BeforeClass
    public static void setUpBeforeClass() {
    }

    /**
     *
     */
    @AfterClass
    public static void tearDownAfterClass() {
        deleteTestOutputs();
    }

    /**
     *
     */
    @Before
    public void setUp() {
//		Log.off();
    }

    /**
     *
     */
    @After
    public void tearDown() {
        deleteTestOutputs();
    }

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Test
    public void invertSimpleCoverage() {
        final CoberturaXMLProvider<HitTrace<SourceCodeBlock>> c = CoberturaSpectraProviderFactory.getHitSpectraFromXMLProvider(true);
        c.addData("src/test/resources/fk/stardust/provider/simple-coverage.xml", "simple", true);
        //load and invert (only one trace exists - successful)
        HitSpectra<SourceCodeBlock> s = SpectraUtils.createInvertedSpectrum(c.loadSpectra(), true, false);
        checkSimpleNodes(s);
        checkSimpleTraceInverted(s);

        //invert again
        s = s.createInvertedSpectra(true, true);
        checkSimpleNodes(s);
        checkSimpleTraceNormal(s);

        //should not change
        s = s.createInvertedSpectra(false, true);
        checkSimpleNodes(s);
        checkSimpleTraceNormal(s);

        //should not change
        s = s.createInvertedSpectra(false, false);
        checkSimpleNodes(s);
        checkSimpleTraceNormal(s);
    }

    private static void checkSimpleNodes(final HitSpectra<SourceCodeBlock> s) {
        // assert loaded count is correct
        Assert.assertEquals(s.getNodes().size(), 3);
        Assert.assertEquals(s.getTraces().size(), 1);

        // assert we have nodes
        Assert.assertTrue(s.hasNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "<init>()V", 3, NodeType.NORMAL)));
        Assert.assertTrue(s.hasNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "main([Ljava/lang/String;)V", 9, NodeType.NORMAL)));
        Assert.assertTrue(s.hasNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "main([Ljava/lang/String;)V", 10, NodeType.NORMAL)));
    }

    private static void checkSimpleTraceNormal(final HitSpectra<SourceCodeBlock> s) {
        // assert trace has correct involvement loaded
        final ITrace<SourceCodeBlock> t = s.getTraces().iterator().next();
        Assert.assertFalse(t.isInvolved(s.getOrCreateNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "<init>()V", 3, NodeType.NORMAL))));
        Assert.assertTrue(t.isInvolved(s.getOrCreateNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "main([Ljava/lang/String;)V", 9, NodeType.NORMAL))));
        Assert.assertTrue(t.isInvolved(s.getOrCreateNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "main([Ljava/lang/String;)V", 10, NodeType.NORMAL))));

        //assert that trace is loaded as 'successful'
        Assert.assertTrue(t.isSuccessful());
    }

    private static void checkSimpleTraceInverted(final HitSpectra<SourceCodeBlock> s) {
        // assert trace has correct involvement loaded
        final ITrace<SourceCodeBlock> t = s.getTraces().iterator().next();
        Assert.assertTrue(t.isInvolved(s.getOrCreateNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "<init>()V", 3, NodeType.NORMAL))));
        Assert.assertFalse(t.isInvolved(s.getOrCreateNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "main([Ljava/lang/String;)V", 9, NodeType.NORMAL))));
        Assert.assertFalse(t.isInvolved(s.getOrCreateNode(new SourceCodeBlock("cobertura", "cobertura/CoverageTest.java", "main([Ljava/lang/String;)V", 10, NodeType.NORMAL))));

        //assert that trace is loaded as 'successful'
        Assert.assertTrue(t.isSuccessful());
    }

    @Test
    public void removeNodes() {
        HitSpectra<String> s;

        s = loadSimpleSpectraAndCheck();
        s.removeNodesWithCoverageType(CoverageType.EXECUTED);
        Assert.assertEquals(1, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.NOT_EXECUTED);
        Assert.assertEquals(0, s.getNodes().size());

        s = loadSimpleSpectraAndCheck();
        s.removeNodesWithCoverageType(CoverageType.NOT_EXECUTED);
        Assert.assertEquals(5, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EXECUTED);
        Assert.assertEquals(0, s.getNodes().size());

        s = loadSimpleSpectraAndCheck();
        s.removeNodesWithCoverageType(CoverageType.EF_GT_ZERO);
        Assert.assertEquals(2, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EP_EQUALS_ZERO);
        Assert.assertEquals(1, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EF_EQUALS_ZERO);
        Assert.assertEquals(0, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EP_GT_ZERO);
        Assert.assertEquals(0, s.getNodes().size());

        s = loadSimpleSpectraAndCheck();
        s.removeNodesWithCoverageType(CoverageType.EP_EQUALS_ZERO);
        Assert.assertEquals(4, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EF_EQUALS_ZERO);
        Assert.assertEquals(3, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EP_GT_ZERO);
        Assert.assertEquals(0, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EF_GT_ZERO);
        Assert.assertEquals(0, s.getNodes().size());

        s = loadSimpleSpectraAndCheck();
        s.removeNodesWithCoverageType(CoverageType.EP_GT_ZERO);
        Assert.assertEquals(2, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EF_EQUALS_ZERO);
        Assert.assertEquals(1, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EP_EQUALS_ZERO);
        Assert.assertEquals(0, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EF_GT_ZERO);
        Assert.assertEquals(0, s.getNodes().size());

        s = loadSimpleSpectraAndCheck();
        s.removeNodesWithCoverageType(CoverageType.EF_EQUALS_ZERO);
        Assert.assertEquals(4, s.getNodes().size(), 4);
        s.removeNodesWithCoverageType(CoverageType.EP_GT_ZERO);
        Assert.assertEquals(1, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EP_EQUALS_ZERO);
        Assert.assertEquals(0, s.getNodes().size());
        s.removeNodesWithCoverageType(CoverageType.EF_GT_ZERO);
        Assert.assertEquals(0, s.getNodes().size());
    }

    private static HitSpectra<String> loadSimpleSpectraAndCheck() {
        final HitSpectra<String> s = new SimpleSpectraProvider2().loadHitSpectra();
        Assert.assertEquals(s.getNodes().size(), 6);
        Assert.assertEquals(s.getTraces().size(), 6);
        return s;
    }

}
