/**
 * 
 */
package se.de.hu_berlin.informatik.astlmbuilder;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;

/**
 * @author Simon
 *
 */
public class ASTLMBuilderTest extends TestSettings {

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
		deleteTestOutputs();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		deleteTestOutputs();
	}

	/**
	 * Test method for {@link se.de.hu_berlin.informatik.astlmbuilder.ASTLMBuilder#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() throws Exception {
		String[] args = {
				"-i", getStdResourcesDir() + File.separator + "training_files",  
				"-o", getStdTestDir() + File.separator + "out.lm",
				"-g", "all",
				"-e", "root",
				"-t",
				"-n", "3"};
		ASTLMBuilder.main(args);
		assertTrue(Files.exists(Paths.get(getStdTestDir(), "out.lm.bin")));
	}

}