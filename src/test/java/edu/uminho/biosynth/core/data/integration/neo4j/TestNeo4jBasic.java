package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import java.io.File;
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
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.biodb.bigg.CsvBiggMetaboliteDaoImpl;
import edu.uminho.biosynth.core.data.io.remote.BioCycRemoteSource;
import edu.uminho.biosynth.core.data.io.remote.KeggRemoteSource;

public class TestNeo4jBasic {

//	private static String DB_PATH = "D:/home/data/neo4j/integration";
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

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private String nullToString(Object obj) {
		return obj==null?"null":obj.toString();
	}
	
	@Test
	public void testBasic() {
		ExecutionEngine engine = new ExecutionEngine(db);
		ExecutionResult result;
		
		try ( Transaction tx = db.beginTx()) {
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("id", "A");
			attributes.put("uuid", System.currentTimeMillis());
			attributes.put("foo", nullToString(null));
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("attributes", attributes);

			result = engine.execute("CREATE (n:Test {attributes})", params);

			assertEquals(false, result.iterator().hasNext());
			result = engine.execute("MATCH (n:Test) RETURN n");
			Iterator<Node> nodes = result.columnAs("n");
			List<Node> nodeList = IteratorUtil.asList(nodes); 
			
			for (Node o : nodeList) {
				System.out.println(o.getId());
				if (o.hasProperty("uuid")) System.out.println(o.getProperty("uuid"));
			}
			assertEquals(true, nodeList.size() > 0);
			
			engine.execute("MATCH (n:Test) DELETE n");
			
			
			tx.success();
		}
	}

	@Test
	public void testKegg() {
		KeggRemoteSource.LOCALCACHE = "D:/home/data/kegg";
		KeggRemoteSource keggRemoteDao = new KeggRemoteSource();
		Neo4jKeggCompoundMetaboliteDaoImpl keggDao = new Neo4jKeggCompoundMetaboliteDaoImpl(db);
		
		try ( Transaction tx = db.beginTx()) {
			KeggCompoundMetaboliteEntity keggCpd1 = keggRemoteDao.getMetaboliteInformation("C00001");
			System.out.println(keggCpd1);
			keggDao.save(keggCpd1);
			KeggCompoundMetaboliteEntity keggCpd2 = keggRemoteDao.getMetaboliteInformation("C16844");
			System.out.println(keggCpd2);
			keggDao.save(keggCpd2);
			KeggCompoundMetaboliteEntity keggCpd3 = keggRemoteDao.getMetaboliteInformation("C01328");
			System.out.println(keggCpd3);
			keggDao.save(keggCpd3);
			KeggCompoundMetaboliteEntity keggCpd4 = keggRemoteDao.getMetaboliteInformation("C00226");
			System.out.println(keggCpd4);
			keggDao.save(keggCpd4);
			KeggCompoundMetaboliteEntity keggCpd5 = keggRemoteDao.getMetaboliteInformation("C06142");
			System.out.println(keggCpd5);
			keggDao.save(keggCpd5);
			tx.success();
		}
	}
	
	@Test
	public void testBigg() {
		File csv = new File("D:/home/data/bigg/BiGGmetaboliteList.tsv");
		CsvBiggMetaboliteDaoImpl biggCsvDao = new CsvBiggMetaboliteDaoImpl();
		biggCsvDao.setCsvFile(csv);
		Neo4jBiggMetaboliteDaoImpl biggNeo4jDao = new Neo4jBiggMetaboliteDaoImpl(db);
		
		try ( Transaction tx = db.beginTx()) {
			BiggMetaboliteEntity biggCpd1 = biggCsvDao.find("h2o");
			System.out.println(biggCpd1);
			biggNeo4jDao.save(biggCpd1);
			BiggMetaboliteEntity biggCpd2 = biggCsvDao.find("btal");
			System.out.println(biggCpd2);
			biggNeo4jDao.save(biggCpd2);
			BiggMetaboliteEntity biggCpd3 = biggCsvDao.find("btcoa");
			System.out.println(biggCpd3);
			biggNeo4jDao.save(biggCpd3);
			tx.success();
		}
	}
	
	@Test
	public void testMetaCyc() {
		BioCycRemoteSource.LOCALCACHE = "D:/home/data/biocyc";
		BioCycRemoteSource biocycRemoteDao = new BioCycRemoteSource("META");
		Neo4jBioCycMetaboiteDaoImpl biocycNeo4jDao = new Neo4jBioCycMetaboiteDaoImpl(db);
		
		try ( Transaction tx = db.beginTx()) {
			BioCycMetaboliteEntity cpd1 = biocycRemoteDao.getMetaboliteInformation("BUTANAL");
			System.out.println(cpd1);
			biocycNeo4jDao.save(cpd1);
			BioCycMetaboliteEntity cpd2 = biocycRemoteDao.getMetaboliteInformation("BUTANOL");
			System.out.println(cpd2);
			biocycNeo4jDao.save(cpd2);
			BioCycMetaboliteEntity cpd3 = biocycRemoteDao.getMetaboliteInformation("Medium-Chain-Alcohols");
			System.out.println(cpd3);
			biocycNeo4jDao.save(cpd3);
			BioCycMetaboliteEntity cpd4 = biocycRemoteDao.getMetaboliteInformation("Primary-Alcohols");
			System.out.println(cpd4);
			biocycNeo4jDao.save(cpd4);
			tx.success();
		}
		
		
		
	}
}
