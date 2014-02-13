package edu.uminho.biosynth.core.data.integration.staging;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.dictionary.BioDbDictionary;
import edu.uminho.biosynth.core.data.integration.references.TransformBiocycMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.integration.staging.components.MetaboliteStga;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.service.BiggService;
import edu.uminho.biosynth.core.data.service.IMetaboliteService;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestStageBiocyc {

	public static SessionFactory sessionFactory_stga;
	public static SessionFactory sessionFactory_biocyc;
	private static IGenericDao dao_stga;
	private static IGenericDao dao_biocyc;
	private static Transaction tx_stga;
	private static Transaction tx_biocyc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		sessionFactory_biocyc = TestConfig.initializeHibernateSession("hibernate_production_pgsql.cfg.xml");
		dao_biocyc = new GenericEntityDaoImpl(sessionFactory_biocyc);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		
	}

	@Before
	public void setUp() throws Exception {
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
		tx_biocyc = sessionFactory_biocyc.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_stga.commit();
		tx_biocyc.commit();
	}

	@Test
	public void testStageBiocyc() {
		
		IMetaboliteService<BiggMetaboliteEntity> biggService = new BiggService(dao_biocyc);
		TransformBiocycMetaboliteCrossReference biocycXrefTrans =  new TransformBiocycMetaboliteCrossReference();
		biocycXrefTrans.setRefTransformMap(BioDbDictionary.getDbDictionary());

		List<BiggMetaboliteEntity> listBiggCpds = biggService.getAllMetabolites();
		Map<String, Map<String, String>> biggIdToEntryDictionary = new HashMap<> ();
		biggIdToEntryDictionary.put("BIGG", 
				new HashMap<String, String> ());
		for (BiggMetaboliteEntity biggCpd : listBiggCpds) {
			biggIdToEntryDictionary.get("BIGG")
				.put(biggCpd.getId().toString().toUpperCase(), biggCpd.getEntry().toUpperCase());
		}
		biocycXrefTrans.setValueTransformMap(biggIdToEntryDictionary);
		
		BiocycMetaboliteStageLoader loader = new BiocycMetaboliteStageLoader();
		loader.setDao(dao_stga);
		loader.setTransformer(biocycXrefTrans);
		
		int counter = 0;
		for (BioCycMetaboliteEntity cpdBiocyc : dao_biocyc.findAll(BioCycMetaboliteEntity.class)) {
			System.out.println(cpdBiocyc.getEntry());
			System.out.println(cpdBiocyc.getFormula());
			MetaboliteStga cpd_stga = loader.stageMetabolite(cpdBiocyc);
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
