package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.cypher.internal.PathImpl;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.collection.IteratorUtil;

public class TestNeo4jIntegration {
	
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
		
		try ( Transaction tx = db.beginTx()) {
			ExecutionEngine engine = new ExecutionEngine( db );
			String query = "MATCH path=(start:BiGG {entry:\"h2o\"})-[*1..2]-(end:Compound)-[r:HasFormula]->(f:Formula {formula:\"H1O1\"}) RETURN path LIMIT 1";
			
			Iterator<Path> iterator = engine.execute(query).columnAs("path");
			List<Path> listOfNodes = IteratorUtil.asList(iterator);
			System.out.println(listOfNodes.getClass());
			for (Path n: listOfNodes) {
				System.out.println(n);
			}
			tx.success();
		}
	}
}
