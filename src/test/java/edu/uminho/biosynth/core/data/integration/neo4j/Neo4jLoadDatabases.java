package edu.uminho.biosynth.core.data.integration.neo4j;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.core.io.FileSystemResource;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.CsvBiggMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.seed.JsonSeedMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.factory.BiggDaoFactory;
import pt.uminho.sysbio.biosynthframework.core.data.io.remote.BioCycRemoteSource;
import pt.uminho.sysbio.biosynthframework.core.data.io.remote.KeggRemoteSource;

public class Neo4jLoadDatabases {

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
	
	@Test
	public void testSeed() {
		File json = new File("D:/home/data/seed/seed.json");
		JsonSeedMetaboliteDaoImpl jsonSeedDao = new JsonSeedMetaboliteDaoImpl(new FileSystemResource(json));
//		jsonSeedDao.setJsonFile(new FileSystemResource(json));
//		jsonSeedDao.initialize();
		
		Neo4jSeedMetaboliteDaoImpl seedNeo4jDao = new Neo4jSeedMetaboliteDaoImpl(db);
		List<String> skipEntries = new ArrayList<> ();
		try ( Transaction tx = db.beginTx()) {
			for (SeedMetaboliteEntity cpd : seedNeo4jDao.findAll()) {
				skipEntries.add(cpd.getEntry());
			}
			tx.success();
		}
		List<SeedMetaboliteEntity> batch = new ArrayList<> ();
		
		int i = 0;
		for (SeedMetaboliteEntity cpd : jsonSeedDao.findAll()) {
			if ( !skipEntries.contains(cpd.getEntry())) batch.add(cpd);
			if (i % 10 == 0) {
				if (!batch.isEmpty())
				try ( Transaction tx = db.beginTx()) {
					for (SeedMetaboliteEntity b : batch) {
						System.out.println("SAVE:" + b.getEntry());
						System.out.println(b.getCrossreferences());
						seedNeo4jDao.save(b);
					}
					tx.success();
					batch.clear();
				}
				System.out.println(i);
			}
			i++;
		}
		if (!batch.isEmpty()) {
			try ( Transaction tx = db.beginTx()) {
				for (SeedMetaboliteEntity b : batch)  seedNeo4jDao.save(b);
				tx.success();
			}
		}
		
		assertEquals(16996, i);
	}

	@Test
	public void testMetaCyc() {
		//BIOCYC needs to translate BiGG xref Value or not ...
		BioCycRemoteSource.LOCALCACHE = "D:/home/data/biocyc";
		BioCycRemoteSource biocycRemoteDao = new BioCycRemoteSource("META");
		Neo4jBioCycMetaboiteDaoImpl biocycNeo4jDao = new Neo4jBioCycMetaboiteDaoImpl(db);
		
		List<String> skipEntries = new ArrayList<> ();
		try ( Transaction tx = db.beginTx()) {
			for (BioCycMetaboliteEntity cpd : biocycNeo4jDao.findAll()) {
				skipEntries.add(cpd.getEntry());
			}
			tx.success();
		}
		
		List<BioCycMetaboliteEntity> batch = new ArrayList<> ();
		int i = 0;
		for (String cpdId : biocycRemoteDao.getAllMetabolitesIds()) {
			System.out.println(cpdId);
			
			if (!skipEntries.contains(cpdId)) batch.add(biocycRemoteDao.getMetaboliteInformation(cpdId));
			if (i % 10 == 0) {
				if (!batch.isEmpty())
				try ( Transaction tx = db.beginTx()) {
					for (BioCycMetaboliteEntity b : batch) {
						System.out.println("SAVE:" + b.getEntry());
						System.out.println(b.getCrossreferences());
						biocycNeo4jDao.save(b);
					}
					tx.success();
					batch.clear();
				}
				System.out.println(i);
			}
			i++;
		}
		if (!batch.isEmpty()) {
			try ( Transaction tx = db.beginTx()) {
				for (BioCycMetaboliteEntity b : batch)  biocycNeo4jDao.save(b);
				tx.success();
			}
		}
		
		assertEquals(10856, i);
	}
	
