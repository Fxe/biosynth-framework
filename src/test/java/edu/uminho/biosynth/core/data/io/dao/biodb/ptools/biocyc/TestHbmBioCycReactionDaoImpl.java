package edu.uminho.biosynth.core.data.io.dao.biodb.ptools.biocyc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionEcNumberEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionLeftEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycReactionRightEntity;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;

public class TestHbmBioCycReactionDaoImpl {

	private static final String HBM_BIOCYC_CFG_FILE = "D:/home/data/java_config/hbm_mysql_biobase_test.cfg.xml";
	private static SessionFactory sessionFactory;
	private static ReactionDao<BioCycReactionEntity> reactionDao;
	private static org.hibernate.Transaction hbm_tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(
				new File(HBM_BIOCYC_CFG_FILE));
		HbmBioCycReactionDaoImpl bioCycReactionDaoImpl = new HbmBioCycReactionDaoImpl();
		bioCycReactionDaoImpl.setSessionFactory(sessionFactory);
		
		reactionDao = bioCycReactionDaoImpl;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		hbm_tx = sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		hbm_tx.rollback();
	}

	@Test
	public void testGetAllReactionEntriesEmpty() {
		Set<String> entries = reactionDao.getAllReactionEntries();
		
		assertEquals(0, entries.size());
	}
	
	@Test
	public void testGetAllReactionIdsEmpty() {
		Set<Long> ids = reactionDao.getAllReactionIds();
		
		assertEquals(0, ids.size());
	}
	
	@Test
	public void testSaveReactionMinimal() {
		BioCycReactionEntity reaction = new BioCycReactionEntity();
		reaction.setEntry("RXN-12345");
		
		reactionDao.saveReaction(reaction);
		
		assertNotEquals(null, reaction.getId());
		assertEquals("RXN-12345", reaction.getEntry());
	}

	@Test
	public void testSaveReactionFull() {
		BioCycReactionEntity reaction = new BioCycReactionEntity();
		reaction.setEntry("RXN-12345");
		reaction.setName("The Reaction 123456");
		reaction.setSource("META");
		reaction.getParents().add("RXN-P12345");
		reaction.getParents().add("RXN-P12346");
		reaction.getEnzymaticReactions().add("ERXN-E12345");
		reaction.getEnzymaticReactions().add("ERXN-E12346");
		reaction.getPathways().add("PWY-123456");
		reaction.getPathways().add("PWY-123457");
		BioCycReactionLeftEntity leftEntity1 = new BioCycReactionLeftEntity();
		BioCycReactionLeftEntity leftEntity2 = new BioCycReactionLeftEntity();
		BioCycReactionRightEntity rightEntity1 = new BioCycReactionRightEntity();
		BioCycReactionRightEntity rightEntity2 = new BioCycReactionRightEntity();
		leftEntity1.setCpdEntry("CPD-123456L"); leftEntity1.setBioCycReactionEntity(reaction);
		leftEntity2.setCpdEntry("CPD-123457L"); leftEntity2.setBioCycReactionEntity(reaction);
		rightEntity1.setCpdEntry("CPD-123456R"); rightEntity1.setBioCycReactionEntity(reaction);
		rightEntity2.setCpdEntry("CPD-123457R"); rightEntity2.setBioCycReactionEntity(reaction);
		reaction.getLeft().add(leftEntity1);
		reaction.getLeft().add(leftEntity2);
		reaction.getRight().add(rightEntity1);
		reaction.getRight().add(rightEntity2);
		
		BioCycReactionEcNumberEntity ecNumberEntity1 = new BioCycReactionEcNumberEntity();
		ecNumberEntity1.setBioCycReactionEntity(reaction);
		ecNumberEntity1.setOfficial(true);
		ecNumberEntity1.setEcNumber("127.0.0.0");
		BioCycReactionEcNumberEntity ecNumberEntity2 = new BioCycReactionEcNumberEntity();
		ecNumberEntity2.setBioCycReactionEntity(reaction);
		ecNumberEntity2.setOfficial(false);
		ecNumberEntity2.setEcNumber("168.192.0.1");
		
		reaction.getEcNumbers().add(ecNumberEntity1);
		reaction.getEcNumbers().add(ecNumberEntity2);
		
		BioCycReactionCrossReferenceEntity crossReferenceEntity1 = new BioCycReactionCrossReferenceEntity();
		crossReferenceEntity1.setType(GenericCrossReference.Type.DATABASE);
		crossReferenceEntity1.setRef("AnotherDb");
		crossReferenceEntity1.setValue("R123456");
		crossReferenceEntity1.setBioCycReactionEntity(reaction);

		reaction.getCrossreferences().add(crossReferenceEntity1);
		
		reactionDao.saveReaction(reaction);
		
		System.out.println(reaction);
		
		assertNotEquals(null, reaction.getId());
		assertEquals("RXN-12345", reaction.getEntry());
	}
}
