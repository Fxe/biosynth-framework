package edu.uminho.biosynth.example;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxReactionCrossReferenceEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.bigg.CsvBiggMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.HbmKeggCompoundMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.service.MnxService;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@SuppressWarnings({ "unused", "deprecation", "null" })
public class TestMxnService {
	
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
//	public void test() {
//		final String[] keggId = {"R08101", "R08105", "R08108", "R08109", "R08110"};
//		
//		IGenericDao dao = new GenericEntityDaoImpl(sessionFactory);
//		MnxService service = new MnxService(dao);
//		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
//		List<MnxReactionCrossReferenceEntity> res = service.getReactionCrossreferences("kegg", "R00066");
//		for (MnxReactionCrossReferenceEntity xrefKegg : res) {
//			for (MnxReactionCrossReferenceEntity rxnXref : xrefKegg.getMnxReactionEntity().getCrossreferences()) {
//				if (rxnXref.getRef().equals("metacyc")) {
//					System.out.println(rxnXref);
//				}
//			}
//		}
//		tx.commit();
//		
//	}
//	
//	@Test
//	public void tmp() {
//		
//		
//		List<Metabolite> list = new ArrayList<> ();
//		
////		MetaboliteDao<?> dao = ;
//		MetaboliteDao<?> metaboliteDao = null;
//		
//		for (String entry : metaboliteDao.getAllMetaboliteEntries()) {
//			list.add(metaboliteDao.getMetaboliteByEntry(entry));
//		}
//		
//		System.out.println(list.size());
//	}

}
