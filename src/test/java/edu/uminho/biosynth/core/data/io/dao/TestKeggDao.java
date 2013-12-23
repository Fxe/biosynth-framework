package edu.uminho.biosynth.core.data.io.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.kegg.KeggReactionEntity;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestKeggDao {

	private static SessionFactory sessionFactory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration config = new Configuration();
		config.configure();
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory.close();
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
	public void testSaveMetabolite() {
		IGenericEntityDao dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		KeggMetaboliteEntity cpd = new KeggMetaboliteEntity();
		cpd.setEntry("T00755");
		cpd.setName("4-Hydroxy-3-methoxy-benzaldehyde;Vanillin;Vanillaldehyde;4-Hydroxy-3-methoxybenzaldehyde");
		cpd.setFormula("C8H8O3");
		cpd.setMass(152.0473);
		cpd.setMolWeight(152.1473);
		cpd.setRemark("Same as: D00091");
		cpd.setDescription("Manual");
		cpd.setSource("KEGG");
		cpd.setMetaboliteClass("COMPOUND");
		String[] enzymes = {"1.1.3.38", "1.2.1.67", "1.2.3.9", "1.13.11.43", "4.1.2.41"};
		cpd.setEnzymes(new ArrayList<String> (Arrays.asList(enzymes)));
		dao.save(cpd);
		tx.commit();
		
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testLoadMetabolite() {
		IGenericEntityDao dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		KeggMetaboliteEntity cpd1 = dao.find(KeggMetaboliteEntity.class, 1);
		KeggMetaboliteEntity cpd2 = dao.criteria(KeggMetaboliteEntity.class, Restrictions.eq("entry", "T00755")).get(0);
		System.out.println(cpd1.getFormula());
		System.out.println(cpd2.getName());
		tx.commit();
		
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testSaveReaction() {
		IGenericEntityDao dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		KeggReactionEntity rxn = new KeggReactionEntity();
		rxn.setEntry("R00010");
		rxn.setName("alpha,alpha-trehalose glucohydrolase");
		rxn.setDefinition("alpha,alpha-Trehalose + H2O <=> 2 D-Glucose");
		rxn.setEquation("C01083 + C00001 <=> 2 C00031");
		rxn.setRemark("Same as: R06103");
		rxn.setComment("No comment");
		rxn.setSource("KEGG");
		rxn.setEnzymes(Arrays.asList(new String[] {"3.2.1.28"}));
		rxn.setPathways(Arrays.asList(new String[] {"rn00500", "rn01100"}));
		rxn.setRpairs(Arrays.asList(new String[] {"RP00453", "RP05679"}));
		rxn.setOrthologies(Arrays.asList(new String[] {"K01194"}));
		dao.save(rxn);
		tx.commit();
		
		
		fail("Not yet implemented");
	}

}
