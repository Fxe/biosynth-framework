/**
 * 
 */
package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import pt.uminho.sysbio.biosynth.integration.strategy.metabolite.ChebiParentClusteringStrategy;

/**
 * @author Filipe
 *
 */
public class TestClusteringStrategy {

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
	
	private void printCluster(Set<Long> cluster) {
		System.out.println("#####################################");
		for (Long eid : cluster) {
			Node node = graphDatabaseService.getNodeById(eid);
			String formula = node.hasProperty("formula")?(String)node.getProperty("formula"):null;
			Integer charge = node.hasProperty("charge")?(Integer)node.getProperty("charge"):null;
			System.out.println(String.format("%d\t%s\t%s\t%s\t%d", 
					eid, node.getLabels(), node.getProperty("entry"), formula, charge));
		}
		System.out.println("#####################################");
	}
	
	@Test
	public void generateH2oCluster() {
		Node initialNode = graphDatabaseService.getNodeById(5533L);
		BiggCompoundMatcherStrategy strategy = 
				new BiggCompoundMatcherStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
	}
	
	@Test
	public void generateNadCluster() {
		Node initialNode = graphDatabaseService.getNodeById(7402L);
		BiggCompoundMatcherStrategy strategy = 
				new BiggCompoundMatcherStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
	}
	
	@Test
	public void generateNedCluster() {
		Node initialNode = graphDatabaseService.getNodeById(29L);
		BiggCompoundMatcherStrategy strategy = 
				new BiggCompoundMatcherStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
	}
	
	@Test
	public void generateOmgCluster() {
		Node initialNode = graphDatabaseService.getNodeById(995L);
		BiggCompoundMatcherStrategy strategy = 
				new BiggCompoundMatcherStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
	}
	
	@Test
	public void generateOh1Cluster() {
		Node initialNode = graphDatabaseService.getNodeById(7645L);
		BiggCompoundMatcherStrategy strategy = 
				new BiggCompoundMatcherStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
	}
	
	@Test
	public void generateChEBI_16908_Main() {
		Node initialNode = graphDatabaseService.getNodeById(167069L);
		ChebiParentClusteringStrategy strategy = 
				new ChebiParentClusteringStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
		
		assertEquals(6, result.size());
	}
	
	@Test
	public void generateChEBI_7423_Parent_16908() {
		Node initialNode = graphDatabaseService.getNodeById(167071L);
		ChebiParentClusteringStrategy strategy = 
				new ChebiParentClusteringStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		
		assertEquals(0, result.size());
	}
	
	@Test
	public void generateMetaCycWATER_Cluster() {
		Node initialNode = graphDatabaseService.getNodeById(436046L);
		BiocycFirstDegreeCrossreferenceClusteringStrategy strategy = 
				new BiocycFirstDegreeCrossreferenceClusteringStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
	}
	
	@Test
	public void generateMetaCycPROTON_Cluster() {
		Node initialNode = graphDatabaseService.getNodeById(167042L);
		BiocycFirstDegreeCrossreferenceClusteringStrategy strategy = 
				new BiocycFirstDegreeCrossreferenceClusteringStrategy(graphDatabaseService);
		strategy.setInitialNode(initialNode);
		
		Set<Long> result = strategy.execute();
		this.printCluster(result);
	}
	
//	@Test
//	public void generateAllCluster() {
//		for (Node biggNode : GlobalGraphOperations.at(graphDatabaseService)
//				.getAllNodesWithLabel(CompoundNodeLabel.BiGG)) {
//			Node initialNode = biggNode;
//			BiggCompoundMatcherStrategy strategy = new BiggCompoundMatcherStrategy();
//			strategy.setDb(graphDatabaseService);
//			strategy.setInitialNode(initialNode);
//			
//			Set<Long> result = strategy.execute();
//			this.printCluster(result);
//		}
//	}
}
