package edu.uminho.biosynth.core.data.integration.chimera.service;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.Neo4jChimeraDataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.generator.PrefixKeyGenerator;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestOrderedIntegrationServiceImpl {

	private static SessionFactory sessionFactory;
	private static final String GRAPH_DB_PATH = "D:/opt/neo4j-community-2.1.0-M01/data/graph.db.central";
	private static final String HBM_CFG = "D:/home/data/java_config/hbm_mysql_chimera_meta_testing.cfg.xml";
	private static GraphDatabaseService graphDatabaseService;
	private static org.hibernate.Transaction meta_tx;
	private static org.neo4j.graphdb.Transaction data_tx;
	private static DefaultMetaboliteIntegrationServiceImpl integrationService;
	private static Neo4jChimeraDataDaoImpl data;
	private static HbmIntegrationMetadataDaoImpl meta;
	
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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(new File(HBM_CFG));
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDatabaseConstraints(GRAPH_DB_PATH);
		
		data = new Neo4jChimeraDataDaoImpl();
		data.setGraphDatabaseService(graphDatabaseService);
		
		meta = new HbmIntegrationMetadataDaoImpl();
		meta.setSessionFactory(sessionFactory);
		
		integrationService = new DefaultMetaboliteIntegrationServiceImpl();
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
		meta_tx.commit();
		data_tx.success();
	}

	@Test
	public void testSetA0_Integration_Not_Found() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		
		printIntegrationState(integrationSet, "A0 Not Found");
		assertEquals(null, integrationSet);
	}
	
	@Test
	public void testSetA1_Create_Integration() {
		IntegrationSet integrationSet = 
				integrationService.createIntegrationSet("TEST_RUN", "TEST");
		
		printIntegrationState(integrationSet, "A1 Created");
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testSetA2_Add_Clusters() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		assertNotEquals(null, integrationSet);
		
		Set<Long> allNodes = new HashSet<> (data.getAllMetaboliteIds());
		Iterator<Long> iterator = allNodes.iterator();
		for (int i = 0; i < 10; i++) {
			Set<Long> members = new HashSet<> ();
			for (int j = 0; j < 10; j++) {
				members.add(iterator.next());
			}
			integrationService.createCluster(integrationSet, String.format("C%d", i), members, String.format("cluster [%d]", i), ConflictDecision.ABORT);
		}
		
		printIntegrationState(integrationSet, "A2 Ten Clusters with 10 elements");
		assertEquals(10, integrationSet.getIntegratedClustersMap().size());
	}
	
	@Test
	public void testSetA3_Split_Clusters() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		
		printIntegrationState(integrationSet, "A3 1 even split cluster - 11 Clusters");
		assertNotEquals(null, integrationSet);
	}
	
	@Test
	public void testSetA4_Merge_Clusters() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		assertNotEquals(null, integrationSet);
		
		int before = integrationSet.getIntegratedClustersMap().size();
		
		Iterator<Long> iterator = integrationSet.getIntegratedClustersMap().keySet().iterator();
		Long cid1 = iterator.next();
		Long cid2 = iterator.next();
		String name = integrationSet.getIntegratedClustersMap().get(cid1).getName();
		String description = integrationSet.getIntegratedClustersMap().get(cid1).getDescription();
		Set<Long> cidList = new HashSet<> ();
		cidList.add(cid1); cidList.add(cid2);
		Set<Long> elementList = new HashSet<> ();
		
		elementList.addAll(integrationSet.getIntegratedClustersMap().get(cid1).listAllIntegratedMemberIds());
		elementList.addAll(integrationSet.getIntegratedClustersMap().get(cid2).listAllIntegratedMemberIds());
		IntegratedCluster integratedCluster = integrationService.mergeCluster(integrationSet, cidList, name, elementList, description);
		
		printIntegrationState(integrationSet, "A3 2 merged clusters 10 clusters");
		assertEquals(before - 1, integrationSet.size());
		assertEquals(name, integratedCluster.getName());
		assertEquals(description, integratedCluster.getDescription());
		assertEquals(elementList, new HashSet<> (integratedCluster.listAllIntegratedMemberIds()));
	}
	
	@Test
	public void testSetA5_Delete_Clusters() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		assertNotEquals(null, integrationSet);
		
		int before = integrationSet.getIntegratedClustersMap().size();
		
		Long cid = integrationSet.getIntegratedClustersMap().keySet().iterator().next();
		
		integrationService.deleteCluster(integrationSet, cid);
		
		printIntegrationState(integrationSet, "A5 1 deleted cluster 9 clusters");
		assertEquals(before - 1, integrationSet.size());
	}
	
	@Test
	public void testSetA6_Update_Clusters() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		
		printIntegrationState(integrationSet, "A6 1 updated cluster 9 clusters");
		assertNotEquals(null, integrationSet);
	}
	
	@Test
	public void testSetA7_Reset_Clusters() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		assertNotEquals(null, integrationSet);
		integrationService.resetIntegrationSet(integrationSet);
		
		printIntegrationState(integrationSet, "A7 integration reset 0 clusters");
		assertEquals(0, integrationSet.getIntegratedClustersMap().size());
	}
	
	@Test
	public void testSetA8_Strategy_Clusters() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		
		printIntegrationState(integrationSet, "A7 integration strategy 0 clusters");
		assertNotEquals(null, integrationSet);
	}
	
	@Test
	public void testSetA91_Delete_Integration() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		
		assertNotEquals(null, integrationSet);
		
		integrationService.deleteIntegrationSet(integrationSet);
		integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		
		printIntegrationState(integrationSet, "A91 deleted integration not found");
		assertEquals(null, integrationSet);
	}
	
	@Test
	public void testSetA92_Integration_Not_Found() {
		IntegrationSet integrationSet = integrationService.getIntegrationSetByEntry("TEST_RUN");
		
		printIntegrationState(integrationSet, "A92 integration not found");
		assertEquals(null, integrationSet);
	}
}
