package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestNeo4jGraphReactionDaoImpl {

	private final static String NEO_DATA_DB_PATH = "D:/tmp/testtt.db";
	
	private static GraphDatabaseService graphDatabaseService;
	private static Neo4jGraphReactionDaoImpl neo4jGraphReactionDaoImpl;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDataDatabaseConstraints(NEO_DATA_DB_PATH);
		neo4jGraphReactionDaoImpl = new Neo4jGraphReactionDaoImpl(graphDatabaseService);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graphDatabaseService.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		tx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		tx.failure();
		tx.close();
	}

	@Test
	public void test_save_reaction_entity_minimal() {
		GraphReactionEntity reaction = new SomeNodeFactory()
			.withEntry("RX0100")
			.buildGraphReactionEntity(ReactionMajorLabel.LigandReaction);
		neo4jGraphReactionDaoImpl.saveReaction("", reaction);
		assertNotNull(reaction.getId());
	}

	@Test
	public void test_save_reaction_entity_with_no_connected_link() {
		GraphReactionEntity reaction = new SomeNodeFactory()
			.withEntry("RX0101")
			.withProperty("name", "enzymase")
			.withProperty("comment", "no comments")
			.buildGraphReactionEntity(ReactionMajorLabel.LigandReaction);
		neo4jGraphReactionDaoImpl.saveReaction("", reaction);
		assertNotNull(reaction.getId());
	}
	
	@Test
	public void test_get_reaction_entity_with_no_connected_link() {
		GraphReactionEntity reaction = new SomeNodeFactory()
			.withEntry("RY0101")
			.withProperty("name", "enzymase")
			.withProperty("comment", "no comments")
			.buildGraphReactionEntity(ReactionMajorLabel.LigandReaction);
		neo4jGraphReactionDaoImpl.saveReaction("", reaction);
		
		GraphReactionEntity reaction_ = neo4jGraphReactionDaoImpl.getReactionById("", reaction.getId());
		assertEquals(reaction.getId(), reaction_.getId());
		assertEquals(reaction.getName(), reaction_.getName());
		assertEquals(reaction.getProperty("comment", "wut"), reaction_.getProperty("comment", "aww"));
	}
	
	@Test
	public void test_save_reaction_entity_with_component_link() {
		GraphReactionEntity reaction = new SomeNodeFactory()
			.withEntry("RX0101")
			.withProperty("name", "enzymase")
			.withProperty("comment", "no comments")
			.withLeftSoitchiometry("CXX001", MetaboliteMajorLabel.LigandCompound, 1d)
			.withRightSoitchiometry("CXX002", MetaboliteMajorLabel.LigandCompound, 1d)
			.buildGraphReactionEntity(ReactionMajorLabel.LigandReaction);
		neo4jGraphReactionDaoImpl.saveReaction("", reaction);
		assertNotNull(reaction.getId());
	}
}
