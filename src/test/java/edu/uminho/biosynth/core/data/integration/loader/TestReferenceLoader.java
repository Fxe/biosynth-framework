package edu.uminho.biosynth.core.data.integration.loader;

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

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;
import edu.uminho.biosynth.core.data.integration.loader.ReferenceLoader;
import edu.uminho.biosynth.core.data.integration.references.IReferenceTransformer;
import edu.uminho.biosynth.core.data.integration.references.TransformBiocycMetaboliteCrossReference;
import edu.uminho.biosynth.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import edu.uminho.biosynth.core.data.service.BiocycService;
import edu.uminho.biosynth.core.data.service.IMetaboliteService;

public class TestReferenceLoader {

	private static SessionFactory sessionFactory;
	private static IGenericDao dao;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		Configuration config = new Configuration().configure("hibernate_debug_pgsql.cfg.xml");
		System.out.println(config.getProperty("hibernate.dialect"));
		
		ServiceRegistry servReg = 
				new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(servReg);
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
	public void test() {
		IReferenceTransformer<BioCycMetaboliteCrossReferenceEntity> biocycXrefTrans = 
				new TransformBiocycMetaboliteCrossReference();
		ReferenceLoader<BioCycMetaboliteEntity, BioCycMetaboliteCrossReferenceEntity> biocycLoader = 
				new ReferenceLoader<>(BioCycMetaboliteEntity.class, BioCycMetaboliteCrossReferenceEntity.class, 
						biocycXrefTrans);
		IMetaboliteService<BioCycMetaboliteEntity> biocycService = new BiocycService(dao);
		biocycLoader.setService(biocycService);
		biocycLoader.setReferenceTransformer(biocycXrefTrans);
		IBinaryGraph<ReferenceNode, ReferenceLink> refGraph = biocycLoader.getMetaboliteReferences("WATER");
		
		assertEquals(2, refGraph.size());
		assertEquals(3, refGraph.order());
	}

}
