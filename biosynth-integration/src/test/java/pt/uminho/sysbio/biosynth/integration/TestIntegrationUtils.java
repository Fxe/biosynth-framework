package pt.uminho.sysbio.biosynth.integration;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.util.BioSynthUtils;

public class TestIntegrationUtils {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test_escape_entry_1() {
		String entry  = "MetaCyc_a 3'-phosphopolynucleotide";
		String entry_ = BioSynthUtils.escapeEntry(entry);
		assertEquals("MetaCyc_a_SPACE_3'-phosphopolynucleotide", entry_);
	}

	@Test
	public void test_escape_entry_2() {
		String entry  = "a b c";
		String entry_ = BioSynthUtils.escapeEntry(entry);
		assertEquals("a_SPACE_b_SPACE_c", entry_);
	}
}
