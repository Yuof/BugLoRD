/**
 * 
 */
package se.de.hu_berlin.informatik.sbfl.spectra;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.ExpectedException;

import se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra;
import se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra.CmdOptions;
import se.de.hu_berlin.informatik.stardust.localizer.SourceCodeBlock;
import se.de.hu_berlin.informatik.stardust.spectra.ISpectra;
import se.de.hu_berlin.informatik.stardust.util.SpectraFileUtils;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;

/**
 * @author Simon
 *
 */
public class CoberturaToSpectraTest extends TestSettings {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		FileUtils.delete(Paths.get(extraTestOutput));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
//		FileUtils.delete(Paths.get(extraTestOutput));
	}
	
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static String extraTestOutput = "target" + File.separator + "testoutputExtra";
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#main(java.lang.String[])}.
	 */
	@Test
	public void testMainRankingGeneration() {
		final String[] args = {
				CmdOptions.PROJECT_DIR.asArg(), getStdResourcesDir() + File.separator + "CoberturaTestProject", 
//				CmdOptions.CLASS_PATH.asArg(), getStdResourcesDir() + File.separator + "lib" + File.separator + "junit-4.11.jar",
				CmdOptions.SOURCE_DIR.asArg(), "src", 
				CmdOptions.TEST_CLASS_DIR.asArg(), "test-bin",
				CmdOptions.TEST_LIST.asArg(), getStdResourcesDir() + File.separator + "all_testsSimple.txt",
				CmdOptions.INSTRUMENT_CLASSES.asArg(), "bin",
				CmdOptions.OUTPUT.asArg(),  extraTestOutput + File.separator + "reportCobertura"};
		CoberturaToSpectra.main(args);
		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCobertura", "spectraCompressed.zip")));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCobertura", "ranking.trc")));
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#main(java.lang.String[])}.
	 */
	@Test
	public void testMainRankingGenerationSeparateJVM() {
		final String[] args = {
				CmdOptions.PROJECT_DIR.asArg(), getStdResourcesDir() + File.separator + "CoberturaTestProject", 
				CmdOptions.SOURCE_DIR.asArg(), "src", 
				CmdOptions.TEST_CLASS_DIR.asArg(), "test-bin",
				CmdOptions.TEST_LIST.asArg(), getStdResourcesDir() + File.separator + "all_testsSimple.txt",
				CmdOptions.SEPARATE_JVM.asArg(),
				CmdOptions.INSTRUMENT_CLASSES.asArg(), "bin",
				CmdOptions.OUTPUT.asArg(),  extraTestOutput + File.separator + "reportCobertura"};
		CoberturaToSpectra.main(args);
		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCobertura", "spectraCompressed.zip")));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCobertura", "ranking.trc")));
	}

	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
//	@Test
	public void testGenerateRankingForDefects4JElementMockito() {
		String testCP = getStdResourcesDir() + File.separator + "Mockito12b/lib/junit-4.11.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/target/classes" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/target/test-classes" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/asm-all-5.0.4.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/assertj-core-2.1.0.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/cglib-and-asm-1.0.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/cobertura-2.0.3.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/fest-assert-1.3.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/fest-util-1.1.4.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/hamcrest-all-1.3.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/hamcrest-core-1.1.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/objenesis-2.1.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/objenesis-2.2.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Mockito12b/lib/powermock-reflect-1.2.5.jar";
		
		ArrayList<String> failingTests = new ArrayList<>();
		failingTests.add("org.mockito.internal.util.reflection.GenericMasterTest::shouldDealWithNestedGenerics");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationBasicTest::shouldUseAnnotatedCaptor");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationBasicTest::shouldUseCaptorInOrdinaryWay");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationBasicTest::shouldCaptureGenericList");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationBasicTest::shouldUseGenericlessAnnotatedCaptor");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationTest::shouldScreamWhenWrongTypeForCaptor");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationTest::testNormalUsage");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationTest::shouldScreamWhenMoreThanOneMockitoAnnotaton");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationTest::shouldScreamWhenInitializingCaptorsForNullClass");
		failingTests.add("org.mockitousage.annotation.CaptorAnnotationTest::shouldLookForAnnotatedCaptorsInSuperClasses");

		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "Mockito12b")
		.setSourceDir("src")
		.setTestClassDir("target" + File.separator + "test-classes")
		.setTestClassPath(testCP)
		.setPathsToBinaries("target" + File.separator + "classes")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClassMockito12b")
		.setTestClassList("testClasses.txt")
		.setFailingTests(failingTests)
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(1)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClassMockito12b", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
		
