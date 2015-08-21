package edu.uminho.biosynth.core.data.io.dao;

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

import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.service.MnxService;

@SuppressWarnings("deprecation")
public class TestMnxDao {
//
//	private static SessionFactory sessionFactory;
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("");
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		sessionFactory.close();
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		sessionFactory.openSession();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		sessionFactory.getCurrentSession().close();
//	}
//
//	@Test
//	public void testLoadMetabolite() {
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		MnxMetaboliteEntity cpd = dao.find(MnxMetaboliteEntity.class, 58);
//		System.out.println(cpd);
//		tx.commit();
//	}
//	
//	@Test
//	public void testLoadReaction() {
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		MnxReactionEntity rxn = dao.find(MnxReactionEntity.class, 585);
//		System.out.println(rxn);
//		tx.commit();
//	}
//
//	@Test
//	public void testFindProducer() {
//		
//		
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		MnxService service = new MnxService(dao);
//		
//		List<String> set1 = new ArrayList<> ();
//		set1.add("MNXM20"); set1.add("MNXM268");
//		List<String> set2 = new ArrayList<> ();
//		set2.add("MNXM263"); set2.add("MNXM89557");
//
//		
//		List<String> finalAnswer = service.findReactionByReactantsAndProducts(set1, set2);
//		
////		MnxReactionEntity rxn = dao.find(MnxReactionEntity.class, 58);
////		System.out.println(rxn);
//		tx.commit();
//		assertEquals(true, finalAnswer.contains("MNXR30562"));
//	}
}
