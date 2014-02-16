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

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.etl.staging.HbmMetaboliteStagingManagerImpl;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteServiceDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.integration.etl.staging.transform.KeggMetaboliteStageLoader;
import edu.uminho.biosynth.core.data.integration.references.TransformKeggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestStageKegg {
	
	public static SessionFactory sessionFactory_stga;
	public static SessionFactory sessionFactory_kegg;
	private static IGenericDao dao_stga;
	private static IGenericDao dao_kegg;
	private static Transaction tx_stga;
	private static Transaction tx_kegg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		sessionFactory_kegg = TestConfig.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
		dao_kegg = new GenericEntityDaoImpl(sessionFactory_kegg);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		
	}

	@Before
	public void setUp() throws Exception {
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
		tx_kegg = sessionFactory_kegg.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_stga.commit();
		tx_kegg.commit();
	}

	@Test
	public void testStageKegg() {
		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
		manager.setDao(dao_stga);
		MetaboliteServiceDim service = new MetaboliteServiceDim("KEGG_LIVE", "0.0.1-SNAPSHOT", null);
		service = manager.createOrGetService(service);
		System.out.println(service.getServiceName() + " " + service.getServiceVersion());
		
		Set<String> skipEntries = new HashSet<> ();
		for (MetaboliteStga cpd : service.getMetaboliteStgas()) {
			skipEntries.add(cpd.getTextKey());
		}
		
		TransformKeggMetaboliteCrossReference keggXrefTrans = new TransformKeggMetaboliteCrossReference();
		keggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		
		KeggMetaboliteStageLoader loader = new KeggMetaboliteStageLoader();
		loader.setManager(manager);
		loader.setDao(dao_stga);
		loader.setTransformer(keggXrefTrans);
		
		int counter = 0;
		int total = 0;
		for (KeggMetaboliteEntity cpdKegg : dao_kegg.findAll(KeggMetaboliteEntity.class)) {
//			if ( cpdKegg.getEntry().equals("C00001"))
			if ( !skipEntries.contains(cpdKegg.getEntry())) {
				System.out.println(cpdKegg.getEntry());
				System.out.println(cpdKegg.getFormula());
				MetaboliteStga cpd_stga = loader.etlTransform(cpdKegg);
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
		
		System.out.println(total);
		
		assertEquals(28162, total);
	}

}
