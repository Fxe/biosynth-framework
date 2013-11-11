package edu.uminho.biosynth.core.data.io.remote;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.kegg.KeggReactionEntity;

public class TestKeggRemoteSource {

	private static KeggRemoteSource remote;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		remote = new KeggRemoteSource();
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
	public void testGetR00001() {
		KeggReactionEntity rxn = remote.getReactionInformation("R00001");
		System.out.println(rxn);
	}

	@Test
	public void testGetC00001() {
		KeggMetaboliteEntity cpd = remote.getMetaboliteInformation("C00001");
		System.out.println(cpd);
	}
}
