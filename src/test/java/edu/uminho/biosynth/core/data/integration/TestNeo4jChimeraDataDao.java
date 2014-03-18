package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;

public class TestNeo4jChimeraDataDao {
	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db";
	private static GraphDatabaseService db;
	private static org.neo4j.graphdb.Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		tx = db.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		tx.success();
		tx.close();
	}

	@Test
	public void testAllMetaboliteIds() {
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphdb(db);
		
		assertEquals(false, data.getAllMetaboliteIds().isEmpty());
	}

}
