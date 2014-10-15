package edu.uminho.biosynth.core.data.integration.chimera.service;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.StaticMapClusterStrategy;
import edu.uminho.biosynth.core.data.integration.generator.PrefixKeyGenerator;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCascadeIntegrationServiceImpl {


	private static SessionFactory sessionFactory;
	private static final String GRAPH_DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static final String HBM_CFG = "D:/home/data/java_config/hbm_mysql_chimera_meta_testing.cfg.xml";
	private static final String INTEGRATION_ENTRY = "CASCADE_TEST";
	private static GraphDatabaseService graphDatabaseService;
	private static org.hibernate.Transaction meta_tx;
	private static org.neo4j.graphdb.Transaction data_tx;
	private static OldMetaboliteIntegrationServiceImpl integrationService;
	private static Neo4jChimeraDataDaoImpl data;
	private static HbmIntegrationMetadataDaoImpl meta;
	
	private static Set<Set<Long>> create = new HashSet<> ();
	private static Set<Set<Long>> update = new HashSet<> ();
	private static Set<Long> domain = new HashSet<> ();
	
	private void printIntegrationState(IntegrationSet integrationSet, String message) {
		System.out.println(String.format("################ %s ################", message.trim()));
		if (integrationSet == null) {
			System.out.println("Integration Set Not Found");
		} else {
			System.out.println(integrationSet);
			for (Long cid : integrationSet.getIntegratedClustersMap().keySet()) {
				System.out.println(String.format("\t[%d] - %s", cid, meta.getIntegratedClusterById(cid).listAllIntegratedMemberIds()));
			}
		}
	}
	
	private static void printNode(Long id) {
		Node node = graphDatabaseService.getNodeById(id);
		System.out.println(String.format("[%d]%s - %s", id, node.getLabels(), node.getProperty("entry")));
	}
	
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
		integrationService.setClusterIdGenerator(new PrefixKeyGenerator("TEST_CASCADE"));
		
		data_tx = graphDatabaseService.beginTx();
		
		TreeSet<Long> metaboliteId = new TreeSet<> (data.getAllMetaboliteIds());
		Long[] idArray = metaboliteId.toArray(new Long[0]);
		domain.addAll(metaboliteId);
		int c = 0;
		for (int j = 0; j < 10; j++) {
			Set<Long> eids = new HashSet<> ();
			Set<Long> eids_up = new HashSet<> ();
			for (int i = 0; i < 4; i++) {
				eids.add(idArray[c]);
				c++;
			}
			eids_up.addAll(eids);
			eids_up.add(idArray[c]);
			update.add(eids_up);
			create.add(eids);
			c++;
		}
		
		data_tx.success();
		data_tx.close();
		
		System.out.println(create);
		System.out.println(update);
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
		meta_tx.commit();
		data_tx.success();
	}
	
	@Test
	public void testSetA1_Create_Integration() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry(INTEGRATION_ENTRY);
		if (integrationSet != null) {
			integrationService.deleteIntegrationSet(integrationSet);
			meta_tx.commit();
			meta_tx = sessionFactory.getCurrentSession().beginTransaction();
		}
		
		integrationSet = integrationService.createIntegrationSet(INTEGRATION_ENTRY, "TEST");
		
		printIntegrationState(integrationSet, "A1 Created");
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testSetA2_Cascade_Create_Integration() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry(INTEGRATION_ENTRY);
		assertNotEquals(null, integrationSet);
		
		StaticMapClusterStrategy staticMapClusterStrategy = new StaticMapClusterStrategy();
		staticMapClusterStrategy.setGraphDatabaseService(graphDatabaseService);
		staticMapClusterStrategy.setClusters(create);
		Set<Long> initial = new HashSet<> ();
		for (Set<Long> set : create) {
			initial.addAll(set);
		}
		integrationService.createCluster(integrationSet, staticMapClusterStrategy, initial, domain, ConflictDecision.ABORT, null);
		
		printIntegrationState(integrationSet, "A2 Cascade Create");
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testSetA3_Cascade_Merge_Abort_Integration() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry(INTEGRATION_ENTRY);
		assertNotEquals(null, integrationSet);
		
		StaticMapClusterStrategy staticMapClusterStrategy = new StaticMapClusterStrategy();
		staticMapClusterStrategy.setGraphDatabaseService(graphDatabaseService);
		Set<Long> initial = new HashSet<> ();
		for (Set<Long> set : create) {
			initial.addAll(set);
		}
		Set<Set<Long>> singletonCluster = new HashSet<> ();
		singletonCluster.add(initial);
		staticMapClusterStrategy.setClusters(singletonCluster);
		integrationService.createCluster(integrationSet, staticMapClusterStrategy, initial, domain, ConflictDecision.ABORT, null);
		
		printIntegrationState(integrationSet, "A3 Cascade Merge All Abort");
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testSetA41_Cascade_Update_Abort_Integration() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry(INTEGRATION_ENTRY);
		assertNotEquals(null, integrationSet);
		
		StaticMapClusterStrategy staticMapClusterStrategy = new StaticMapClusterStrategy();
		staticMapClusterStrategy.setGraphDatabaseService(graphDatabaseService);
		Set<Long> initial = new HashSet<> ();
		for (Set<Long> set : update) {
			initial.addAll(set);
		}
		staticMapClusterStrategy.setClusters(update);
		integrationService.createCluster(integrationSet, staticMapClusterStrategy, initial, domain, ConflictDecision.ABORT, null);
		
		printIntegrationState(integrationSet, "A41 Cascade Update Abort");
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testSetA42_Cascade_Update_Integration() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry(INTEGRATION_ENTRY);
		assertNotEquals(null, integrationSet);
		
		StaticMapClusterStrategy staticMapClusterStrategy = new StaticMapClusterStrategy();
		staticMapClusterStrategy.setGraphDatabaseService(graphDatabaseService);
		Set<Long> initial = new HashSet<> ();
		for (Set<Long> set : update) {
			initial.addAll(set);
		}
		staticMapClusterStrategy.setClusters(update);
		integrationService.createCluster(integrationSet, staticMapClusterStrategy, initial, domain, ConflictDecision.UPDATE, null);
		
		printIntegrationState(integrationSet, "A42 Cascade Update");
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testSetA99_Cascade_Merge_All_Integration() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry(INTEGRATION_ENTRY);
		assertNotEquals(null, integrationSet);
		
		StaticMapClusterStrategy staticMapClusterStrategy = new StaticMapClusterStrategy();
		staticMapClusterStrategy.setGraphDatabaseService(graphDatabaseService);
		Set<Long> initial = new HashSet<> ();
		for (Set<Long> set : create) {
			initial.addAll(set);
		}
		Set<Set<Long>> singletonCluster = new HashSet<> ();
		singletonCluster.add(initial);
		staticMapClusterStrategy.setClusters(singletonCluster);
		integrationService.createCluster(integrationSet, staticMapClusterStrategy, initial, domain, ConflictDecision.MERGE, null);
		
		printIntegrationState(integrationSet, "A99 Cascade Merge All");
		assertNotEquals(null, integrationSet.getId());
	}
}