	@Test
	public void testKegg() {
		KeggRemoteSource.LOCALCACHE = "D:/home/data/kegg";
		KeggRemoteSource keggRemoteDao = new KeggRemoteSource();
		Neo4jKeggCompoundMetaboliteDaoImpl keggNeo4jDao = new Neo4jKeggCompoundMetaboliteDaoImpl(db);
		
		List<String> skipEntries = new ArrayList<> ();
		try ( Transaction tx = db.beginTx()) {
			for (KeggCompoundMetaboliteEntity cpd : keggNeo4jDao.findAll()) {
				skipEntries.add(cpd.getEntry());
			}
			tx.success();
		}
		
		System.out.println(skipEntries);
		List<KeggCompoundMetaboliteEntity> batch = new ArrayList<> ();
		int i = 0;
		for (String cpdId : keggRemoteDao.getAllMetabolitesIds()) {
			System.out.println(cpdId);
			if (!skipEntries.contains(cpdId)) batch.add(keggRemoteDao.getMetaboliteInformation(cpdId));
				
			if (i % 10 == 0) {
				if (!batch.isEmpty())
				try ( Transaction tx = db.beginTx()) {
					for (KeggCompoundMetaboliteEntity b : batch) {
						System.out.println("SAVE:" + b.getEntry());
						keggNeo4jDao.save(b);
					}
					tx.success();
					batch.clear();
				}
				System.out.println(i);
			}
			i++;
		}
		if (!batch.isEmpty()) {
			try ( Transaction tx = db.beginTx()) {
				for (KeggCompoundMetaboliteEntity b : batch) keggNeo4jDao.save(b);
				tx.success();
				batch.clear();
			}
		}
		
		assertEquals(28170, i);
	}
	
	@Test
	public void testBigg() {
		File csv = new File("D:/home/data/bigg/BiGGmetaboliteList.tsv");
		CsvBiggMetaboliteDaoImpl biggCsvDao = new BiggDaoFactory()
			.withFile(csv)
			.buildCsvBiggMetaboliteDao();
		
			
		Neo4jBiggMetaboliteDaoImpl biggNeo4jDao = new Neo4jBiggMetaboliteDaoImpl(db);
		int i = 0;
		int limitSave = 1000;
		int saves = 0;
		List<BiggMetaboliteEntity> batch = new ArrayList<> ();
		List<String> skipEntries = new ArrayList<> ();
		try ( Transaction tx = db.beginTx()) {
			for (BiggMetaboliteEntity cpd : biggNeo4jDao.findAll()) {
				skipEntries.add(cpd.getEntry());
			}
			tx.success();
		}
		for (String cpdEntry : biggCsvDao.getAllMetaboliteEntries()) {
			BiggMetaboliteEntity cpd = biggCsvDao.getMetaboliteByEntry(cpdEntry);
			if ( !skipEntries.contains(cpd.getEntry())) batch.add(cpd);
			System.out.println(cpd.getEntry());
			
			if (i % 10 == 0) {
				if (!batch.isEmpty())
				try ( Transaction tx = db.beginTx()) {
					for (BiggMetaboliteEntity b : batch) {
						System.out.println("SAVE:" + b.getEntry());
						biggNeo4jDao.save(b);
						saves++;
					}
					tx.success();
					batch.clear();
				}
				System.out.println(i);
			}
			i++;
			
			if (saves > limitSave) break;
		}
		if (!batch.isEmpty()) {
			try ( Transaction tx = db.beginTx()) {
				for (BiggMetaboliteEntity b : batch) biggNeo4jDao.save(b);
				tx.success();
				batch.clear();
			}
		}
		assertEquals(2835, i);
	}
}
