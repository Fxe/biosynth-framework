package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModel;
import pt.uminho.sysbio.biosynthframework.io.MetabolicModelDao;

public class TestNeo4jGraphMetabolicModelDaoImpl {

	private static MetabolicModelDao<DefaultMetabolicModel> dao;
	private static GraphDatabaseService graphDatabaseService;
	private static Transaction tx;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDatabase("D:/tmp/data.db");
		dao = new Neo4jGraphMetabolicModelDaoImpl(graphDatabaseService);
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
		tx.failure(); tx.close();
	}

	@Test
	public void test_page_alot() {
		System.out.println(dao.findAll(0, 10000).size());
	}
	
	@Test
	public void test_page_zero() {
		System.out.println(dao.findAll(0, 1));
	}
	
	@Test
	public void test_pagination() {
		System.out.println(dao.findAll(0, 10000).size());
	}

}