//		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
//		assertFalse(spectra.getTraces().isEmpty());
//		assertEquals(spectra.getTraces().size()-2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
//	@Test
	public void testGenerateRankingForDefects4JElementClosure() {
		String testCP = getStdResourcesDir() + File.separator + "Closure101b/build/classes" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/ant_deploy.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/args4j_deploy.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/google_common_deploy.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/hamcrest-core-1.1.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/junit.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/libtrunk_rhino_parser_jarjared.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/protobuf_deploy.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/lib/ant.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Closure101b/build/test";
		
		ArrayList<String> failingTests = new ArrayList<>();
		failingTests.add("com.google.javascript.jscomp.CommandLineRunnerTest::testProcessClosurePrimitives");

		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "Closure101b")
		.setSourceDir("src")
		.setTestClassDir("build" + File.separator + "test")
		.setTestClassPath(testCP)
		.setPathsToBinaries("build" + File.separator + "classes")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClassClosure101b")
		.setTestClassList("testClasses.txt")
		.setFailingTests(failingTests)
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(5L)
		.setTestRepeatCount(1)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClassClosure101b", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportJaCoCoTestClass", "ranking.trc")));
		
//		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
//		assertFalse(spectra.getTraces().isEmpty());
//		assertEquals(spectra.getTraces().size()-2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
//	@Test
	public void testGenerateRankingForDefects4JElementLang8() {
		String testCP = getStdResourcesDir() + File.separator + "Lang8b/target/classes" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Lang8b/target/tests" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Lang8b/lib/junit-4.11.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Lang8b/lib/easymock.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Lang8b/lib/commons-io.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Lang8b/lib/cglib.jar" + File.pathSeparator
				+ getStdResourcesDir() + File.separator + "Lang8b/lib/asm.jar";
		
		ArrayList<String> failingTests = new ArrayList<>();
		failingTests.add("org.apache.commons.lang3.time.FastDateFormat_PrinterTest::testCalendarTimezoneRespected");
		failingTests.add("org.apache.commons.lang3.time.FastDatePrinterTest::testCalendarTimezoneRespected");

		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "Lang8b")
		.setSourceDir("src")
		.setTestClassDir("target" + File.separator + "tests")
		.setTestClassPath(testCP)
		.setPathsToBinaries("target" + File.separator + "classes")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClassLang8b")
		.setTestClassList("testClasses.txt")
		.setFailingTests(failingTests)
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(5L)
		.setTestRepeatCount(1)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClassLang8b", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportJaCoCoTestClass", "ranking.trc")));
		
