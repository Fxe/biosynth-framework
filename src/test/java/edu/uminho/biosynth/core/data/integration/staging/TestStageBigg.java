package edu.uminho.biosynth.core.data.integration.staging;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.references.TransformBiggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestStageBigg {

	public static SessionFactory sessionFactory_stga;
	public static SessionFactory sessionFactory_bigg;
	private static IGenericDao dao_stga;
	private static IGenericDao dao_bigg;
	private static Transaction tx_stga;
	private static Transaction tx_bigg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		sessionFactory_bigg = TestConfig.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
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
		TransformBiggMetaboliteCrossReference biggXrefTrans = new TransformBiggMetaboliteCrossReference();
		biggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		
		BiggMetaboliteStageLoader loader = new BiggMetaboliteStageLoader();
		loader.setDao(dao_stga);
		loader.setTransformer(biggXrefTrans);
		
		int counter = 0;
		for (BiggMetaboliteEntity cpdBigg : dao_bigg.findAll(BiggMetaboliteEntity.class)) {
			System.out.println(cpdBigg.getEntry());
			System.out.println(cpdBigg.getFormula());
			MetaboliteStga cpd_stga = loader.stageMetabolite(cpdBigg);
			dao_stga.save(cpd_stga);			
			counter++;
			if (counter % 50 == 0) {
				tx_stga.commit();
				tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
			}
		}
		
		fail("Not yet implemented");
	}

}
