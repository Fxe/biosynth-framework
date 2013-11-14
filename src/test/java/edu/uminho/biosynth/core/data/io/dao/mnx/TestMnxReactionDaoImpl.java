package edu.uminho.biosynth.core.data.io.dao.mnx;

import static org.junit.Assert.*;

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

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.mnx.MnxReactionEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionProductEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionReactantEntity;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestMnxReactionDaoImpl {
	
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
	public void testInsertReaction() {
		GenericEntityDaoImpl dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		MnxReactionEntity rxn = new MnxReactionEntity();
		rxn.setEntry("MNXR37");
		rxn.setOrientation(0);
		rxn.setBalanced(true);
		rxn.setEquation("1 MNXM2 + 1 MNXM3149 = 1 MNXM1 + 1 MNXM2183");
		rxn.setDefinition("1 H2O + 1 2',3'-cyclic GMP = 1 H(+) + 1 3'-GMP");
		rxn.setOriginalSource("rhea:27858");
		rxn.addEnzyme("3.1.4.16");
		
		MnxReactionReactantEntity lp1 = new MnxReactionReactantEntity();
		lp1.setCpdEntry("MNXM2");
		lp1.setValue(1);
		MnxReactionReactantEntity lp2 = new MnxReactionReactantEntity();
		lp2.setCpdEntry("MNXM3149");
		lp2.setValue(1);
		MnxReactionProductEntity rp1 = new MnxReactionProductEntity();
		rp1.setCpdEntry("MNXM1");
		rp1.setValue(1);
		MnxReactionProductEntity rp2 = new MnxReactionProductEntity();
		rp2.setCpdEntry("MNXM2183");
		rp2.setValue(1);
		rxn.addLeft(lp1);
		rxn.addLeft(lp2);
		rxn.addRight(rp1);
		rxn.addRight(rp2);
		
		MnxReactionCrossReferenceEntity xref1 = 
				new MnxReactionCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "bigg", "23PDE9pp");
		MnxReactionCrossReferenceEntity xref2 = 
				new MnxReactionCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "kegg", "R05135");
		MnxReactionCrossReferenceEntity xref3 = 
				new MnxReactionCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "metacyc", "RXN-14064");
		MnxReactionCrossReferenceEntity xref4 = 
				new MnxReactionCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "rhea", "27858");
		MnxReactionCrossReferenceEntity xref5 = 
				new MnxReactionCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "rhea", "27859");
		rxn.addCrossReference(xref1);
		rxn.addCrossReference(xref2);
		rxn.addCrossReference(xref3);
		rxn.addCrossReference(xref4);
		rxn.addCrossReference(xref5);

		dao.save(rxn);
		tx.commit();
//		fail("Not yet implemented");
	}
	
	@Test
	public void testLoadReactions() {
		GenericEntityDaoImpl dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		assertEquals(1, dao.findAll(MnxReactionEntity.class).size());
		tx.commit();
	}

}
