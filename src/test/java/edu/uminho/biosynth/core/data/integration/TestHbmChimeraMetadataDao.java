package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.chimera.dao.HbmChimeraMetadataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;

public class TestHbmChimeraMetadataDao {

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
	public void test1() {
		HbmChimeraMetadataDaoImpl dao = new HbmChimeraMetadataDaoImpl();
		dao.setSessionFactory(sessionFactory);
		
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName("TestSet_" + System.currentTimeMillis());
		integrationSet.setDescription("Test Set");
		
		dao.saveIntegrationSet(integrationSet);
		System.out.println(dao.getAllIntegrationSetsId());
		assertEquals(true, !dao.getAllIntegrationSetsId().isEmpty());
	}
	
	@Test
	public void test2() {
		HbmChimeraMetadataDaoImpl dao = new HbmChimeraMetadataDaoImpl();
		dao.setSessionFactory(sessionFactory);
		
		IntegrationSet integrationSet = dao.getIntegrationSet(1L);
		
		List<Long> list = new ArrayList<> ();
		list.add(1L);
		list.add(13L);
		list.add(11L);
		IntegratedCluster cluster = dao.createCluster(list, "member", integrationSet);

		assertEquals(true, !cluster.getMemberMap().isEmpty());
	}

}
