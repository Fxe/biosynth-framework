package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.bigg.CsvBiggMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.remote.KeggRemoteSource;

public class Neo4jLoadDatabases {

	private static String DB_PATH = "D:/home/data/neo4j/integration";
	private static GraphDatabaseService db;
	
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
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void testKegg() {
		KeggRemoteSource.LOCALCACHE = "D:/home/data/kegg";
		KeggRemoteSource keggRemoteDao = new KeggRemoteSource();
		Neo4jKeggMetaboliteDaoImpl keggDao = new Neo4jKeggMetaboliteDaoImpl();
		keggDao.setGraphdb(db);
		
		try ( Transaction tx = db.beginTx()) {
			KeggMetaboliteEntity keggCpd1 = keggRemoteDao.getMetaboliteInformation("C00001");
			keggDao.save(keggCpd1);
			KeggMetaboliteEntity keggCpd2 = keggRemoteDao.getMetaboliteInformation("C16844");
			keggDao.save(keggCpd2);
			KeggMetaboliteEntity keggCpd3 = keggRemoteDao.getMetaboliteInformation("C01328");
			keggDao.save(keggCpd3);
			tx.success();
		}
	}
	
	@Test
	public void testBigg() {
		File csv = new File("D:/home/data/bigg/BiGGmetaboliteList.tsv");
		CsvBiggMetaboliteDaoImpl biggCsvDao = new CsvBiggMetaboliteDaoImpl();
		biggCsvDao.setBiggMetaboliteTsv(csv);
		Neo4jBiggMetaboliteDaoImpl biggNeo4jDao = new Neo4jBiggMetaboliteDaoImpl();
		biggNeo4jDao.setGraphdb(db);
		int i = 0;

		List<BiggMetaboliteEntity> batch = new ArrayList<> ();
		for (BiggMetaboliteEntity cpd : biggCsvDao.findAll()) {
			
			batch.add(cpd);
			System.out.println(cpd);
			
			
			if (i % 10 == 0) {
				try ( Transaction tx = db.beginTx()) {
					for (BiggMetaboliteEntity b : batch) biggNeo4jDao.save(b);
					tx.success();
					batch.clear();
				}
				System.out.println(i);
			}
			i++;
		}
		if (!batch.isEmpty()) {
			try ( Transaction tx = db.beginTx()) {
				for (BiggMetaboliteEntity b : batch) biggNeo4jDao.save(b);
				tx.success();
				batch.clear();
			}
		}
		
	}
}
