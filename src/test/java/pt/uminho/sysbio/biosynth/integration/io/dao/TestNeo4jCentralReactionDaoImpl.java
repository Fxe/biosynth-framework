package pt.uminho.sysbio.biosynth.integration.io.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.factory.CentralReactionFactory;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jGraphReactionDaoImpl;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestNeo4jCentralReactionDaoImpl {

	private static String DB_PATH = "D:/dev/null/test.db";
	private static GraphDatabaseService db;
	private static org.neo4j.graphdb.Transaction tx;
	private ReactionHeterogeneousDao<GraphReactionEntity> reactionDao;
	
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
		Neo4jGraphReactionDaoImpl daoImpl = new Neo4jGraphReactionDaoImpl(db);
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
		GraphReactionEntity entity = new CentralReactionFactory()
			.withEntry("TES00001")
			.withMajorLabel("LigandReaction")
			.withLabels(new String[]{"Compound", "Test", "Factory"})
			.build();
	
		System.out.println(entity);

		assertNull(entity.getId());
		
		entity = reactionDao.saveReaction(entity.getMajorLabel(), entity);
		
		assertNotNull(entity.getId());
		assertEquals("TES00001", entity.getEntry());
	}
	
	@Test
	public void testSaveReactionFail() {
		GraphReactionEntity entity = new CentralReactionFactory()
			.withEntry("TES00001")
			.withMajorLabel("LigandReaction")
			.withLabels(new String[]{"Compound", "Test", "Factory"})
			.build();
	
		System.out.println(entity);

		assertNull(entity.getId());
		
		entity = reactionDao.saveReaction(entity.getMajorLabel(), entity);
		
		assertNull(entity);
	}

	@Test
	public void testGetAllReactionEntries() {
		List<String> entries = reactionDao.getAllReactionEntries("LigandReaction");
		
		assertNotNull(entries);
		assertEquals(1, entries.size());
	}
	
	@Test
	public void testGetAllReactionIds() {
		List<Long> ids = reactionDao.getAllReactionIds("LigandReaction");
		
		assertNotNull(ids);
		assertEquals(1, ids.size());
	}
	
	@Test
	public void testGetAllReactionEntriesEmpty() {
		List<String> entries = reactionDao.getAllReactionEntries("LigandReaction");
		
		assertNotNull(entries);
		assertEquals(0, entries.size());
	}
	
	@Test
	public void testGetAllReactionIdsEmpty() {
		List<Long> ids = reactionDao.getAllReactionIds("LigandReaction");
		
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}
}
