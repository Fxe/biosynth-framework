package edu.uminho.biosynth.core.data.integration.chimera.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.PropertyValueException;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynth.integration.strategy.metabolite.CrossreferenceTraversalStrategyImpl;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.generator.PrefixKeyGenerator;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;


public class TestHbmChimeraService {

	private static SessionFactory sessionFactory;
	private static final String GRAPH_DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static final String HBM_CFG = "D:/home/data/java_config/hbm_mysql_chimera_meta.cfg.xml";
	private static GraphDatabaseService graphDatabaseService;
	private static org.hibernate.Transaction meta_tx;
	private static org.neo4j.graphdb.Transaction data_tx;
	private static OldMetaboliteIntegrationServiceImpl integrationService;
	private static Neo4jChimeraDataDaoImpl data;
	private static HbmIntegrationMetadataDaoImpl meta;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(new File(HBM_CFG));
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDatabaseConstraints(GRAPH_DB_PATH);
		
		data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(graphDatabaseService);
		
		meta = new HbmIntegrationMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		
		integrationService = new OldMetaboliteIntegrationServiceImpl();
		integrationService.setData(data);
		integrationService.setMeta(meta);
		integrationService.setClusterIdGenerator(new PrefixKeyGenerator("TEST"));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
		graphDatabaseService.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		meta_tx = sessionFactory.getCurrentSession().beginTransaction();
		data_tx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		meta_tx.rollback();
		data_tx.success();
	}

	@Test
	public void testCreateIntegrationSet() {
		IntegrationSet integrationSet = 
				integrationService.createIntegrationSet("TEST", "TEST");
		
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test(expected=PropertyValueException.class)
	public void testCreateIntegrationSetAllNull() {
		IntegrationSet integrationSet = 
				integrationService.createIntegrationSet(null, null);
		
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testCreateIntegratedClusterWithTenMembers() {
		IntegrationSet integrationSet = 
				integrationService.createIntegrationSet("TEST", null);
		
		Set<Long> allNodes = new HashSet<> (data.getAllMetaboliteIds());
		
		Set<Long> members = new HashSet<> ();
		Iterator<Long> iterator = allNodes.iterator();
		for (int i = 0; i < 10; i++) {
			members.add(iterator.next());
		}
		
		IntegratedCluster integratedCluster = 
				integrationService.createCluster(integrationSet, "C1", members, "C1", ConflictDecision.ABORT);
		
		assertEquals(10, integratedCluster.getMembers().size());
	}

	@Test
	public void testCreateIntegratedClusterWithZeroMembers() {
		IntegrationSet integrationSet = 
				integrationService.createIntegrationSet("TEST", null);
	
		Set<Long> members = new HashSet<> ();

		IntegratedCluster integratedCluster = 
				integrationService.createCluster(integrationSet, "C1", members, "C1", ConflictDecision.ABORT);
		
		assertEquals(null, integratedCluster);
	}
	
	@Test
	public void testCreateIntegratedClusterWithStrategy() {
		IntegrationSet integrationSet = 
				integrationService.createIntegrationSet("TEST", null);
	
		Set<Long> allNodes = new HashSet<> (data.getAllMetaboliteIds());
		
		
		CrossreferenceTraversalStrategyImpl clusteringStrategy = new CrossreferenceTraversalStrategyImpl(graphDatabaseService);
		
		
		List<IntegratedCluster> integratedClusters = 
				integrationService.createCluster(
						integrationSet, 
						clusteringStrategy, 
						allNodes, 
						allNodes, 
						ConflictDecision.ABORT, 10L);
	
		assertEquals(10, integratedClusters.size());
	}
}