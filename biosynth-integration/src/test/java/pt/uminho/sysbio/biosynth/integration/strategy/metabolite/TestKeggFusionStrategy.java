package pt.uminho.sysbio.biosynth.integration.strategy.metabolite;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestKeggFusionStrategy {

	private static GraphDatabaseService graphDatabaseService;
	private static Transaction tx;
	private static KeggFusionStrategy strategy;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDatabase("D:/tmp/data.db");
		strategy = new KeggFusionStrategy(graphDatabaseService);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (graphDatabaseService != null) graphDatabaseService.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		tx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		if (tx != null) {
			tx.failure();
			tx.close();
		}
	}

	@Test
	public void test_C00001_water() {
		strategy.setInitialNode(Neo4jUtils.getUniqueResult(graphDatabaseService.findNodesByLabelAndProperty(MetaboliteMajorLabel.LigandCompound, "entry", "C00001")));
		Set<Long> result = strategy.execute();
		System.out.println(result);
	}
	
	@Test
	public void test_C00007_oxygen() {
		strategy.setInitialNode(Neo4jUtils.getUniqueResult(graphDatabaseService.findNodesByLabelAndProperty(MetaboliteMajorLabel.LigandCompound, "entry", "C00007")));
		Set<Long> result = strategy.execute();
		System.out.println(result);
	}
	
	@Test
	public void test_C00003_nad() {
		strategy.setInitialNode(Neo4jUtils.getUniqueResult(graphDatabaseService.findNodesByLabelAndProperty(MetaboliteMajorLabel.LigandCompound, "entry", "C00003")));
		Set<Long> result = strategy.execute();
		System.out.println(result);
	}

}
