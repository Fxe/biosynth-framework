package edu.uminho.biosynth.core.data.integration.staging;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.references.TransformMnxMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestStageMnx {

	public static SessionFactory sessionFactory_stga;
	public static SessionFactory sessionFactory_mnx;
	private static IGenericDao dao_stga;
	private static IGenericDao dao_mnx;
	private static Transaction tx_stga;
	private static Transaction tx_mnx;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		sessionFactory_mnx = TestConfig.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
		dao_mnx = new GenericEntityDaoImpl(sessionFactory_mnx);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		
	}

	@Before
	public void setUp() throws Exception {
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
		tx_mnx = sessionFactory_mnx.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_stga.commit();
		tx_mnx.commit();
	}

	@Test
	public void testStageMnx() {
		TransformMnxMetaboliteCrossReference mnxXrefTrans =  new TransformMnxMetaboliteCrossReference();
		mnxXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		
		MnxMetaboliteStageLoader loader = new MnxMetaboliteStageLoader();
		loader.setDao(dao_stga);
		loader.setTransformer(mnxXrefTrans);
		
		int counter = 0;
		for (MnxMetaboliteEntity cpdMnx : dao_mnx.findAll(MnxMetaboliteEntity.class)) {
			System.out.println(cpdMnx.getEntry());
			System.out.println(cpdMnx.getFormula());
			MetaboliteStga cpd_stga = loader.stageMetabolite(cpdMnx);
			dao_stga.save(cpd_stga);
			counter++;
			if (counter % 50 == 0) {
				tx_stga.commit();
				tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
			}
		}
	}

}
