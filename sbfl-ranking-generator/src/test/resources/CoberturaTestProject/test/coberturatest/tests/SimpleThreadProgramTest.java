/**
 * 
 */
package coberturatest.tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cobertura.test.SimpleThreadProgram;

/**
 * @author SimHigh
 *
 */
public class SimpleThreadProgramTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOneThread() throws Exception {
		SimpleThreadProgram.runOneThread();
	}

	@Test
	public void testTwoThreads() throws Exception {
		SimpleThreadProgram.runTwoThreads();
	}
	
}
