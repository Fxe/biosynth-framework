package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynth.integration.strategy.metabolite.CrossreferenceTraversalStrategyImpl;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.service.OldMetaboliteIntegrationServiceImpl;
import edu.uminho.biosynth.core.data.integration.generator.PrefixKeyGenerator;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

public class TestHbmChimeraService {

	private static SessionFactory sessionFactory;
	private static String DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static GraphDatabaseService db;
	private static org.hibernate.Transaction meta_tx;
	private static org.neo4j.graphdb.Transaction data_tx;
	private static OldMetaboliteIntegrationServiceImpl integrationService;
	private static Neo4jChimeraDataDaoImpl data;
	private static HbmIntegrationMetadataDaoImpl meta;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hbm_mysql_chimera_meta.cfg.xml");
		db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		sessionFactory.openSession();
		meta = new HbmIntegrationMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(db);
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
		integrationService = new  OldMetaboliteIntegrationServiceImpl();
		integrationService.setClusterIdGenerator(new PrefixKeyGenerator("GEN"));
		integrationService.setMeta(meta);
		integrationService.setData(data);
	}

	@After
	public void tearDown() throws Exception {
		meta_tx.commit();
		data_tx.success();
		data_tx.close();
	}

//	@Test
	public void testCreateSingleCluster() {
		OldMetaboliteIntegrationServiceImpl integrator = new OldMetaboliteIntegrationServiceImpl();
		Neo4jChimeraDataDaoImpl data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(db);
		HbmIntegrationMetadataDaoImpl meta = new HbmIntegrationMetadataDaoImpl();
		integrator.setClusterIdGenerator(new PrefixKeyGenerator("GEN_"));
		
		meta.setSessionFactory(sessionFactory);
		integrator.setData(data);
		integrator.setMeta(meta);
		IntegrationSet integrationSet = integrator.createIntegrationSet(
				"TestService_SingleCluster_" + System.currentTimeMillis(), "Created by Service");
		
//		integrator.changeIntegrationSet(integrationSet.getId());
		
		

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
	
//	@Test
	public void testCreateClusterByCascade() {
		IntegrationSet integrationSet = integrationService.createIntegrationSet(
				"TestService_CascadeCluster_" + System.currentTimeMillis(), "Created by Service");
//		integrationSet = integrationService.changeIntegrationSet(integrationSet.getId());
		integrationService.resetIntegrationSet(integrationSet);
		
		try {
			//START cpd=node(0) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))
			integrationService.createClusterCascade("START cpd=node(%d) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))");
			
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
	}

//	@Test
	public void testCreateClusterByClusteringStrategy() {
		IntegrationSet integrationSet = integrationService.createIntegrationSet(
				"TestService_CascadeCluster_" + System.currentTimeMillis(), "Created by Service");
//		integrationSet = integrationService.changeIntegrationSet(integrationSet.getId());
		integrationService.resetIntegrationSet(integrationSet);
		
		try {
			//START cpd=node(0) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))
			CrossreferenceTraversalStrategyImpl strategy = new CrossreferenceTraversalStrategyImpl(db);
			System.out.println(db.getNodeById(0).getProperty("entry"));
			strategy.setInitialNode(db.getNodeById(0));
			integrationService.mergeCluster(strategy);
			
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
	}
	
//	@Test
	public void testCreateClusterByClusteringStrategyCascade() {
		IntegrationSet integrationSet = integrationService.createIntegrationSet(
				"TestService_CascadeCluster_Cascade_" + System.currentTimeMillis(), "Created by Test Unit");
//		integrationSet = integrationService.changeIntegrationSet(integrationSet.getId());
		integrationService.resetIntegrationSet(integrationSet);
		
		try {
			//START cpd=node(0) WITH cpd MATCH path=(cpd)-[:HasCrossreferenceTo*1..10]-(x:Compound) RETURN collect(distinct ID(x))
			CrossreferenceTraversalStrategyImpl strategy = new CrossreferenceTraversalStrategyImpl(db);
			
			//Get Nodes To Apply Map
			List<Node> compoundNodes = IteratorUtil.asList(
					GlobalGraphOperations.at(db)
					.getAllNodesWithLabel(CompoundNodeLabel.Compound));
			List<Long> elementsToCascade = new ArrayList<> ();
			for (Node n: compoundNodes) elementsToCascade.add(n.getId());
			List<IntegratedCluster> res = integrationService.createClusterCascade(strategy, elementsToCascade);
			System.out.println(res.size());
		} catch (Exception e) {
			db.shutdown();
			throw e;
		}
	}
	
//	@Test
//	public void testSplitCluster() {
//		integrationService.changeIntegrationSet(1L);
//		integrationService.splitClusterByProperty(64564L, "Name", "name");
//	}
	
	@Test
	public void testUpdateCluster() {
//		integrationService.changeIntegrationSet(1L);
		IntegratedCluster cluster = meta.getIntegratedClusterById(3L);
		System.out.println(cluster);
		System.out.println(cluster.getMembers());

		Set<Long> values = new HashSet<> ();
		values.add(167084L);
		values.add(757391L);
		values.add(167107L);
		integrationService.updateCluster(3L, "CPD_2", "CHANGED !", values);
		
	}
	
	@Test
	public void testSplitCluster() {
//		integrationService.changeIntegrationSet(1L);
//		Set<Long> values = new HashSet<> ();
//		values.add(167615L);
//		values.add(204271L);
//		integrationService.splitCluster(89L, values, "SPLITED", "NEW_GUY");
	}
	
//	@Test
//	public void basic() {
//		IntegrationSet integrationSet = new IntegrationSet();
//		integrationSet.setName("SET_ABC");
//		integrationSet.setDescription("A B C");
//		
//		s
//	}
}