//		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
//		assertFalse(spectra.getTraces().isEmpty());
//		assertEquals(spectra.getTraces().size()-2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElement() {
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass")
		.setTestClassList(getStdResourcesDir() + File.separator + "testclassesSimple.txt")
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(2)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
		
		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
		assertFalse(spectra.getTraces().isEmpty());
		assertEquals(spectra.getTraces().size()-2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElementTestList() {
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass9")
		.setTestList(getStdResourcesDir() + File.separator + "all_testsSimple.txt")
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(2)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass9", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
		
		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
		assertFalse(spectra.getTraces().isEmpty());
		assertEquals(2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElementTestListFullSpectra() {
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass10")
		.setTestList(getStdResourcesDir() + File.separator + "all_testsSimple.txt")
		.useFullSpectra(true)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(2)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass10", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
		
		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
		assertFalse(spectra.getTraces().isEmpty());
		assertEquals(2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElementWithFailedTestCases() {
		ArrayList<String> failingTests = new ArrayList<>();
		failingTests.add("coberturatest.tests.SimpleProgramTest::testAddWrong");
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass7")
		.setTestClassList(getStdResourcesDir() + File.separator + "testclassesSimple.txt")
		.setFailingTests(failingTests)
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(2)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass7", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
		
		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
		assertFalse(spectra.getTraces().isEmpty());
		assertEquals(spectra.getTraces().size()-2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElementWithWrongFailedTestCases() {
		ArrayList<String> failingTests = new ArrayList<>();
		failingTests.add("coberturatest.tests.SimpleProgramTest::testAdd");
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass8")
		.setTestClassList(getStdResourcesDir() + File.separator + "testclassesSimple.txt")
		.setFailingTests(failingTests)
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(2)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass8", "spectraCompressed.zip");
		assertFalse(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElementFullSpectra() {
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass2")
		.setTestClassList(getStdResourcesDir() + File.separator + "testclassesSimple.txt")
		.useFullSpectra(true)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(2)
		.run();

		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass2", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
		
		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
		assertFalse(spectra.getTraces().isEmpty());
		assertEquals(spectra.getTraces().size()-2, spectra.getSuccessfulTraces().size());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElementWrongTestClass() {
//		exception.expect(Abort.class);
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass3")
		.setTestClassList(getStdResourcesDir() + File.separator + "wrongTestClassesSimple.txt")
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(null)
		.setTestRepeatCount(2)
		.run();
		
		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass3", "spectraCompressed.zip");
		assertTrue(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
		
		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
		assertFalse(spectra.getTraces().isEmpty());
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.sbfl.spectra.cobertura.CoberturaToSpectra#generateRankingForDefects4JElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGenerateRankingForDefects4JElementWithTimeOut() {
		new CoberturaToSpectra.Builder()
		.setProjectDir(getStdResourcesDir() + File.separator + "CoberturaTestProject")
		.setSourceDir("src")
		.setTestClassDir("test-bin")
		.setPathsToBinaries("bin")
		.setOutputDir(extraTestOutput + File.separator + "reportCoberturaTestClass4")
		.setTestClassList(getStdResourcesDir() + File.separator + "testclassesSimple.txt")
		.useFullSpectra(false)
		.useSeparateJVM(false)
		.setTimeout(-1L)
		.setTestRepeatCount(1)
		.run();
		
		Path spectraZipFile = Paths.get(extraTestOutput, "reportCoberturaTestClass4", "spectraCompressed.zip");
		assertFalse(Files.exists(spectraZipFile));
//		assertTrue(Files.exists(Paths.get(extraTestOutput, "reportCoberturaTestClass", "ranking.trc")));
//		
//		ISpectra<SourceCodeBlock> spectra = SpectraFileUtils.loadBlockSpectraFromZipFile(spectraZipFile);
//		assertTrue(spectra.getTraces().isEmpty());
	}
	
//	/**
//	 * Test method for {@link se.de.hu_berlin.informatik.c2r.Cob2Instr2Coverage2Ranking#main(java.lang.String[])}.
//	 */
//	@Test
//	public void testMainRankingGeneration() {
//		String[] args = {
//				CmdOptions.PROJECT_DIR.asArg(), ".." + File.separator + "java-tokenizer", 
//				CmdOptions.SOURCE_DIR.asArg(), "src" + File.separator + "main" + File.separator + "java", 
//				CmdOptions.TEST_CLASS_DIR.asArg(), "target" + File.separator + "test-classes",
//				CmdOptions.TEST_LIST.asArg(), getStdResourcesDir() + File.separator + "all_tests.txt",
//				CmdOptions.INSTRUMENT_CLASSES.asArg(), ".." + File.separator + "java-tokenizer" + File.separator + "target" + File.separator + "classes",
//				CmdOptions.LOCALIZERS.asArg(), "tarantula", "jaccard",
//				CmdOptions.OUTPUT.asArg(), getStdTestDir() + File.separator + "reportCobertura" };
//		Cob2Instr2Coverage2Ranking.main(args);
//		assertTrue(true);
//	}
//	
//	/**
//	 * Test method for {@link se.de.hu_berlin.informatik.c2r.Cob2Instr2Coverage2Ranking#main(java.lang.String[])}.
//	 */
//	@Test
//	public void testMainRankingGenerationTestClassFile() {
//		String[] args = {
//				CmdOptions.PROJECT_DIR.asArg(), ".." + File.separator + "java-tokenizer", 
//				CmdOptions.SOURCE_DIR.asArg(), "src" + File.separator + "main" + File.separator + "java", 
//				CmdOptions.TEST_CLASS_DIR.asArg(), "target" + File.separator + "test-classes",
//				CmdOptions.TEST_CLASS_LIST.asArg(), getStdResourcesDir() + File.separator + "testclasses.txt",
//				CmdOptions.INSTRUMENT_CLASSES.asArg(), ".." + File.separator + "java-tokenizer" + File.separator + "target" + File.separator + "classes",
//				CmdOptions.LOCALIZERS.asArg(), "tarantula", "jaccard",
//				CmdOptions.OUTPUT.asArg(), getStdTestDir() + File.separator + "reportCoberturaTestClass" };
//		Cob2Instr2Coverage2Ranking.main(args);
//		assertTrue(true);
//	}
//	
//	/**
//	 * Test method for {@link se.de.hu_berlin.informatik.c2r.Cob2Instr2Coverage2Ranking#main(java.lang.String[])}.
//	 */
//	@Test
//	public void testMainTraceGeneration() {
//		String[] args = {
//				CmdOptions.PROJECT_DIR.asArg(), ".." + File.separator + "java-tokenizer", 
//				CmdOptions.SOURCE_DIR.asArg(), "src" + File.separator + "main" + File.separator + "java", 
//				CmdOptions.TEST_CLASS_DIR.asArg(), "target" + File.separator + "test-classes",
//				CmdOptions.TEST_LIST.asArg(), getStdResourcesDir() + File.separator + "all_tests.txt",
//				CmdOptions.INSTRUMENT_CLASSES.asArg(), ".." + File.separator + "java-tokenizer" + File.separator + "target" + File.separator + "classes",
//				CmdOptions.HIT_TRACE.asArg(),
//				CmdOptions.OUTPUT.asArg(), getStdTestDir() + File.separator + "reportCoberturaTraces" };
//		Cob2Instr2Coverage2Ranking.main(args);
//		assertTrue(true);
//	}

}
