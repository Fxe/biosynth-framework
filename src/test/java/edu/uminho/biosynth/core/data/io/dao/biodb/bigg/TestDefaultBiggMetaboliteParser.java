package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggMetaboliteParser;

public class TestDefaultBiggMetaboliteParser {
	
	private static InputStream IS_VALID;
	private static InputStream IS_PARSE_ERROR;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		IS_VALID		= new FileInputStream(
				new File("./src/test/resources/bigg_metabolite_valid.tsv"));
		IS_PARSE_ERROR	= new FileInputStream(
				new File("./src/test/resources/bigg_metabolite_invalid.tsv"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		closeInputStream(IS_VALID);
		closeInputStream(IS_PARSE_ERROR);
	}
	
	private static void closeInputStream(InputStream inputStream) throws IOException {
		if (inputStream != null) inputStream.close();
	}
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testParseMetabolitesSuccess() throws IOException {
		List<BiggMetaboliteEntity> biggMetaboliteEntities = 
				DefaultBiggMetaboliteParser.parseMetabolites(IS_VALID);
		
		assertNotEquals(null, biggMetaboliteEntities);
		assertEquals(7, biggMetaboliteEntities.size());
//		System.out.println(biggMetaboliteEntities);
	}
	
	@Test
	public void testParseMetabolitesFail() throws IOException {
		List<BiggMetaboliteEntity> biggMetaboliteEntities = 
				DefaultBiggMetaboliteParser.parseMetabolites(IS_PARSE_ERROR);
		System.out.println(biggMetaboliteEntities);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testParseMetabolitesNull() throws IOException {
		DefaultBiggMetaboliteParser.parseMetabolites(null);
	}

	@Test
	public void testParseMetaboliteSuccess_example1() {
		final String CPD_LINE = "aaa1bbb\tN10I\tN2K\t0\tPe, ex, Z B, C, E, N, V, P, Mmm, Lyy\tC00000\t1-2-3\t12345\t1,10,2,3,4,5,6,7,8,9\t";
		BiggMetaboliteEntity biggMetaboliteEntity = 
				DefaultBiggMetaboliteParser.parseMetabolite(CPD_LINE);
		
		assertEquals(12345, (long)biggMetaboliteEntity.getId());
		assertEquals("aaa1bbb", biggMetaboliteEntity.getEntry());
		assertEquals("N10I", biggMetaboliteEntity.getName());
		assertEquals(0, (int)biggMetaboliteEntity.getCharge());
		assertEquals("N2K", biggMetaboliteEntity.getFormula());
		assertEquals(11, biggMetaboliteEntity.getCrossreferences().size());
		assertEquals(10, biggMetaboliteEntity.getCompartments().size());
	}
	
	@Test
	public void testParseMetaboliteSuccess_glc() {
		DefaultBiggMetaboliteParser.parseMetabolite("a");
	}
	
	@Test
	public void testParseMetaboliteSuccess_example3() {
		final String CPD_LINE = "11\t(aa':aa')-(-:-)\tZe8S8X\t-3\tK\t\tSE: KKK\t11111111\t1,10,2,3,4,5,6,7,8,9\t";
		BiggMetaboliteEntity biggMetaboliteEntity = 
				DefaultBiggMetaboliteParser.parseMetabolite(CPD_LINE);
		
		assertEquals(11111111, (long)biggMetaboliteEntity.getId());
		assertEquals("11", biggMetaboliteEntity.getEntry());
		assertEquals("(aa':aa')-(-:-)", biggMetaboliteEntity.getName());
		assertEquals(-3, (int)biggMetaboliteEntity.getCharge());
		assertEquals("Ze8S8X", biggMetaboliteEntity.getFormula());
		assertEquals(10, biggMetaboliteEntity.getCrossreferences().size());
		assertEquals("K", biggMetaboliteEntity.getCompartments().iterator().next());
	}
	
	@Test
	public void testParseMetaboliteFail() {
		DefaultBiggMetaboliteParser.parseMetabolite("a\tb\tc\td\te");
	}
	
	@Test
	public void testParseMetaboliteNull() {
		DefaultBiggMetaboliteParser.parseMetabolite(null);
	}

	@Test
	public void testParseMetaboliteEmpty() {
		DefaultBiggMetaboliteParser.parseMetabolite("");
	}
}
