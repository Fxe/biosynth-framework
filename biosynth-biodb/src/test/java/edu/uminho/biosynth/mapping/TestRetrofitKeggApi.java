package edu.uminho.biosynth.mapping;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.io.biodb.kegg.KeggApi;

@SuppressWarnings("unused")
public class TestRetrofitKeggApi {

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

//	@Test
//	public void test() {
//		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://rest.kegg.jp/").build();
//		KeggApi keggApi = restAdapter.create(KeggApi.class);
//		System.out.println(keggApi.getInfo("ligand"));
//	}
  @Test
  public void test2() {
    assertEquals(true, true);
  }

}
