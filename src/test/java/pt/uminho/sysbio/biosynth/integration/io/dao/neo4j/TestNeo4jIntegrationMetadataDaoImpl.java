package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestNeo4jIntegrationMetadataDaoImpl {

	private static GraphDatabaseService graphDatabaseService;
	private static final String NEO_META_DB = "D:/tmp/neo_meta";
	private static org.neo4j.graphdb.Transaction neoTx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDatabaseConstraints(NEO_META_DB);
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
		neoTx.failure();
		neoTx.close();
	}

	@Test
	public void testCreateIntegrationSet() {
		Neo4jIntegrationMetadataDaoImpl daoImpl = new Neo4jIntegrationMetadataDaoImpl(graphDatabaseService);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("IID_BIGG");
		integrationSet.setDescription("BiGG <=> KEGG Compound Integration");
		
		daoImpl.saveIntegrationSet(integrationSet);
		
		List<Long> ids = daoImpl.getAllIntegrationSetsId();
		
		System.out.println(ids);
		
		assertNotEquals(0, ids.size());
	}

	@Test
	public void test_create_IntegratedCluster_Without_Presaved_IntegrationSet() {
		Neo4jIntegrationMetadataDaoImpl daoImpl = new Neo4jIntegrationMetadataDaoImpl(graphDatabaseService);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("IID_BIGG");
		integrationSet.setDescription("BiGG <=> KEGG Compound Integration");
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setEntry("X00001");
		integratedCluster.setDescription("some cluster");
		
		IntegratedMember integratedMember = new IntegratedMember();
		integratedMember.setId(12345L);
		integratedMember.setDescription("cpd-0000A");
		
		IntegratedClusterMember integratedClusterMember = new IntegratedClusterMember();
		integratedClusterMember.setCluster(integratedCluster);
		integratedClusterMember.setMember(integratedMember);
		
		List<IntegratedClusterMember> integratedClusterMembers = new ArrayList<> ();
		integratedCluster.setMembers(integratedClusterMembers);
		
		daoImpl.saveIntegratedCluster(integratedCluster);
		
		List<Long> ids = daoImpl.getAllIntegrationSetsId();
		
		System.out.println(ids);
		
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
		integratedCluster.setDescription("some cluster");
		
		IntegratedMember integratedMember = new IntegratedMember();
		integratedMember.setId(12345L);
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
}
