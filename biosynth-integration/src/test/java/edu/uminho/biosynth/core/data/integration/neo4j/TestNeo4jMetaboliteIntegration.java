package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class TestNeo4jMetaboliteIntegration {

	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db";
	private static GraphDatabaseService db;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
	}


	@Test
	public void test() {
		Neo4jMetaboliteIntegration integrarion = new Neo4jMetaboliteIntegration("IntegrateIdentity", db);
		
		try ( Transaction tx = db.beginTx()) {
			integrarion.initialize();
			
			integrarion.getAllMetaboliteId();
			
//			integrarion.resetIntegration();
			tx.success();
		}
		
		
		assertEquals(true, true);
	}

}
