package pt.uminho.sysbio.biosynth.integration.io.dao;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;

import pt.uminho.sysbio.biosynth.integration.CentralReactionEntity;
import pt.uminho.sysbio.biosynth.integration.factory.CentralReactionFactory;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jCentralReactionDaoImpl;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.ReactionDao;

public class TestNeo4jCentralReactionDaoImpl {

	private static String DB_PATH = "D:/dev/null/test.db";
	private static GraphDatabaseService db;
	private static org.neo4j.graphdb.Transaction tx;
	private ReactionDao<CentralReactionEntity> reactionDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = HelperNeo4jConfigInitializer.initializeNeo4jDatabaseConstraints(DB_PATH);
		tx = db.beginTx();
		System.out.println("N:" + IteratorUtil.asList(GlobalGraphOperations.at(db).getAllNodes()));
		System.out.println("L:" + IteratorUtil.asList(GlobalGraphOperations.at(db).getAllLabels()));
		tx.success();
		tx.close();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		Neo4jCentralReactionDaoImpl daoImpl = new Neo4jCentralReactionDaoImpl();
		daoImpl.setGraphDatabaseService(db);
		reactionDao = daoImpl;
		tx = db.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		tx.success();
		tx.close();
	}

	@Test
	public void testSaveReactionSuccess() {
		CentralReactionEntity entity = new CentralReactionFactory()
			.withEntry("TES00001")
			.withMajorLabel("LigandCompound")
			.withLabels(new String[]{"Compound", "Test", "Factory"})
			.build();
	
		System.out.println(entity);

		assertNull(entity.getId());
		
		entity = reactionDao.saveReaction(entity);
		
		assertNotNull(entity.getId());
		assertEquals("TES00001", entity.getEntry());
	}
	
	@Test
	public void testSaveReactionFail() {
		CentralReactionEntity entity = new CentralReactionFactory()
			.withEntry("TES00001")
			.withMajorLabel("LigandCompound")
			.withLabels(new String[]{"Compound", "Test", "Factory"})
			.build();
	
		System.out.println(entity);

		assertNull(entity.getId());
		
		entity = reactionDao.saveReaction(entity);
		
		assertNull(entity);
	}

	@Test
	public void testGetAllReactionEntries() {
		Set<String> entries = reactionDao.getAllReactionEntries();
		
		assertNotNull(entries);
		assertEquals(1, entries.size());
	}
	
	@Test
	public void testGetAllReactionIds() {
		Set<Serializable> ids = reactionDao.getAllReactionIds();
		
		assertNotNull(ids);
		assertEquals(1, ids.size());
	}
	
	@Test
	public void testGetAllReactionEntriesEmpty() {
		Set<String> entries = reactionDao.getAllReactionEntries();
		
		assertNotNull(entries);
		assertEquals(0, entries.size());
	}
	
	@Test
	public void testGetAllReactionIdsEmpty() {
		Set<Serializable> ids = reactionDao.getAllReactionIds();
		
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}
}
