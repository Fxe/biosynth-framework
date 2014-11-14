package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;

public class TestNeo4jGraphMetaboliteDaoImpl {

	private static GraphDatabaseService graphDatabaseService;
	private static Neo4jGraphMetaboliteDaoImpl neo4jGraphMetaboliteDaoImpl;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
//		graphDatabaseService = new TestGraphDatabaseFactory().
		neo4jGraphMetaboliteDaoImpl = new Neo4jGraphMetaboliteDaoImpl(graphDatabaseService);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		GraphMetaboliteEntity metabolite = neo4jGraphMetaboliteDaoImpl.saveMetabolite("", new GraphMetaboliteEntity());
		
		assertNotNull(metabolite.getId());
	}

}
