package edu.uminho.biosynth.core.data.io.dao.chebi;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiDumpMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestHbmChebiDumpDao {
	
	private static SessionFactory sessionFactory;
	private static SessionFactory sessionFactory_chebi;
	private static Transaction tx;
	private static Transaction tx_chebi;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_mysql_chebi_dump.cfg.xml");
		sessionFactory_chebi = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_chebi_pgsql.cfg.xml");
		sessionFactory.openSession();
		sessionFactory_chebi.openSession();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.getCurrentSession().close();
		sessionFactory_chebi.getCurrentSession().close();
		sessionFactory.close();
		sessionFactory_chebi.close();
	}

	@Before
	public void setUp() throws Exception {
		tx = sessionFactory.getCurrentSession().beginTransaction();
		tx_chebi = sessionFactory_chebi.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx.commit();
		tx_chebi.commit();
	}

	@Test
	public void getChebi15377() {
		HbmChebiDumpDaoImpl dao = new HbmChebiDumpDaoImpl();
		dao.setSessionFactory(sessionFactory);
		ChebiMetaboliteEntity cpd = dao.getMetaboliteById(15377);
		
		assertEquals("15377", cpd.getEntry());
	}
	
	@Test
	public void getChebi5585() {
		HbmChebiDumpDaoImpl dao = new HbmChebiDumpDaoImpl();
		dao.setSessionFactory(sessionFactory);
		ChebiMetaboliteEntity cpd = dao.getMetaboliteById(5585);
		
		assertEquals("5585", cpd.getEntry());
		assertEquals(3, cpd.getCrossreferences().size());
	}
	
//	@Test
	public void test() {
		//15377 Water
		//65732
		//10440
		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
		IGenericDao genericDao = new GenericEntityDaoImpl(sessionFactory_chebi);
		
		ChebiDumpMetaboliteEntity cpd = dao.find(ChebiDumpMetaboliteEntity.class, 10440);
		System.out.println(cpd);
		
		assertEquals(true, cpd != null);
		
		HbmChebiDumpDaoImpl chebiDumpDao = new HbmChebiDumpDaoImpl();
		chebiDumpDao.setSessionFactory(sessionFactory);
		
//		System.out.println(chebiDumpDao.find(10440));
		@SuppressWarnings("unchecked")
		List<Serializable> cpdIds = genericDao.createQuery("SELECT cpd.id FROM ChebiMetaboliteEntity cpd").list();
		int i = 0;
		for (Serializable id : chebiDumpDao.getAllMetaboliteIds()) {
			System.out.println(id);
			if (!cpdIds.contains(id)) {
				
				ChebiMetaboliteEntity chebi = chebiDumpDao.find(id);
				System.out.println(chebi.getName());
				genericDao.save(chebi);
				if (i % 2 == 0) {
					tx_chebi.commit();
					tx_chebi = sessionFactory_chebi.getCurrentSession().beginTransaction();
				}
				i++;
			}
		}
	}

}
