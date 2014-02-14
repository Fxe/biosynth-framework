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
import edu.uminho.biosynth.core.data.integration.references.TransformKeggMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteServiceDim;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestStagingInsertion {
	
	public static SessionFactory sessionFactory;
	private static IGenericDao dao;
	private static Transaction tx;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
		dao = new GenericEntityDaoImpl(sessionFactory);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		
	}

	@Before
	public void setUp() throws Exception {
		tx = sessionFactory.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx.commit();
	}

	@Test
	public void testAddMetaboliteWithNoDimensions() {
		MetaboliteStga cpdStaging = new MetaboliteStga();
		cpdStaging.setNumeryKey(0);
		cpdStaging.setTextKey("compound-0");
		
//		MetaboliteFormulaDim formula_dim = new MetaboliteFormulaDim();
//		formula_dim.getMetaboliteStgas().add(cpdStaging);
//		formula_dim.setFormula("H2O");
//		
//		cpdStaging.setMetaboliteFormulaDim(formula_dim);
		
		dao.save(cpdStaging);
	}
	
	@Test
	public void testAddMetaboliteWithSingle() {
		MetaboliteStga cpdStaging = new MetaboliteStga();
		cpdStaging.setNumeryKey(0);
		cpdStaging.setTextKey("C00001");
		
		MetaboliteFormulaDim formula_h2o = new MetaboliteFormulaDim();
//		formula_h2o.setId(0);
		formula_h2o.setFormula("H2O");
		dao.save(formula_h2o);
		
		cpdStaging.setFormula("H2O");
		cpdStaging.setMetaboliteFormulaDim(formula_h2o);
		
		dao.save(cpdStaging);
	}

	@Test 
	public void testStageKeggMetabolite() {
		TransformKeggMetaboliteCrossReference transformer = new TransformKeggMetaboliteCrossReference();
		KeggMetaboliteStageLoader keggStageLoader = new KeggMetaboliteStageLoader();
		keggStageLoader.setTransformer(transformer);
		keggStageLoader.setDao(dao);
		KeggMetaboliteEntity cpdKegg1 = new KeggMetaboliteEntity();
		cpdKegg1.setId(283);
		cpdKegg1.setEntry("C98222");
		cpdKegg1.setFormula("H20C90O100");
		cpdKegg1.setRemark(":)");
		cpdKegg1.setComment("manual");
		cpdKegg1.setDescription("fake");
		cpdKegg1.setName("some crazy compound; compound xpto;");
		MetaboliteStga cpd_stga = keggStageLoader.stageMetabolite(cpdKegg1);
		
		dao.save(cpd_stga);
	}
}
