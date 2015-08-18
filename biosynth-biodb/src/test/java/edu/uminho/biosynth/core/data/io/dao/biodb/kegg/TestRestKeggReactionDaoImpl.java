package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggReactionDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class TestRestKeggReactionDaoImpl {

	private static ReactionDao<KeggReactionEntity> reactionDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RestKeggReactionDaoImpl restKeggReactionDaoImpl = new RestKeggReactionDaoImpl();
		restKeggReactionDaoImpl.setUseLocalStorage(true);
		restKeggReactionDaoImpl.setSaveLocalStorage(true);
		restKeggReactionDaoImpl.setLocalStorage("D:/home/data/kegg");
		
		reactionDao = restKeggReactionDaoImpl;
	}

	@Test
	public void testR00001() {
		KeggReactionEntity rxn = reactionDao.getReactionByEntry("R00001");
		
		assertEquals("R00001", rxn.getEntry());
		assertEquals("polyphosphate polyphosphohydrolase", rxn.getName());
		assertEquals("Polyphosphate + n H2O <=> (n+1) Oligophosphate", rxn.getDefinition());
		assertEquals("C00404 + n C00001 <=> (n+1) C02174", rxn.getEquation());
		assertEquals(null, rxn.getComment());
		assertEquals(null, rxn.getRemark());
		assertEquals(0, rxn.getRpairs().size());
		assertEquals(0, rxn.getPathways().size());
		assertEquals(1, rxn.getEnzymes().size());
		assertEquals(0, rxn.getOrthologies().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(1, rxn.getRight().size());
	}
	
	@Test
	public void testR00002() {
		KeggReactionEntity rxn = reactionDao.getReactionByEntry("R00002");
		
		assertEquals("R00002", rxn.getEntry());
		assertEquals("Reduced ferredoxin:dinitrogen oxidoreductase (ATP-hydrolysing)", rxn.getName());
		assertEquals("16 ATP + 16 H2O + 8 Reduced ferredoxin <=> 8 e- + 16 Orthophosphate + 16 ADP + 8 Oxidized ferredoxin", rxn.getDefinition());
		assertEquals("16 C00002 + 16 C00001 + 8 C00138 <=> 8 C05359 + 16 C00009 + 16 C00008 + 8 C00139", rxn.getEquation());
		assertEquals("a part of multi-step reaction (see R05185, R00002+R00067+R00153+R02802+R04782)", rxn.getComment());
		assertEquals(null, rxn.getRemark());
		assertEquals(3, rxn.getRpairs().size());
		assertEquals(0, rxn.getPathways().size());
		assertEquals(1, rxn.getEnzymes().size());
		assertEquals(0, rxn.getOrthologies().size());
		assertEquals(3, rxn.getLeft().size());
		assertEquals(4, rxn.getRight().size());
	}
	
	@Test
	public void testR00010() {
		KeggReactionEntity rxn = reactionDao.getReactionByEntry("R00010");
		
		assertEquals("R00010", rxn.getEntry());
		assertEquals("alpha,alpha-trehalose glucohydrolase", rxn.getName());
		assertEquals("alpha,alpha-Trehalose + H2O <=> 2 D-Glucose", rxn.getDefinition());
		assertEquals("C01083 + C00001 <=> 2 C00031", rxn.getEquation());
		assertEquals(null, rxn.getComment());
		assertEquals("Same as: R06103", rxn.getRemark());
		assertEquals(2, rxn.getRpairs().size());
		assertEquals(2, rxn.getPathways().size());
		assertEquals(1, rxn.getEnzymes().size());
		assertEquals(1, rxn.getOrthologies().size());
		assertEquals(2, rxn.getLeft().size());
		assertEquals(1, rxn.getRight().size());
	}
	
	@Test(expected=RuntimeException.class)
	public void testGetById() {
		reactionDao.getReactionById(5L);
	}
	
	@Test
	public void testGetAllEntries() {
		Set<String> entries = reactionDao.getAllReactionEntries();
		assertEquals(false, entries.isEmpty());
	}

	@Test(expected=RuntimeException.class)
	public void testGetAllIds() {
		reactionDao.getAllReactionIds();
	}
}
