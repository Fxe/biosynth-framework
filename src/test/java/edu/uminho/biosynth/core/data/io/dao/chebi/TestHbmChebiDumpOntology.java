package edu.uminho.biosynth.core.data.io.dao.chebi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpOntologyEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpOntologyRelationEntity;
import pt.uminho.sysbio.biosynthframework.biodb.ChebiDumpOntologyVertexEntity;
import pt.uminho.sysbio.biosynthframework.biodb.helper.HelperHbmConfigInitializer;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.hibernate.GenericEntityDaoImpl;

@SuppressWarnings("deprecation")
public class TestHbmChebiDumpOntology {
	
	private static SessionFactory sessionFactory_chebi;
	private static Transaction tx_chebi;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sessionFactory_chebi = HelperHbmConfigInitializer.initializeHibernateSession("hibernate_mysql_chebi_dump.cfg.xml");
		sessionFactory_chebi.openSession();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sessionFactory_chebi.getCurrentSession().close();
		sessionFactory_chebi.close();
	}

	@Before
	public void setUp() throws Exception {
		tx_chebi = sessionFactory_chebi.getCurrentSession().beginTransaction();
	}

	@After
	public void tearDown() throws Exception {
		tx_chebi.commit();
	}

	@Test
	public void testGetChebiOntology() {
		GenericEntityDaoImpl dao = new GenericEntityDaoImpl();
		dao.setSessionFactory(sessionFactory_chebi);
		ChebiDumpOntologyEntity ontologySet = dao.find(ChebiDumpOntologyEntity.class, 1L);
		
		assertEquals("Chemical Ontology", ontologySet.getTitle());
	}
	
	@Test
	public void testGetChebi15377() {
//		Outgoing
//		water (CHEBI:15377) has role amphiprotic solvent (CHEBI:48360) 
//		water (CHEBI:15377) has role greenhouse gas (CHEBI:76413) 
//		water (CHEBI:15377) is a inorganic hydroxy compound (CHEBI:52625) 
//		water (CHEBI:15377) is a mononuclear parent hydride (CHEBI:37176) 
//		water (CHEBI:15377) is a oxygen hydride (CHEBI:33693) 
//		water (CHEBI:15377) is conjugate acid of hydroxide (CHEBI:16234) 
//		water (CHEBI:15377) is conjugate base of oxonium (CHEBI:29412)
//		Incomming
//		hydrate (CHEBI:35505) has part water (CHEBI:15377)
//		(18O)water (CHEBI:33813) is a water (CHEBI:15377)
//		deuterium hydrogen oxide (CHEBI:33806) is a water (CHEBI:15377)
//		dideuterium oxide (CHEBI:41981) is a water (CHEBI:15377)
//		diprotium oxide (CHEBI:29375) is a water (CHEBI:15377)
//		ditritium oxide (CHEBI:29374) is a water (CHEBI:15377)
//		hydrogen tritium oxide (CHEBI:33811) is a water (CHEBI:15377)
//		oxonium (CHEBI:29412) is conjugate acid of water (CHEBI:15377)
//		hydroxide (CHEBI:16234) is conjugate base of water (CHEBI:15377)

		GenericEntityDaoImpl dao = new GenericEntityDaoImpl();
		dao.setSessionFactory(sessionFactory_chebi);
		
		
		List<?> ids = dao.query("SELECT cpd.id FROM ChebiDumpMetaboliteEntity cpd WHERE cpd.chebiAccession = 'CHEBI:15377'");
		if (ids == null) fail("CHEBI:15377 Accession Number not found");
		if (ids.isEmpty() || ids.size() > 1) fail("CHEBI:15377 Accession Number not unique");
		Integer cpdId = (Integer) ids.iterator().next();
		
		List<?> vertexIds = dao.query(String.format("SELECT v.id FROM ChebiDumpOntologyVertexEntity v WHERE v.compoundId = %d", cpdId));
		if (vertexIds == null) fail("CHEBI:15377 Vertex not found");
		if (vertexIds.isEmpty() || ids.size() > 1) fail("CHEBI:15377 Vertex not unique");
		
		Long vertexId = (Long) vertexIds.iterator().next();
		ChebiDumpOntologyVertexEntity chebi_15377_vertex = dao.find(ChebiDumpOntologyVertexEntity.class, vertexId);
		
		System.out.println(chebi_15377_vertex);
		
		for (ChebiDumpOntologyRelationEntity rel : chebi_15377_vertex.getChebiDumpOntologyOutgoingRelationEntities()) {
			System.out.println(rel);
		}
		
		for (ChebiDumpOntologyRelationEntity rel : chebi_15377_vertex.getChebiDumpOntologyIncommingRelationEntities()) {
			System.out.println(rel);
		}
	}

}
