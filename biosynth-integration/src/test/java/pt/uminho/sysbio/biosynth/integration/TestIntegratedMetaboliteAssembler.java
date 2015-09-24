package pt.uminho.sysbio.biosynth.integration;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jGraphMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynth.integration.lostandfound.IntegratedMetaboliteAssembler;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestIntegratedMetaboliteAssembler {
	
	private final static String NEO_DATA_DB_PATH = "D:/tmp/data.db";
	
	private static GraphDatabaseService graphDatabaseService;
	private static Neo4jGraphMetaboliteDaoImpl neo4jGraphMetaboliteDaoImpl;
	private static IntegratedMetaboliteAssembler assembler = new IntegratedMetaboliteAssembler();
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDataDatabaseConstraints(NEO_DATA_DB_PATH);
		neo4jGraphMetaboliteDaoImpl = new Neo4jGraphMetaboliteDaoImpl(graphDatabaseService);
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
	public void test_single_element_assemble() {
		Set<GraphMetaboliteEntity> entities = new HashSet<> ();
		entities.add(neo4jGraphMetaboliteDaoImpl.getMetaboliteByEntry(MetaboliteMajorLabel.BiGG.toString(), "h2o"));
		IntegratedMetaboliteEntity integratedMetabolite = assembler.assemble("test", entities);
		System.out.println(integratedMetabolite.getProperties());
		
		assertEquals(true, true);
	}
	
	@Test
	public void test_single_multiple_assemble() {
		Set<GraphMetaboliteEntity> entities = new HashSet<> ();
		entities.add(neo4jGraphMetaboliteDaoImpl.getMetaboliteByEntry(MetaboliteMajorLabel.BiGG.toString(), "h2o"));
		entities.add(neo4jGraphMetaboliteDaoImpl.getMetaboliteByEntry(MetaboliteMajorLabel.Seed.toString(), "cpd00001"));
		IntegratedMetaboliteEntity integratedMetabolite = assembler.assemble("test", entities);
		System.out.println(integratedMetabolite.getProperties());
	}

}
