package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestNeo4jGraphReactionDaoImpl {

	private final static String NEO_DATA_DB_PATH = "D:/tmp/testtt.db";
	
	private static GraphDatabaseService graphDatabaseService;
	private static Neo4jGraphReactionDaoImpl neo4jGraphReactionDaoImpl;
	
	private static org.neo4j.graphdb.Transaction dataDbTx;
	
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
		neo4jGraphReactionDaoImpl = new Neo4jGraphReactionDaoImpl(graphDatabaseService);
		dataDbTx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		dataDbTx.failure();
		dataDbTx.close();
	}

	@Test
	public void test() {
		GraphReactionEntity graphReactionEntity = neo4jGraphReactionDaoImpl.getReactionByEntry(ReactionMajorLabel.BiGG.toString(), "CYSTtp");
		System.out.println(graphReactionEntity);
		
		assertEquals("cystathione peroxisomal transport", graphReactionEntity.getName());
	}

}
