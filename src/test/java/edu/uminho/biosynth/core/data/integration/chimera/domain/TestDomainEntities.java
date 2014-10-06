package edu.uminho.biosynth.core.data.integration.chimera.domain;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class TestDomainEntities {

	private static SessionFactory sessionFactory;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hbm_mysql_chimera_meta.cfg.xml");
		sessionFactory.openSession();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.getCurrentSession().close();
		sessionFactory.close();
	}

	@Before
	public void setUp() throws Exception {
		tx = sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx.commit();
	}
	
	@Test
	public void testCreateNodes() {
		HbmIntegrationMetadataDaoImpl dao = new HbmIntegrationMetadataDaoImpl();
		dao.setSessionFactory(sessionFactory);
		
		IntegratedMember node1 = new IntegratedMember();
		node1.setId(34L); node1.setDescription("Entry A'");
		IntegratedMember node2 = new IntegratedMember();
		node2.setId(344L); node2.setDescription("Entry B");
		IntegratedMember node3 = new IntegratedMember();
		node3.setId(323L); node3.setDescription("Entry C");
		
		dao.saveIntegratedMember(node1);
		dao.saveIntegratedMember(node2);
		dao.saveIntegratedMember(node3);
	}

	@Test
	public void test() {
		HbmIntegrationMetadataDaoImpl dao = new HbmIntegrationMetadataDaoImpl();
		dao.setSessionFactory(sessionFactory);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("Test Integration");
		integrationSet.setDescription("Dummy Integration with fake elements");
		
		
		
		IntegratedCluster cluster1 = new IntegratedCluster();
		cluster1.setName("C_A");
		cluster1.setDescription("Should Contain A");
		cluster1.setIntegrationSet(integrationSet);
		IntegratedCluster cluster2 = new IntegratedCluster();
		cluster2.setName("C_B");
		cluster2.setDescription("Should Contain B, C");
		cluster2.setIntegrationSet(integrationSet);
		
		IntegratedMember node1 = new IntegratedMember();
		node1.setId(34L); node1.setDescription("Entry A");
		IntegratedMember node2 = new IntegratedMember();
		node2.setId(344L); node2.setDescription("Entry B");
		IntegratedMember node3 = new IntegratedMember();
		node3.setId(323L); node3.setDescription("Entry C");
		
		IntegratedClusterMember m1 = new IntegratedClusterMember();
		m1.setCluster(cluster1);m1.setMember(node1);
		IntegratedClusterMember m2 = new IntegratedClusterMember();
		m2.setCluster(cluster2);m2.setMember(node2);
		IntegratedClusterMember m3 = new IntegratedClusterMember();
		m3.setCluster(cluster2);m3.setMember(node3);
		
		node1.getClusters().add(m1);
		node2.getClusters().add(m2);
		node3.getClusters().add(m3);
		
		cluster1.getMembers().add(m1);
		cluster2.getMembers().add(m2);
		cluster2.getMembers().add(m3);
		
		dao.saveIntegrationSet(integrationSet);
		dao.saveIntegratedCluster(cluster1);
		dao.saveIntegratedCluster(cluster2);
		
		integrationSet.getIntegratedClustersMap().put(cluster1.getId(),	cluster1);
		integrationSet.getIntegratedClustersMap().put(cluster2.getId(),	cluster2);
		
		
//		cluster1.
		
		fail("Not yet implemented");
	}

}

