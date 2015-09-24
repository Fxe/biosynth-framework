package edu.uminho.biosynth.core.data.integration.staging;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;

public class TestStagingSelection {

//	public static SessionFactory sessionFactory;
//	private static IGenericDao dao;
//	private static Transaction tx;
//
////	@BeforeClass
////	public static void setUpBeforeClass() throws Exception {
////		sessionFactory = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
////		dao = new GenericEntityDaoImpl(sessionFactory);
////	}
////
////	@AfterClass
////	public static void tearDownAfterClass() throws Exception {
////		
////		
////	}
////
////	@Before
////	public void setUp() throws Exception {
////		tx = sessionFactory.getCurrentSession().beginTransaction();
////	}
////
////	@After
////	public void tearDown() throws Exception {
////		tx.commit();
////	}
//
//	@Test
//	public void test() {
////		JniInchiOutputKey out;
////		try {
////			String inchi = "InChI=1S/5C7H12N2O4.3Al.4H2O/c5*1-4(10)9-5(7(12)13)2-3-6(8)11;;;;;;;/h5*5H,2-3H2,1H3,(H2,8,11)(H,9,10)(H,12,13);;;;4*1H2/q;;;;;3*+3;;;;/p-9/t5*5-;;;;;;;/m00000......./s1";
////			out = JniInchiWrapper.getInchiKey(inchi);
////			System.out.println(out.getReturnStatus().toString());
////			String key = out.getKey();
////			System.out.println(inchi);
////			System.out.println(key);
////		} catch (JniInchiException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		
////		for (MetaboliteFormulaDim formula_dim : dao.criteria(MetaboliteFormulaDim.class, Restrictions.eq("formula", "H2O"))) {
////			System.out.println(formula_dim.getId());
////			System.out.println(formula_dim.getFormula());
////		}
//	}
}
