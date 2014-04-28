package edu.uminho.biosynth.core.data.integration.chimera.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Set;

import org.hibernate.PropertyValueException;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class TestHbmChimeraDaoImpl {
	
	private static final String HBM_CFG = "D:/home/data/java_config/hbm_mysql_chimera_meta.cfg.xml";
	private static SessionFactory sessionFactory;
	private static HbmChimeraMetadataDaoImpl dao;
	private static org.hibernate.Transaction hbm_tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession(new File(HBM_CFG));
		dao = new HbmChimeraMetadataDaoImpl();
		dao.setSessionFactory(sessionFactory);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
	}

	@Before
	public void setUp() throws Exception {
		hbm_tx = sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		hbm_tx.rollback();
	}

	@Test
	public void testCreateIntegrationSetSuccess() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		integrationSet.setDescription("Test Set");
		
		dao.saveIntegrationSet(integrationSet);
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test(expected = PropertyValueException.class)
	public void testCreateIntegrationSetNullName() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setDescription("Test Set");
		
		dao.saveIntegrationSet(integrationSet);
		assertNotEquals(null, integrationSet.getId());
	}

	@Test
	public void testCreateIntegrationSetNullDescription() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		
		dao.saveIntegrationSet(integrationSet);
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test(expected = PropertyValueException.class)
	public void testCreateIntegrationSetNullAll() {
		IntegrationSet integrationSet = new IntegrationSet();
		
		dao.saveIntegrationSet(integrationSet);
		assertNotEquals(null, integrationSet.getId());
	}
	
	@Test
	public void testCreateIntegratedMember() {
		IntegratedMember integratedMember = new IntegratedMember();
		integratedMember.setId(1L);
		dao.saveIntegratedMember(integratedMember);
		
		assertNotEquals(null, integratedMember.getId());
	}
	
	@Test
	public void testCreateIntegratedClusterWithoutMembers() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		
		dao.saveIntegrationSet(integrationSet);
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setName("C1");
		
		dao.saveIntegratedCluster(integratedCluster);
		
		assertNotEquals(null, integratedCluster.getId());
	}
	
	@Test
	public void testCreateIntegratedClusterOneMember() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		
		dao.saveIntegrationSet(integrationSet);
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setName("C1");
		
		IntegratedMember integratedMember1 = new IntegratedMember();
		integratedMember1.setId(1L);
		dao.saveIntegratedMember(integratedMember1);
		
		integratedCluster.addMember(integratedMember1);
		
		dao.saveIntegratedCluster(integratedCluster);
		
		assertNotEquals(null, integratedCluster.getId());
		assertEquals(1, integratedCluster.getMembers().size());
	}
	
	@Test
	public void testCreateIntegratedClusterManyMember() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		
		dao.saveIntegrationSet(integrationSet);
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setName("C1");
		
		IntegratedMember integratedMember1 = new IntegratedMember();
		integratedMember1.setId(1L);
		IntegratedMember integratedMember2 = new IntegratedMember();
		integratedMember2.setId(2L);
		IntegratedMember integratedMember3 = new IntegratedMember();
		integratedMember3.setId(3L);
		dao.saveIntegratedMember(integratedMember1);
		dao.saveIntegratedMember(integratedMember2);
		dao.saveIntegratedMember(integratedMember3);
		
		integratedCluster.addMember(integratedMember1);
		integratedCluster.addMember(integratedMember2);
		integratedCluster.addMember(integratedMember3);
		
		dao.saveIntegratedCluster(integratedCluster);
		
		assertEquals(true, integratedCluster.getId() != null);
		assertEquals(3, integratedCluster.getMembers().size());
	}
	
	@Test
	public void testUpdateIntegratedClusterManyMember() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		
		dao.saveIntegrationSet(integrationSet);
		
		Long clusterId;
		
		{
			IntegratedCluster integratedCluster = new IntegratedCluster();
			integratedCluster.setIntegrationSet(integrationSet);
			integratedCluster.setName("C1");
			
			
			
			IntegratedMember integratedMember1 = new IntegratedMember();
			integratedMember1.setId(1L);
			IntegratedMember integratedMember2 = new IntegratedMember();
			integratedMember2.setId(2L);
			IntegratedMember integratedMember3 = new IntegratedMember();
			integratedMember3.setId(3L);
			dao.saveIntegratedMember(integratedMember1);
			dao.saveIntegratedMember(integratedMember2);
			dao.saveIntegratedMember(integratedMember3);
			
			integratedCluster.addMember(integratedMember1);
			integratedCluster.addMember(integratedMember2);
			integratedCluster.addMember(integratedMember3);
			
			dao.saveIntegratedCluster(integratedCluster);
			
			clusterId = integratedCluster.getId();
			
			assertEquals(true, clusterId != null);
			assertEquals(3, integratedCluster.getMembers().size());
			assertEquals(null, integratedCluster.getDescription());
		}
		
		{
			IntegratedCluster integratedCluster = dao.getIntegratedClusterById(clusterId);
			integratedCluster.setDescription("updated");
			integratedCluster.getMembers().clear();
			dao.updateCluster(integratedCluster);
		}

		IntegratedCluster integratedCluster = dao.getIntegratedClusterById(clusterId);
		
		assertEquals(true, integratedCluster.getId() != null);
		assertEquals(0, integratedCluster.getMembers().size());
		assertEquals("updated", integratedCluster.getDescription());
	}
	
	@Test
	public void testGetAllClusterIds() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		
		dao.saveIntegrationSet(integrationSet);
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setName("C1");
		
		dao.saveIntegratedCluster(integratedCluster);
		
		Set<Long> clusterIds = dao.getAllIntegratedClusterIds(integrationSet);
		
		assertEquals(true, integratedCluster.getId() != null);
		assertEquals(0, integratedCluster.getMembers().size());
		assertEquals(1, clusterIds.size());
	}
	
	@Test
	public void testDeleteIntegratedCluster() {
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		
		dao.saveIntegrationSet(integrationSet);
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(integrationSet);
		integratedCluster.setName("C1");
		
		IntegratedMember integratedMember1 = new IntegratedMember();
		integratedMember1.setId(1L);
		IntegratedMember integratedMember2 = new IntegratedMember();
		integratedMember2.setId(2L);
		IntegratedMember integratedMember3 = new IntegratedMember();
		integratedMember3.setId(3L);
		dao.saveIntegratedMember(integratedMember1);
		dao.saveIntegratedMember(integratedMember2);
		dao.saveIntegratedMember(integratedMember3);
		
		integratedCluster.addMember(integratedMember1);
		integratedCluster.addMember(integratedMember2);
		integratedCluster.addMember(integratedMember3);
		
		dao.saveIntegratedCluster(integratedCluster);
		
		assertEquals(true, integratedCluster.getId() != null);
		assertEquals(3, integratedCluster.getMembers().size());
		assertEquals(null, integratedCluster.getDescription());
		
		Set<Long> clusterIds;
		clusterIds = dao.getAllIntegratedClusterIds(integrationSet);
		assertEquals(1, clusterIds.size());
		dao.deleteCluster(integratedCluster);
		clusterIds = dao.getAllIntegratedClusterIds(integrationSet);
		assertEquals(0, clusterIds.size());

	}
}
