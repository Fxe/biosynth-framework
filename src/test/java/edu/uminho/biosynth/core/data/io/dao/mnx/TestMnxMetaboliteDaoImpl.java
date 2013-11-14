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
import edu.uminho.biosynth.core.components.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;

public class TestMnxMetaboliteDaoImpl {
	
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
	public void testInsertNewMetabolite() {
		GenericEntityDaoImpl dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		MnxMetaboliteEntity cpd1 = new MnxMetaboliteEntity();
		cpd1.setCharge(1);
		cpd1.setEntry("MNXM1");
		cpd1.setFormula("H");
		cpd1.setName("H(+)");
		cpd1.setMass(1.008);
		cpd1.setInChI("InChI=1S/p+1");
		cpd1.setSmiles("[H+]");
		cpd1.setOriginalSource("chebi:15378");
		//hmdb:HMDB59597	MNXM1	inferred	Hydrogen Ion|hydron
		MnxMetaboliteCrossReferenceEntity xref1 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "chebi", "15378");
		xref1.setEvidence("identity");
		xref1.setDescription("H(+)|H(+)|hydrogen(1+)|hydron|H+");
		MnxMetaboliteCrossReferenceEntity xref2 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "chebi", "13357");
		xref2.setEvidence("identity");
		xref2.setDescription("H(+)|H(+)|hydrogen(1+)|hydron|H+");
		MnxMetaboliteCrossReferenceEntity xref3 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "chebi", "10744");
		xref3.setEvidence("identity");
		xref3.setDescription("H(+)|H(+)|hydrogen(1+)|hydron|H+");
		MnxMetaboliteCrossReferenceEntity xref4 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "chebi", "5584");
		xref4.setEvidence("identity");
		xref4.setDescription("H(+)|H(+)|hydrogen(1+)|hydron|H+");
		
		MnxMetaboliteCrossReferenceEntity xref5 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "bigg", "h");
		xref5.setEvidence("inferred");
		xref5.setDescription("H+");
		MnxMetaboliteCrossReferenceEntity xref6 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "biopath", "Proton");
		xref6.setEvidence("inferred");
		xref6.setDescription("Proton|HEXT|H+|External H+");
		MnxMetaboliteCrossReferenceEntity xref7 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "brenda", "BG79440");
		xref7.setEvidence("inferred");
		xref7.setDescription("H+/in|proton|H +|H+/out|H+|H+out");
		MnxMetaboliteCrossReferenceEntity xref8 = 
				new MnxMetaboliteCrossReferenceEntity(GenericCrossReference.Type.DATABASE, "metacyc", "PROTON");
		xref8.setEvidence("inferred");
		xref8.setDescription("H+|proton|PROTON|hydrogen ion|H");
		
		cpd1.addCrossReference(xref1);
		cpd1.addCrossReference(xref2);
		cpd1.addCrossReference(xref3);
		cpd1.addCrossReference(xref4);
		cpd1.addCrossReference(xref5);
		cpd1.addCrossReference(xref6);
		cpd1.addCrossReference(xref7);
		cpd1.addCrossReference(xref8);
		
		dao.save(cpd1);
		tx.commit();
	}
	
	@Test
	public void testLoadMetabolite() {
		GenericEntityDaoImpl dao = new GenericEntityDaoImpl(sessionFactory);
		Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
		MnxMetaboliteEntity cpd = dao.find(MnxMetaboliteEntity.class, 1);
		System.out.println(cpd);
		assertEquals(8, cpd.getCrossReferences().size());
		tx.commit();
	}


}
