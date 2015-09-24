package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
//	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db";
//	private static GraphDatabaseService db;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		db.shutdown();
//	}
//
////	@Test
//	public void test1() {
//		
//		try ( Transaction tx = db.beginTx()) {
//			ExecutionEngine engine = new ExecutionEngine( db );
//			String query = "MATCH path=(start:BiGG {entry:\"h2o\"})-[*1..2]-(end:Compound)-[r:HasFormula]->(f:Formula {formula:\"H1O1\"}) RETURN path LIMIT 1";
//			
//			Iterator<Path> iterator = engine.execute(query).columnAs("path");
//			List<Path> listOfNodes = IteratorUtil.asList(iterator);
//			System.out.println(listOfNodes.getClass());
//			for (Path n: listOfNodes) {
//				System.out.println(n);
//			}
//			tx.success();
//		}
//	}
//	
//	@Test
//	public void testSpanCompound() {
//		String d  = "BiGG";
//		String e = "btal";
//		Integer r = 2;
//		Integer l = 100;
//		try ( Transaction tx = db.beginTx()) {
//			ExecutionEngine engine = new ExecutionEngine( db );
//			String query = 
//					String.format("MATCH path=(start:%s {entry:\"%s\"})-[r:HasCrossreferenceTo*1..2]-(end:Compound) RETURN path LIMIT %d", 
//							d, e, l);
//			
//			Iterator<Path> iterator = engine.execute(query).columnAs("path");
//			List<Path> listOfNodes = IteratorUtil.asList(iterator);
//			System.out.println(listOfNodes.getClass());
//			Map<Long, String> nodesToString = new HashMap<> ();
//			Set<Long> nodes = new HashSet<> ();
//			List<String> paths = new ArrayList<> ();
//			for (Path n: listOfNodes) {
//				System.out.println(n.length());
//				System.out.println(n);
//				for (Node nn : n.reverseNodes()) {
//					System.out.println(nn);
//					if (!nodes.contains(nn.getId())) {
//						System.out.println("discovered " + nn.getId());
//						nodes.add(nn.getId());
//						nodesToString.put(nn.getId(), nn.getLabels() + nn.getProperty("entry").toString());
//					}
//				}
//				System.out.println();
////				if (nodes.contains(o))
//			}
//			System.out.println(nodesToString);
//			tx.success();
//		}
//	}
}
