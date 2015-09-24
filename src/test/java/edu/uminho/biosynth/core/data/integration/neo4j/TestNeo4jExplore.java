package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;

public class TestNeo4jExplore {

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
	public void testBiGG() {
//		File csv = new File("D:/home/data/bigg/BiGGmetaboliteList.tsv");
//		CsvBiggMetaboliteDaoImpl daoCsv = new CsvBiggMetaboliteDaoImpl();
//		daoCsv.setBiggMetaboliteTsv(csv);
		
		Neo4jBiggMetaboliteDaoImpl dao = new Neo4jBiggMetaboliteDaoImpl(db);
		
		
		BiggMetaboliteEntity cpd = null;
		try (Transaction tx = db.beginTx()) {
			cpd = dao.find(33474);
			System.out.println(cpd);
			System.out.println(dao.getAllMetaboliteIds());
			tx.success();
		}
		
		assertEquals(true, cpd != null);
	}

}
