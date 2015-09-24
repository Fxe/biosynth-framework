package edu.uminho.biosynth.core.data.integration.staging;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.etl.dictionary.BioDbDictionary;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.integration.etl.staging.HbmMetaboliteStagingManagerImpl;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteServiceDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.etl.staging.transform.MnxMetaboliteStagingTransform;
import edu.uminho.biosynth.core.data.integration.references.TransformMnxMetaboliteCrossReference;

public class TestStageMnx {

//	public static SessionFactory sessionFactory_stga;
//	public static SessionFactory sessionFactory_mnx;
//	private static IGenericDao dao_stga;
//	private static IGenericDao dao_mnx;
//	private static Transaction tx_stga;
//	private static Transaction tx_mnx;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		sessionFactory_stga = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
//		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
//		sessionFactory_mnx = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
//		dao_mnx = new GenericEntityDaoImpl(sessionFactory_mnx);
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
//		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
//		tx_mnx = sessionFactory_mnx.getCurrentSession().beginTransaction();
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		tx_stga.commit();
//		tx_mnx.commit();
//	}
//
//	@Test
//	public void testStageMnx() {
//		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
//		manager.setDao(dao_stga);
//		MetaboliteServiceDim service = new MetaboliteServiceDim("MNX_FILE", "0.0.1-SNAPSHOT", null);
//		service = manager.createOrGetService(service);
//		System.out.println(service.getServiceName() + " " + service.getServiceVersion());
//		
//		Set<String> skipEntries = new HashSet<> ();
//		for (MetaboliteStga cpd : service.getMetaboliteStgas()) {
//			skipEntries.add(cpd.getTextKey());
//		}
//		
//		TransformMnxMetaboliteCrossReference mnxXrefTrans =  new TransformMnxMetaboliteCrossReference();
//		mnxXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
//		
//		MnxMetaboliteStagingTransform loader = new MnxMetaboliteStagingTransform();
//		loader.setDao(dao_stga);
//		loader.setTransformer(mnxXrefTrans);
//		loader.setManager(manager);
//		
//		int counter = 0;
//		int total = 0;
//		for (MnxMetaboliteEntity cpdMnx : dao_mnx.findAll(MnxMetaboliteEntity.class)) {
//			if ( !skipEntries.contains(cpdMnx.getEntry())) {
//				System.out.println(cpdMnx.getEntry());
//				System.out.println(cpdMnx.getFormula());
//				MetaboliteStga cpd_stga = loader.etlTransform(cpdMnx);
//				cpd_stga.setMetaboliteServiceDim(service);
//				
//				dao_stga.save(cpd_stga);
//				counter++;
//				if (counter % 50 == 0) {
//					tx_stga.commit();
//					tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
//				}
//			}
//			total++;
//		}
//		
//		assertEquals(124834, total);
//	}

}
