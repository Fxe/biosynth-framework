package edu.uminho.biosynth.core.data.integration.etl.staging.olap;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.test.config.TestConfig;

public class TestStagingOlap {

	public static SessionFactory sessionFactory_stga;
	private static IGenericDao dao_stga;
	private static Transaction tx_stga;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_stga = TestConfig.initializeHibernateSession("hibernate_production_staging_example_pgsql.cfg.xml");
		dao_stga = new GenericEntityDaoImpl(sessionFactory_stga);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory_stga.close();
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
		
		IntegrationOLAP olap = new IntegrationOLAP();
		olap.setDao(dao_stga);
		Set<Integer> initialCube = new HashSet<> ();
		for (Object o : dao_stga.query("SELECT cpd.id FROM MetaboliteStga cpd")) {
			initialCube.add((Integer) o) ;
		}
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
			
			@Override
			public void generateFromLastElement(Integer key) {
				System.out.println(":):):):):)");
			}
		};
		System.out.println(initialCube.size());
		
		Set<Serializable> dimFormula = 
				olap.getDimensionElements("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim", initialCube);
		System.out.println(dimFormula);
		
		Map<Integer, Set<Serializable>> newClusters = new HashMap<> ();
		for (Serializable dimId : dimFormula) {
			Set<Serializable> slice = olap.slice("edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteFormulaDim", dimId, initialCube);
			newClusters.put(generator.generateKey(), slice);
		}
		
		for (Integer cid : newClusters.keySet()) {
			System.out.println("Cluster " + cid + " -> " + newClusters.get(cid).size());
		}
	}

}
