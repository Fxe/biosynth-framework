package edu.uminho.biosynth.core.data.integration.staging;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.references.TransformKeggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;
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
		TransformKeggMetaboliteCrossReference keggXrefTrans = new TransformKeggMetaboliteCrossReference();
		keggXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());
		
		KeggMetaboliteStageLoader loader = new KeggMetaboliteStageLoader();
		loader.setDao(dao_stga);
		loader.setTransformer(keggXrefTrans);
		loader.generateXref();
		
		int counter = 0;
		for (KeggMetaboliteEntity cpdKegg : dao_kegg.findAll(KeggMetaboliteEntity.class)) {
			
			System.out.println(cpdKegg.getEntry());
			System.out.println(cpdKegg.getFormula());
			MetaboliteStga cpd_stga = loader.stageMetabolite(cpdKegg);
			dao_stga.save(cpd_stga);			
			counter++;
			if (counter % 50 == 0) {
				tx_stga.commit();
				tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
			}
		}
	}

}
