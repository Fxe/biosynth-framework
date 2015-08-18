package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.RestBiocycReactionDaoImpl;

public class TestRestBiocycReactionDaoImpl {

	private final static double EPSILON = 0.0000001;
	private static RestBiocycReactionDaoImpl restBiocycReactionDaoImpl;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		restBiocycReactionDaoImpl = new RestBiocycReactionDaoImpl();
		restBiocycReactionDaoImpl.setLocalStorage("D:/home/data/biocyc/");
		restBiocycReactionDaoImpl.setSaveLocalStorage(true);
		restBiocycReactionDaoImpl.setUseLocalStorage(true);
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
	public void test_META_PORPHOBILSYNTH_RXN() {
		BioCycReactionEntity rxn = restBiocycReactionDaoImpl.getReactionByEntry("PORPHOBILSYNTH-RXN");
		
		assertEquals("META:PORPHOBILSYNTH-RXN", rxn.getEntry());
		assertEquals("LEFT-TO-RIGHT", rxn.getReactionDirection());
		assertEquals(Orientation.LeftToRight, rxn.getOrientation());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(2, rxn.getPathways().size());
		assertEquals(1, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(4, rxn.getEnzymaticReactions().size());
		assertEquals(23, rxn.getCrossreferences().size());
		assertEquals(-33.084015, rxn.getGibbs(), EPSILON);
	}

//	@Test
	public void test_META_FLAVONE_APIOSYLTRANSFERASE_RXN() {
		BioCycReactionEntity rxn = restBiocycReactionDaoImpl.getReactionByEntry("FLAVONE-APIOSYLTRANSFERASE-RXN");
		
		assertEquals("META:FLAVONE-APIOSYLTRANSFERASE-RXN", rxn.getEntry());
		assertEquals("LEFT-TO-RIGHT", rxn.getReactionDirection());
		assertEquals(Orientation.LeftToRight, rxn.getOrientation());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(1, rxn.getCrossreferences().size());
		assertEquals(8.77002, rxn.getGibbs(), EPSILON);
	}
	
	@Test
	public void test_META_DEOXYADENPHOSPHOR_RXN() {
		BioCycReactionEntity rxn = restBiocycReactionDaoImpl.getReactionByEntry("DEOXYADENPHOSPHOR-RXN");
		
		assertEquals("META:DEOXYADENPHOSPHOR-RXN", rxn.getEntry());
		assertEquals("REVERSIBLE", rxn.getReactionDirection());
		assertEquals(Orientation.Reversible, rxn.getOrientation());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getEcNumbers().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(2, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(2, rxn.getCrossreferences().size());
		assertEquals(13.640015, rxn.getGibbs(), EPSILON);
	}
	
	@Test
	public void test_META_3_1_11_4_RXN() {
		BioCycReactionEntity rxn = restBiocycReactionDaoImpl.getReactionByEntry("3.1.11.4-RXN");
		
		System.out.println(rxn);
		
		assertEquals("META:3.1.11.4-RXN", rxn.getEntry());
		assertEquals(null, rxn.getReactionDirection());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(0, rxn.getEnzymaticReactions().size());
		assertEquals(1, rxn.getLeft().size());
		assertEquals(2, rxn.getRight().size());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getEcNumbers().size());
		assertEquals(0, rxn.getEnzymaticReactions().size());
		assertEquals(0, rxn.getCrossreferences().size());
		assertEquals(null, rxn.getGibbs());
	}
	
//	@Test
	public void test_META_1_2_1_66_RXN() {
		BioCycReactionEntity rxn = restBiocycReactionDaoImpl.getReactionByEntry("1.2.1.66-RXN");
		
		System.out.println(rxn);
		
		assertEquals("META:1.2.1.66-RXN", rxn.getEntry());
		assertEquals("LEFT-TO-RIGHT", rxn.getReactionDirection());
		assertEquals(Orientation.LeftToRight, rxn.getOrientation());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(false, rxn.getOrphan());
		assertEquals(2, rxn.getParents().size());
		assertEquals(1, rxn.getEcNumbers().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(1, rxn.getEnzymaticReactions().size());
		assertEquals(2, rxn.getCrossreferences().size());
		assertEquals(-6.080078, rxn.getGibbs(), EPSILON);
	}
	
	
//	@Test
	public void test_META_RXN0_3283() {
		BioCycReactionEntity rxn = restBiocycReactionDaoImpl.getReactionByEntry("RXN0-3283");
		
		System.out.println(rxn);
		
		assertEquals("META:RXN0-3283", rxn.getEntry());
		assertEquals(null, rxn.getReactionDirection());
		assertEquals(Orientation.Unknown, rxn.getOrientation());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(null, rxn.getOrphan());
		assertEquals(2, rxn.getParents().size());
		assertEquals(0, rxn.getEcNumbers().size());
		assertEquals(0, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(1, rxn.getRight().size());
		assertEquals(0, rxn.getEnzymaticReactions().size());
		assertEquals(0, rxn.getCrossreferences().size());
		assertEquals(null, rxn.getGibbs());
	}
	
//	@Test
	public void test_META_3_OXOACYL_ACP_REDUCT_RXN() {
		BioCycReactionEntity rxn = restBiocycReactionDaoImpl.getReactionByEntry("3-OXOACYL-ACP-REDUCT-RXN");
		
		System.out.println(rxn);
		
		assertEquals("META:3-OXOACYL-ACP-REDUCT-RXN", rxn.getEntry());
		assertEquals("RIGHT-TO-LEFT", rxn.getReactionDirection());
		assertEquals(Orientation.RightToLeft, rxn.getOrientation());
		assertEquals(true, rxn.getPhysiologicallyRelevant());
		assertEquals(false, rxn.getOrphan());
		assertEquals(1, rxn.getParents().size());
		assertEquals(3, rxn.getEcNumbers().size());
		assertEquals(1, rxn.getPathways().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(3, rxn.getRight().size());
		assertEquals(5, rxn.getEnzymaticReactions().size());
		assertEquals(23, rxn.getCrossreferences().size());
		assertEquals(2.330017, rxn.getGibbs(), EPSILON);
	}
//	@Test
//	public void testGetAllReactions() {
//		for (String rxnEntry : reactionDao.getAllReactionEntries()) {
//			System.out.print(rxnEntry);
//			reactionDao.getReactionByEntry(rxnEntry);
//			System.out.println(" OK !");
//		}
//
//		assertEquals(true, true);
//	}
}
