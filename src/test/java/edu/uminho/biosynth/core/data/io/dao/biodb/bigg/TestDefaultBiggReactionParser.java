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
		DefaultBiggReactionParser.parseReactions(IS_VALID);
	}
	
	@Test
	public void testParseReactionsNull() throws IOException {
		DefaultBiggReactionParser.parseReactions(null);
	}

	@Test
	public void testParseReactionSuccess_h2o() {
		DefaultBiggReactionParser.parseReaction("a");
	}
	
	@Test
	public void testParseReactionSuccess_glc() {
		DefaultBiggReactionParser.parseReaction("a");
	}
	
	@Test
	public void testParseReactionSuccess_abc() {
		DefaultBiggReactionParser.parseReaction("a");
	}
	
	@Test
	public void testParseReactionFail() {
		DefaultBiggReactionParser.parseReaction("a\tb\tc\td\te");
	}
	
	@Test
	public void testParseReactionNull() {
		DefaultBiggReactionParser.parseReaction(null);
	}

	@Test
	public void testParseReactionEmpty() {
		DefaultBiggReactionParser.parseReaction("");
	}

}
