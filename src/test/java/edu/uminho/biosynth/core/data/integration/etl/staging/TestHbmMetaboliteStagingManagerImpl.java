package edu.uminho.biosynth.core.data.integration.etl.staging;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteNameGroupDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteXrefGroupDim;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestHbmMetaboliteStagingManagerImpl {

	public static SessionFactory sessionFactory_stga;
	private static IGenericDao dao_stga;
	private static Transaction tx_stga;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = TestConfig.initializeHibernateSession("hibernate_production_staging_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		
	}

	@Before
	public void setUp() throws Exception {
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_stga.commit();
	}

	@Test
	public void test() {
		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
		manager.setDao(dao_stga);
		
	}
	
	@Test
	public void testGetNullFormula() {
		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
		manager.setDao(dao_stga);
		MetaboliteFormulaDim nullFormula = manager.getNullFormulaDim();
		
		assertEquals(IMetaboliteStagingManager.NULL_FORMULA, nullFormula.getFormula());
		
	}
	
	@Test
	public void testGetNullInchi() {
		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
		manager.setDao(dao_stga);
		MetaboliteInchiDim nullInchi = manager.getNullInchiDim();
		
		assertEquals(IMetaboliteStagingManager.NULL_INCHI, nullInchi.getInchi());
	}

	@Test
	public void testGetNullNameGroup() {
		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
		manager.setDao(dao_stga);
		MetaboliteNameGroupDim nullNameGroup = manager.getNullNameGroupDim();
		System.out.println(nullNameGroup);
		System.out.println(nullNameGroup.getId());
		assertEquals(0, nullNameGroup.getMetaboliteNameBridges().size());
	}
	
	@Test
	public void testGetNullXrefGroup() {
		HbmMetaboliteStagingManagerImpl manager = new HbmMetaboliteStagingManagerImpl();
		manager.setDao(dao_stga);
		MetaboliteXrefGroupDim nullXrefGroup = manager.getNullXrefGroupDim();
		System.out.println(nullXrefGroup);
		System.out.println(nullXrefGroup.getId());
		assertEquals(0, nullXrefGroup.getMetaboliteXrefBridges().size());
	}
}
