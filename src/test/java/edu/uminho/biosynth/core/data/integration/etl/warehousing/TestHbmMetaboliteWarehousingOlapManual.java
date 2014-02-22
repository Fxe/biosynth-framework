package edu.uminho.biosynth.core.data.integration.etl.warehousing;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.etl.staging.olap.IntegrationOLAP;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestHbmMetaboliteWarehousingOlapManual {

	public static SessionFactory sessionFactory_stga;
	private static IGenericDao dao_stga;
	private static Transaction tx_stga;
	public static SessionFactory sessionFactory_wh;
	private static IGenericDao dao_wh;
	private static Transaction tx_wh;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_wh = TestConfig.initializeHibernateSession("hibernate_production_warehouse_pgsql.cfg.xml");
//		sessionFactory_stga = TestConfig.initializeHibernateSession("hibernate_production_staging_example_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);
		
		dao_wh = new GenericEntityDaoImpl(sessionFactory_wh);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory_stga.close();
//		sessionFactory_wh.close();
	}

	@Before
	public void setUp() throws Exception {
		tx_stga = sessionFactory_stga.getCurrentSession().beginTransaction();
//		tx_wh = sessionFactory_wh.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_stga.commit();
//		tx_wh.commit();
	}

	@Test
	public void test() {
		IntegrationOLAP olap = new IntegrationOLAP();
		IKeyGenerator<Integer> generator= new IKeyGenerator<Integer>() {
			
			private int seq = 0;
			
			@Override
			public void reset() {
				this.seq = 0;
			}
			
			@Override
			public Integer generateKey() {
				return seq++;
			}
		};
		olap.setDao(dao_stga);
		olap.setIdGenerator(generator);
		olap.reset();
		
		System.out.println(olap);
		
		olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim", 0);
		
		System.out.println(olap);
		
		System.out.println(olap.getCluster(3));

		olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim", 3);
		
		System.out.println(olap);
		
		olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteSmilesDim", 2);

		System.out.println(olap);
	}

}
