package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import java.util.List;

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
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.service.ChimeraIntegrationServiceImpl;
import edu.uminho.biosynth.core.data.integration.chimera.service.CrossreferenceTraversalStrategyImpl;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
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
		data.setGraphDatabaseService(db);
		HbmChimeraMetadataDaoImpl meta = new HbmChimeraMetadataDaoImpl();
		integrator.setClusterIdGenerator(new IKeyGenerator<String>() {
			private Integer base = 0;
			
			@Override
			public void reset() { base = 0;}
			
			@Override
			public String generateKey() { return "GEN_" + base++;}
		});
		
		meta.setSessionFactory(sessionFactory);
		integrator.setData(data);
		integrator.setMeta(meta);
		IntegrationSet integrationSet = integrator.createNewIntegrationSet(
				"TestService_SingleCluster_" + System.currentTimeMillis(), "Created by Service");
		
		integrator.changeIntegrationSet(integrationSet.getId());
		
		

		try {
			//apply some rule to generate a cluster !
			integrator.createCluster("MATCH path=(cpd:BiGG {entry:\"h2o\"})-[:HasCrossreferenceTo*1..5]-(x:Compound) RETURN collect(distinct ID(x))");
//			integrator.createCluster("MATCH path=(cpd:BiGG {entry:\"h2o\"})-[:HasCrossreferenceTo*]-(:Compound) RETURN nodes(path)");
//			integrator.generateIntegratedDatabase();
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
		
//		integrator.resetIntegrationSet();
		
		assertEquals(true, true);
	}
	
	@Test
	public void testCreateClusterByCascade() {
		ChimeraIntegrationServiceImpl integrator = new ChimeraIntegrationServiceImpl();
		integrator.setClusterIdGenerator(new IKeyGenerator<String>() {
			private Integer base = 0;
			
			@Override
			public void reset() { base = 0;}
			
			@Override
			public String generateKey() { return "GEN_" + base++;}
		});
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(db);
		HbmChimeraMetadataDaoImpl meta = new HbmChimeraMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		integrator.setData(data);
		integrator.setMeta(meta);
		IntegrationSet integrationSet = integrator.createNewIntegrationSet(
				"TestService_CascadeCluster_" + System.currentTimeMillis(), "Created by Service");
		integrator.changeIntegrationSet(integrationSet.getId());
		integrator.resetIntegrationSet();
		
		try {
			//START cpd=node(0) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))
			integrator.createClusterCascade("START cpd=node(%d) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))");
			
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
	}

	@Test
	public void testCreateClusterByClusteringStrategy() {
		ChimeraIntegrationServiceImpl integrator = new ChimeraIntegrationServiceImpl();
		integrator.setClusterIdGenerator(new IKeyGenerator<String>() {
			private Integer base = 0;
			
			@Override
			public void reset() { base = 0;}
			
			@Override
			public String generateKey() { return "GEN_" + base++;}
		});
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(db);
		HbmChimeraMetadataDaoImpl meta = new HbmChimeraMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		integrator.setData(data);
		integrator.setMeta(meta);
		IntegrationSet integrationSet = integrator.createNewIntegrationSet(
				"TestService_CascadeCluster_" + System.currentTimeMillis(), "Created by Service");
		integrator.changeIntegrationSet(integrationSet.getId());
		integrator.resetIntegrationSet();
		
		try {
			//START cpd=node(0) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))
			CrossreferenceTraversalStrategyImpl strategy = new CrossreferenceTraversalStrategyImpl();
			strategy.setDb(db);
			System.out.println(db.getNodeById(0).getProperty("entry"));
			strategy.setInitialNode(db.getNodeById(0));
			integrator.mergeCluster(strategy);
			
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
	}
	
	@Test
	public void testCreateClusterByClusteringStrategyCascade() {
		ChimeraIntegrationServiceImpl integrator = new ChimeraIntegrationServiceImpl();
		integrator.setClusterIdGenerator(new IKeyGenerator<String>() {
			private Integer base = 0;
			
			@Override
			public void reset() { base = 0;}
			
			@Override
			public String generateKey() { return "GEN_" + base++;}
		});
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(db);
		HbmChimeraMetadataDaoImpl meta = new HbmChimeraMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		integrator.setData(data);
		integrator.setMeta(meta);
		IntegrationSet integrationSet = integrator.createNewIntegrationSet(
				"TestService_CascadeCluster_Cascade_" + System.currentTimeMillis(), "Created by Test Unit");
		integrator.changeIntegrationSet(integrationSet.getId());
		integrator.resetIntegrationSet();
		
		try {
			//START cpd=node(0) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))
			CrossreferenceTraversalStrategyImpl strategy = new CrossreferenceTraversalStrategyImpl();
			strategy.setDb(db);
			List<IntegratedCluster> res = integrator.createClusterCascade(strategy);
			System.out.println(res.size());
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
	}
}
