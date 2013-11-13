package edu.uminho.biosynth.core.data.io.parser.kegg;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.data.io.http.HttpRequest;

public class TestKeggCompoundFlatFileParser {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testC00007() {
		KeggCompoundFlatFileParser parser = 
				new KeggCompoundFlatFileParser(HttpRequest.get("http://rest.kegg.jp/get/C00007"));
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testC00755() {
		KeggCompoundFlatFileParser parser = 
				new KeggCompoundFlatFileParser(HttpRequest.get("http://rest.kegg.jp/get/C00755"));

		fail("Not yet implemented");
	}
	
	@Test
	public void testC01245() {
		KeggCompoundFlatFileParser parser = 
				new KeggCompoundFlatFileParser(HttpRequest.get("http://rest.kegg.jp/get/C01245"));

		fail("Not yet implemented");
	}

}
