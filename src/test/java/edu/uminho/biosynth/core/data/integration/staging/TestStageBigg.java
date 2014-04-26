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

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.etl.staging.HbmMetaboliteStagingManagerImpl;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteServiceDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.etl.staging.transform.BiggMetaboliteStagingTransform;
import edu.uminho.biosynth.core.data.integration.references.TransformBiggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.io.dao.HelperHbmConfigInitializer;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestStageBigg {

	public static SessionFactory sessionFactory_stga;
	public static SessionFactory sessionFactory_bigg;
	private static IGenericDao dao_stga;
	private static IGenericDao dao_bigg;
	private static Transaction tx_stga;
	private static Transaction tx_bigg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		sessionFactory_bigg = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
		dao_bigg = new GenericEntityDaoImpl(sessionFactory_bigg);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		
	}

	@Before
	public void setUp() throws Exception {
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
		tx_bigg = sessionFactory_bigg.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_stga.commit();
		tx_bigg.commit();
	}

	@Test
	public void testStageBigg() {
		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
		manager.setDao(dao_stga);
		MetaboliteServiceDim service = new MetaboliteServiceDim("BiGG_FILE", "0.0.1-SNAPSHOT", null);
		service = manager.createOrGetService(service);
		System.out.println(service.getServiceName() + " " + service.getServiceVersion());
		
		Set<String> skipEntries = new HashSet<> ();
		for (MetaboliteStga cpd : service.getMetaboliteStgas()) {
			skipEntries.add(cpd.getTextKey());
		}
		
		TransformBiggMetaboliteCrossReference biggXrefTrans = new TransformBiggMetaboliteCrossReference();
		biggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		
		BiggMetaboliteStagingTransform loader = new BiggMetaboliteStagingTransform();
		loader.setDao(dao_stga);
		loader.setTransformer(biggXrefTrans);
		loader.setManager(manager);
		
		int counter = 0;
		int total = 0;
		for (BiggMetaboliteEntity cpdBigg : dao_bigg.findAll(BiggMetaboliteEntity.class)) {
			if ( !skipEntries.contains(cpdBigg.getEntry())) {
				System.out.println(cpdBigg.getEntry());
				System.out.println(cpdBigg.getFormula());
				MetaboliteStga cpd_stga = loader.etlTransform(cpdBigg);
				cpd_stga.setMetaboliteServiceDim(service);
				dao_stga.save(cpd_stga);
			
				counter++;
				if (counter % 50 == 0) {
					tx_stga.commit();
					tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
				}
			}
			total++;
		}
		
		assertEquals(2835, total);
	}

}
