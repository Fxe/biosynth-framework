/**
 * 
 */
package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author Filipe
 *
 */
public class TestCanSmilesStrategy {

	private static final String CENTRAL_DATA = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static GraphDatabaseService graphDatabaseService;
	private static org.neo4j.graphdb.Transaction neo_tx;
	
	/**
	 * Initialize test unit
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(CENTRAL_DATA);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graphDatabaseService.shutdown();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		neo_tx = graphDatabaseService.beginTx();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		neo_tx.success();
		neo_tx.close();
	}

	@Test
	public void testGenerateClusterSuccess1() {
		//1237463 BUTANAL CanSMILES CCCC=O
		CanSmileClusterStrategy strategy = 
				new CanSmileClusterStrategy(graphDatabaseService);
		strategy.setInitialNode(1236473L);
		Set<Long> compoundNodeIds = strategy.execute();
		List<String> entries = new ArrayList<> ();
		
		for (Long id:compoundNodeIds) {
			entries.add((String)graphDatabaseService.getNodeById(id).getProperty("entry"));
		}
		
		assertEquals(true, entries.contains("MNXM1017"));
		assertEquals(true, entries.contains("C01412"));
		assertEquals(true, entries.contains("BUTANAL"));
		assertEquals(true, entries.contains("15743"));
	}

	@Test
	public void testGenerateClusterSuccess2() {
		//1251857 WATER CanSMILES O
		CanSmileClusterStrategy strategy = 
				new CanSmileClusterStrategy(graphDatabaseService);
		strategy.setInitialNode(1251857L);
		Set<Long> compoundNodeIds = strategy.execute();
		List<String> entries = new ArrayList<> ();
		
		for (Long id:compoundNodeIds) {
			entries.add((String)graphDatabaseService.getNodeById(id).getProperty("entry"));
		}

		assertEquals(true, entries.contains("MNXM2"));
		assertEquals(true, entries.contains("15377"));
		assertEquals(true, entries.contains("C00001"));
		assertEquals(true, entries.contains("D00001"));
		assertEquals(true, entries.contains("WATER"));
	}
}
