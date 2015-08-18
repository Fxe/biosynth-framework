package edu.uminho.biosynth.core.data.io.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.remote.BioCycRemoteSource;

@SuppressWarnings("deprecation")
public class TestBiocycDao {
	
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
	}

	@Before
	public void setUp() throws Exception {
		sessionFactory.openSession();
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.getCurrentSession().close();
	}

	@Test
	public void testInsertDeleteBiocycMetaboliteDao() {
		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx;
		BioCycMetaboliteEntity cpd = null;
		List<BioCycMetaboliteEntity> res = null;
		tx = sessionFactory.getCurrentSession().beginTransaction();
		
		BioCycRemoteSource remote = new BioCycRemoteSource("META");
		res = dao.criteria(BioCycMetaboliteEntity.class, Restrictions.eq("entry", "CPD-14641"));
		if (res.size() < 1) {
			cpd = remote.getMetaboliteInformation("CPD-14641");
			dao.save(cpd);
			System.out.println(cpd);
		}
		tx.commit();
		
		tx = sessionFactory.getCurrentSession().beginTransaction();
		
		res = dao.criteria(BioCycMetaboliteEntity.class, Restrictions.eq("entry", "CPD-14641"));
		assertEquals(true, res.size() > 0);
		
		dao.remove(res.iterator().next());
		
		tx.commit();
	}
}
