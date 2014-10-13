package edu.uminho.biosynth.core.data.io.dao.biodb.bigg;

import static org.junit.Assert.*;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class TestCsvBiggReactionDaoImpl {

	@Autowired @Qualifier("csvBiggReactionDao")
	private ReactionDao<BiggReactionEntity> reactionDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
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
	public void testGetAllReactionIdsSize() {
		assertEquals(7135, reactionDao.getAllReactionIds().size());
	}

	@Test
	public void testGetAllReactionEntriesSize() {
		assertEquals(7135, reactionDao.getAllReactionEntries().size());
	}
	
	@Test
	public void testGetInvalidReaction1() {
		BiggReactionEntity rxn = reactionDao.getReactionByEntry("this should not exits");

		assertEquals(null, rxn);
	}
	
	@Test
	public void testGetValidReaction1() {
		BiggReactionEntity rxn = reactionDao.getReactionByEntry("LCYSTAT");

		assertEquals(2418755L, (long) rxn.getId());
		assertEquals("LCYSTAT", rxn.getEntry());
		assertEquals("L-Cysteate:2-oxoglutarate aminotransferase", rxn.getName());
		assertEquals("[c] : Lcyst + akg <==> 3spyr + glu-L", rxn.getEquation());
	}
}
