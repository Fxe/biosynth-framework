package edu.uminho.biosynth.core.data.io.dao.kegg;

import static org.junit.Assert.*;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.GenericEntityDAO;

public class TestKeggMetaboliteDaoImpl {

	private static SessionFactory sessionFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		BasicConfigurator.configure();
//		PropertyConfigurator.configure("E://log4j.property");
		
		Configuration config = new Configuration();
		config.configure();
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		sessionFactory.openSession();
		
	}

	@After
	public void tearDown() throws Exception {
		sessionFactory.getCurrentSession().close();
	}

	@Test
	public void testInsertMetabolite() {
		GenericEntityDAO<KeggMetaboliteEntity> keggMetaboliteDao = new KeggMetaboliteDaoImpl(sessionFactory);
		KeggMetaboliteEntity cpd = new KeggMetaboliteEntity();
		cpd.setEntry("C00ABC");
		cpd.setComment("some comment");
		cpd.setDescription("this hand made");
		cpd.setFormula("H10OC1000");
		cpd.setMass(24.5);
		cpd.setMolWeight(12.6);
		cpd.setRemark("Same as: C00CBA");
		cpd.setSource("KEGG");
		cpd.setName("The metabolite; CBA; ABC;");
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		
		keggMetaboliteDao.addEntity(cpd);
		
		tx.commit();
	}
	
	private KeggMetaboliteEntity maybeKeggMetaboliteEntity(GenericEntityDAO<KeggMetaboliteEntity> keggMetaboliteDao, int i) {
		KeggMetaboliteEntity cpd = null;
		try {
			cpd = keggMetaboliteDao.getEntityById(i);
		} catch (ObjectNotFoundException onfEx) {
			
		}
		return cpd;
	}
	
	@Test
	public void testLoadMetabolite() throws Exception {
		GenericEntityDAO<KeggMetaboliteEntity> keggMetaboliteDao = new KeggMetaboliteDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();

		System.out.println(maybeKeggMetaboliteEntity(keggMetaboliteDao, 1));
		System.out.println(maybeKeggMetaboliteEntity(keggMetaboliteDao, 4));
		System.out.println(keggMetaboliteDao.getAllEntities());
		System.out.println(keggMetaboliteDao.contains(2));
		
		
//		System.out.println(maybeKeggMetaboliteEntity(keggMetaboliteDao, 5));
		
		tx.commit();
	}

}
