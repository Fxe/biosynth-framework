package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import edu.uminho.biosynth.core.data.integration.chimera.dao.HbmChimeraMetadataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.service.ChimeraIntegrationServiceImpl;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class TestHbmChimeraService {

	private static SessionFactory sessionFactory;
	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db";
	private static GraphDatabaseService db;
	private static org.hibernate.Transaction meta_tx;
	private static org.neo4j.graphdb.Transaction data_tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hbm_mysql_chimera_meta.cfg.xml");
		db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		sessionFactory.openSession();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.getCurrentSession().close();
		sessionFactory.close();
		db.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		meta_tx = sessionFactory.getCurrentSession().beginTransaction();
		data_tx = db.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		meta_tx.commit();
		data_tx.success();
		data_tx.close();
	}

	@Test
	public void testCreateSingleCluster() {
		ChimeraIntegrationServiceImpl integrator = new ChimeraIntegrationServiceImpl();
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphdb(db);
		HbmChimeraMetadataDaoImpl meta = new HbmChimeraMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		integrator.setData(data);
		integrator.setMeta(meta);
		
		integrator.changeIntegrationSet(1L);
		integrator.createNewIntegrationSet("TestService_" + System.currentTimeMillis(), "Created by Service");
		
		try {
			//apply some rule to generate a cluster !
			integrator.createCluster("MATCH path=(cpd:BiGG {entry:\"h2o\"})-[:HasCrossreferenceTo*1..5]-(x:Compound) RETURN collect(distinct ID(x))");
//			integrator.createCluster("MATCH path=(cpd:BiGG {entry:\"h2o\"})-[:HasCrossreferenceTo*]-(:Compound) RETURN nodes(path)");
//			integrator.generateIntegratedDatabase();
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testCreateClusterByCascade() {
		ChimeraIntegrationServiceImpl integrator = new ChimeraIntegrationServiceImpl();
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphdb(db);
		HbmChimeraMetadataDaoImpl meta = new HbmChimeraMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		integrator.setData(data);
		integrator.setMeta(meta);
		
		integrator.changeIntegrationSet(1L);
		
		try {
			//START cpd=node(0) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))
			integrator.createClusterCascade("START cpd=node(%d) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))");
			
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
	}

}
