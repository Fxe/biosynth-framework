package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.hbm.HbmIntegrationMetadataDaoImpl;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestHbmChimeraMetadataDao {

//	private static SessionFactory sessionFactory;
//	private static Transaction tx;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hbm_mysql_chimera_meta.cfg.xml");
//		sessionFactory.openSession();
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		sessionFactory.getCurrentSession().close();
//		sessionFactory.close();
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		tx = sessionFactory.getCurrentSession().beginTransaction();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		tx.commit();
//	}
//
//	@Test
//	public void testA_CreateIntegrationSet() {
//		HbmIntegrationMetadataDaoImpl dao = new HbmIntegrationMetadataDaoImpl();
//		dao.setSessionFactory(sessionFactory);
//		
//		IntegrationSet integrationSet = new IntegrationSet();
//		integrationSet.setName("TestSet_" + System.currentTimeMillis());
//		integrationSet.setDescription("Test Set");
//		
//		dao.saveIntegrationSet(integrationSet);
//		System.out.println(dao.getAllIntegrationSetsId());
//		assertEquals(true, !dao.getAllIntegrationSetsId().isEmpty());
//	}
//	
//	@Test
//	public void testB_CreateIntegratedMembers() {
//		HbmIntegrationMetadataDaoImpl dao = new HbmIntegrationMetadataDaoImpl();
//		dao.setSessionFactory(sessionFactory);
//		
//		IntegratedMember m1 = new IntegratedMember(); m1.setId(1L);
//		IntegratedMember m2 = new IntegratedMember(); m2.setId(13L);
//		IntegratedMember m3 = new IntegratedMember(); m3.setId(11L);
//		dao.saveIntegratedMember(m1);
//		dao.saveIntegratedMember(m2);
//		dao.saveIntegratedMember(m3);
//		
//		assertEquals(true, !dao.getAllIntegrationSetsId().isEmpty());
//	}
//	
//	@Test
//	public void testC_CreateIntegratedCluster() {
//		HbmIntegrationMetadataDaoImpl dao = new HbmIntegrationMetadataDaoImpl();
//		dao.setSessionFactory(sessionFactory);
//		
//		List<Long> integrationSetIds = dao.getAllIntegrationSetsId();
//		if (integrationSetIds.isEmpty()) fail("No integration sets");
//		IntegrationSet integrationSet = dao.getIntegrationSet( integrationSetIds.iterator().next());
//		System.out.println(integrationSet);
//		
//		IntegratedMember m1 = new IntegratedMember(); m1.setId(1L);
//		IntegratedMember m2 = new IntegratedMember(); m2.setId(13L);
//		IntegratedMember m3 = new IntegratedMember(); m3.setId(11L);
//		
//		IntegratedCluster cluster = new IntegratedCluster();
//		cluster.setIntegrationSet(integrationSet);
//		cluster.setEntry("C_A");
//		
//		IntegratedClusterMember member1 = new IntegratedClusterMember();
//		member1.setCluster(cluster); member1.setMember(m1);
//		cluster.getMembers().add(member1);
//
//		IntegratedClusterMember member2 = new IntegratedClusterMember();
//		member2.setCluster(cluster); member2.setMember(m2);
//		cluster.getMembers().add(member2);
//		
//		IntegratedClusterMember member3 = new IntegratedClusterMember();
//		member3.setCluster(cluster); member3.setMember(m3);
//		cluster.getMembers().add(member3);
//		
//		dao.saveIntegratedCluster(cluster);
////		IntegratedCluster cluster = dao.createCluster("C_1", list, "member", integrationSet);
//
//		assertEquals(true, cluster.getMembers().size() == 3);
//	}

}

