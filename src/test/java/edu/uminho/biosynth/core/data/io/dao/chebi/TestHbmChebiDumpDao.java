package edu.uminho.biosynth.core.data.io.dao.chebi;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.chebi.ChebiDumpMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestHbmChebiDumpDao {
	
	private static SessionFactory sessionFactory;
	private static Transaction tx;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_mysql_chebi_dump.cfg.xml");
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
	public void test() {
		//15377 Water
		//65732
		//10440
		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
		
		ChebiDumpMetaboliteEntity cpd = dao.find(ChebiDumpMetaboliteEntity.class, 10440);
		System.out.println(cpd);
		
		assertEquals(true, cpd != null);
		
		HbmChebiDumpDaoImpl chebiDumpDao = new HbmChebiDumpDaoImpl();
		chebiDumpDao.setSessionFactory(sessionFactory);
		
		System.out.println(chebiDumpDao.find(10440));
	}

}
