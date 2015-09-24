package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestNeo4jIntegrationMetadataDaoImpl extends TestNeo4jConfiguration {

	private static Logger LOGGER = LoggerFactory.getLogger(TestNeo4jIntegrationMetadataDaoImpl.class);
	
	private static GraphDatabaseService graphDatabaseService;
	private static org.neo4j.graphdb.Transaction neoTx;
	private static Neo4jIntegrationMetadataDaoImpl daoImpl;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	  graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
//		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jMetaDatabaseConstraints(NEO_META_DB);
		daoImpl = new Neo4jIntegrationMetadataDaoImpl(graphDatabaseService);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graphDatabaseService.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		
		neoTx = graphDatabaseService.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		neoTx.success();
		neoTx.close();
	}

	@Test
	public void test_create_integration_set_success() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("IID_BIGG");
		integrationSet.setDescription("BiGG <=> KEGG Compound Integration");
		daoImpl.saveIntegrationSet(integrationSet);
		List<Long> ids = daoImpl.getAllIntegrationSetsId();
		LOGGER.debug(ids.toString());
		assertNotEquals(0, ids.size());
	}
	
	@Test
	public void test_get_integration_set_by_entry_success() {
		String entry = "TEST_SET_2";
		String description = "test_get_integration_set_success";
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName(entry);
		integrationSet.setDescription(description);
		daoImpl.saveIntegrationSet(integrationSet);
		List<Long> ids = daoImpl.getAllIntegrationSetsId();
		LOGGER.debug(ids.toString());
		IntegrationSet integrationSet_ = daoImpl.getIntegrationSet(entry);
		assertEquals(entry, integrationSet_.getEntry());
		assertEquals(description, integrationSet_.getDescription());
	}
	
	@Test
	public void test_get_integration_set_by_id_success() {
		String entry = "TEST_SET_3";
		String description = "test_get_integration_set_by_id_success";
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName(entry);
		integrationSet.setDescription(description);
		daoImpl.saveIntegrationSet(integrationSet);
		List<Long> ids = daoImpl.getAllIntegrationSetsId();
		LOGGER.debug(ids.toString());
		IntegrationSet integrationSet_ = daoImpl.getIntegrationSet(integrationSet.getId());
		assertEquals(entry, integrationSet_.getEntry());
		assertEquals(description, integrationSet_.getDescription());
	}
	
	@Test
	public void test_verify_neo4j_integration_set_data_success() {
		String entry = "TEST_SET_4";
		String description = "test_verify_neo4j_integration_set_data_success";
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName(entry);
		integrationSet.setDescription(description);
		daoImpl.saveIntegrationSet(integrationSet);
		Node node = graphDatabaseService.getNodeById(integrationSet.getId());
		assertEquals(entry, node.getProperty("entry"));
		assertEquals(description, node.getProperty("description"));
		assertEquals(true, node.hasLabel(IntegrationNodeLabel.IntegrationSet));
	}

	@Test
	public void test_create_integrated_cluster_without_presaved_integration_set() {
		Neo4jIntegrationMetadataDaoImpl daoImpl = new Neo4jIntegrationMetadataDaoImpl(graphDatabaseService);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("IID_BIGG");
		integrationSet.setDescription("BiGG <=> KEGG Compound Integration");
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setClusterType(IntegrationNodeLabel.MetaboliteCluster.toString());
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setEntry("X00001");
		integratedCluster.setDescription("some cluster");
		
		IntegratedMember integratedMember = new IntegratedMember();
		integratedMember.setReferenceId(12345L);
		integratedMember.setDescription("cpd-0000A");
		integratedMember.setMemberType(IntegrationNodeLabel.MetaboliteMember.toString());
		
		IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
		integratedClusterMember.setCluster(integratedCluster);
		integratedClusterMember.setMember(integratedMember);
		
		List<IntegratedClusterMember> integratedClusterMembers = new ArrayList<> ();
		integratedClusterMembers.add(integratedClusterMember);
		
		integratedCluster.setMembers(integratedClusterMembers);
		
		daoImpl.saveIntegratedCluster(integratedCluster);
		
		
		assertNotNull(integrationSet.getId());
		
		List<Long> ids = daoImpl.getAllIntegratedClusterIds(integrationSet.getId());
		
		LOGGER.debug("Clusters: " + ids);
		
		assertNotEquals(0, ids.size());
	}
	
	@Test
	public void test_marshall_and_unmarshall_IntegratedCluster() {
		Neo4jIntegrationMetadataDaoImpl daoImpl = new Neo4jIntegrationMetadataDaoImpl(graphDatabaseService);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("IID_BIGG");
		integrationSet.setDescription("BiGG <=> KEGG Compound Integration");
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setEntry("X00001");
		integratedCluster.setClusterType(IntegrationNodeLabel.MetaboliteCluster);
		integratedCluster.setDescription("some cluster");
		
		IntegratedMember integratedMember = new IntegratedMember();
		integratedMember.setReferenceId(12345L);
		integratedMember.setDescription("cpd-0000A");
		
		List<IntegratedClusterMember> integratedClusterMembers = new ArrayList<> ();
		IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
		integratedClusterMember.setCluster(integratedCluster);
		integratedClusterMember.setMember(integratedMember);
		integratedClusterMembers.add(integratedClusterMember);
		
		integratedCluster.setMembers(integratedClusterMembers);
		
		Long savedId = daoImpl.saveIntegratedCluster(integratedCluster).getId();
		
		assertNotNull(savedId);
		
		IntegratedCluster savedIntegratedCluster = daoImpl.getIntegratedClusterById(savedId);
		
		System.out.println(savedIntegratedCluster.getIntegrationSet());
		System.out.println(savedIntegratedCluster);
		System.out.println(savedIntegratedCluster.getMembers());
	}
	
	@Test
	public void test_something() {
		
		
		
//		daoImpl.saveIntegratedMember(member);
	}
}
