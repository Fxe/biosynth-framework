package edu.uminho.biosynth.core.data.integration.loader;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.service.BiocycService;
import pt.uminho.sysbio.biosynthframework.core.data.service.IMetaboliteService;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;
import edu.uminho.biosynth.core.data.integration.loader.ReferenceLoader;
import edu.uminho.biosynth.core.data.integration.references.IReferenceTransformer;
import edu.uminho.biosynth.core.data.integration.references.TransformBiocycMetaboliteCrossReference;

public class TestReferenceLoader {

	private static SessionFactory sessionFactory;
	private static IGenericDao dao;
	private static Transaction tx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_debug_pgsql.cfg.xml");
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
		IReferenceTransformer<BioCycMetaboliteCrossreferenceEntity> biocycXrefTrans = 
				new TransformBiocycMetaboliteCrossReference();
		ReferenceLoader<BioCycMetaboliteEntity, BioCycMetaboliteCrossreferenceEntity> biocycLoader = 
				new ReferenceLoader<>(BioCycMetaboliteEntity.class, BioCycMetaboliteCrossreferenceEntity.class, 
						biocycXrefTrans);
		IMetaboliteService<BioCycMetaboliteEntity> biocycService = new BiocycService(dao);
		biocycLoader.setService(biocycService);
		biocycLoader.setReferenceTransformer(biocycXrefTrans);
		BinaryGraph<ReferenceNode, ReferenceLink> refGraph = biocycLoader.getMetaboliteReferences("WATER");
		
		assertEquals(2, refGraph.size());
		assertEquals(3, refGraph.order());
	}

}
