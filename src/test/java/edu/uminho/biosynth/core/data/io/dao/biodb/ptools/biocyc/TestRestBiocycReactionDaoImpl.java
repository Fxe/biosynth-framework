package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;

public class TestRestBiocycReactionDaoImpl {

	private static RestBiocycReactionDaoImpl reactionDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		reactionDao = new RestBiocycReactionDaoImpl();
		reactionDao.setLocalStorage("D:/home/data/biocyc/");
		reactionDao.setSaveLocalStorage(true);
		reactionDao.setUseLocalStorage(true);
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
	public void testPORPHOBILSYNTH_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("PORPHOBILSYNTH-RXN");
		
//		assertEquals("EC-4.2.1.24", rxn.getEcNumber());
//		assertEquals(true, rxn.getEcNumberOfficial());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(2, rxn.getPathways().size());
		assertEquals(1, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(4, rxn.getEnzymaticReactions().size());
		assertEquals(23, rxn.getCrossReferences().size());
	}

	@Test
	public void testFLAVONE_APIOSYLTRANSFERASE_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("FLAVONE-APIOSYLTRANSFERASE-RXN");
		
//		assertEquals("EC-2.4.2.25", rxn.getEcNumber());
//		assertEquals(true, rxn.getEcNumberOfficial());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(1, rxn.getCrossReferences().size());
	}
	
	@Test
	public void testDEOXYADENPHOSPHOR_RXN() {
		BioCycReactionEntity rxn = reactionDao.getReactionByEntry("DEOXYADENPHOSPHOR-RXN");
		
//		assertEquals("EC-2.4.2.1", rxn.getEcNumber());
//		assertEquals(null, rxn.getEcNumberOfficial());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(2, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(2, rxn.getCrossReferences().size());
	}
	
	@Test
	public void testGetAllReactions() {
		for (String rxnEntry : reactionDao.getAllReactionEntries()) {
			System.out.print(rxnEntry);
			reactionDao.getReactionByEntry(rxnEntry);
			System.out.println(" OK !");
		}

		assertEquals(true, true);
	}
}
