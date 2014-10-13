package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.DefaultBiggReactionParserImpl;

public class TestDefaultBiggReactionParser {

	private static InputStream IS_VALID;
	private static InputStream IS_PARSE_ERROR;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		IS_FNF			= new FileInputStream(new File("/foo/bar/notfound"));
//		IS_VALID		= new FileInputStream(new File("./biosynth-data/src/test/resources/bigg_metabolite_valid.tsv"));
//		IS_PARSE_ERROR	= new FileInputStream(new File(""));
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
	public void testParseReactionsSuccess() throws IOException {
		new DefaultBiggReactionParserImpl().parseReactions(IS_VALID);
	}
	
	@Test
	public void testParseReactionsNull() throws IOException {
		new DefaultBiggReactionParserImpl().parseReactions(null);
	}

	@Test
	public void testParseReactionSuccess_h2o() {
		new DefaultBiggReactionParserImpl().parseReaction("a");
	}
	
	@Test
	public void testParseReactionSuccess_glc() {
		new DefaultBiggReactionParserImpl().parseReaction("a");
	}
	
	@Test
	public void testParseReactionSuccess_abc() {
		new DefaultBiggReactionParserImpl().parseReaction("a");
	}
	
	@Test
	public void testParseReactionFail() {
		new DefaultBiggReactionParserImpl().parseReaction("a\tb\tc\td\te");
	}
	
	@Test
	public void testParseReactionNull() {
		new DefaultBiggReactionParserImpl().parseReaction(null);
	}

	@Test
	public void testParseReactionEmpty() {
		new DefaultBiggReactionParserImpl().parseReaction("");
	}

}
