package edu.uminho.biosynth.core.data.integration.staging;

import static org.junit.Assert.*;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiOutput;
import net.sf.jniinchi.JniInchiOutputKey;
import net.sf.jniinchi.JniInchiWrapper;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestStagingSelection {

	public static SessionFactory sessionFactory;
	private static IGenericDao dao;
	private static Transaction tx;

//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		sessionFactory = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
//		dao = new GenericEntityDaoImpl(sessionFactory);
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		
//		
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

	@Test
	public void test() {
		JniInchiOutputKey out;
		try {
			out = JniInchiWrapper.getInchiKey("InChI=1S/H3O/h1H3");
			System.out.println(out.getReturnStatus().toString());
			String key = out.getKey();
			System.out.println(key);
		} catch (JniInchiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		for (MetaboliteFormulaDim formula_dim : dao.criteria(MetaboliteFormulaDim.class, Restrictions.eq("formula", "H2O"))) {
//			System.out.println(formula_dim.getId());
//			System.out.println(formula_dim.getFormula());
//		}
	}
}
