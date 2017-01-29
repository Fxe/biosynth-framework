package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.SomeNodeFactory;
import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestNeo4jGraphMetaboliteDaoImpl {

  private static final Logger logger = LoggerFactory.getLogger(TestNeo4jGraphMetaboliteDaoImpl.class);

  private static GraphDatabaseService service;
  private static Neo4jGraphMetaboliteDaoImpl metaboliteDao;
  private static Transaction tx;

  @Before
  public void setUp() throws Exception {
    service = new TestGraphDatabaseFactory().newImpermanentDatabase();
    metaboliteDao = new Neo4jGraphMetaboliteDaoImpl(service);
  }

  @After
  public void tearDown() throws Exception {
    service.shutdown();
  }

  @Test
  public void test_save_proxy_metabolite_entity() {
    GraphMetaboliteEntity metabolite = new SomeNodeFactory()
        .withEntry("CPD-PX-10009")
        .buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.MetaCyc);

    tx = service.beginTx();
    metaboliteDao.saveMetabolite("", metabolite);
    tx.success();
    tx.close();

    assertNotNull(metabolite.getId());

    tx = service.beginTx();
    Node node = service.getNodeById(metabolite.getId());
    assertTrue(node.hasProperty("proxy"));
    assertTrue(node.hasLabel(MetaboliteMajorLabel.MetaCyc));
    assertTrue(node.hasLabel(GlobalLabel.Metabolite));
    assertTrue((boolean) node.getProperty("proxy"));
    tx.failure();
    tx.close();
  }

  @Test
  public void test_save_metabolite_entity_with_no_connected_link() {
    GraphMetaboliteEntity metabolite = new SomeNodeFactory()
        .withEntry("CX9999")
        .withLabel(MetaboliteMajorLabel.LigandCompound)
        .withLabel(GlobalLabel.KEGG)
        .withProperty("formula", "CHO")
        .withProperty("name", "name1; name2;")
        .buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandCompound);

    tx = service.beginTx();
    metaboliteDao.saveMetabolite("", metabolite);
    tx.success();
    tx.close();

    assertNotNull(metabolite.getId());

    tx = service.beginTx();
    Node node = service.getNodeById(metabolite.getId());
    assertTrue(node.hasProperty("proxy"));
    assertTrue(node.hasLabel(MetaboliteMajorLabel.LigandCompound));
    assertTrue(node.hasLabel(GlobalLabel.KEGG));    
    assertFalse((boolean) node.getProperty("proxy"));
    tx.failure();
    tx.close();
  }

  @Test
  public void test_save_duplicate_metabolite_entity_with_no_connected_link() {
    GraphMetaboliteEntity metabolite1 = new SomeNodeFactory()
        .withEntry("DD9999")
        .withLabel(MetaboliteMajorLabel.LigandDrug)
        .withLabel(GlobalLabel.KEGG)
        .withProperty("formula", "O10")
        .withProperty("name", "name1; name2;")
        .buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandCompound);

    tx = service.beginTx();
    metaboliteDao.saveMetabolite("", metabolite1);
    tx.success();
    tx.close();

    GraphMetaboliteEntity metabolite2 = new SomeNodeFactory()
        .withEntry("DD9999")
        .withLabel(MetaboliteMajorLabel.LigandDrug)
        .withLabel(GlobalLabel.KEGG)
        .withProperty("formula", "O20")
        .withProperty("name", "the only name")
        .buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandCompound);

    tx = service.beginTx();
    metaboliteDao.saveMetabolite("", metabolite2);
    tx.success();
    tx.close();

    assertNotNull(metabolite1.getId());
    assertNotNull(metabolite2.getId());

    tx = service.beginTx();
    Node node = Neo4jUtils.getUniqueResult(service
        .findNodesByLabelAndProperty(MetaboliteMajorLabel.LigandDrug, "entry", metabolite2.getEntry()));

    assertNotNull(node);
    assertEquals(Long.parseLong(metabolite2.getId().toString()), node.getId());
    assertEquals(metabolite2.getFormula(), node.getProperty("formula"));
    assertEquals(metabolite2.getName(), node.getProperty("name"));
    tx.failure();
    tx.close();
  }

  @Test
  public void test_save_metabolite_entity_with_properties() {
    GraphMetaboliteEntity metabolite = new SomeNodeFactory()
        .withEntry("DX9997")
        .withLabel(MetaboliteMajorLabel.LigandDrug)
        .withLabel(GlobalLabel.KEGG)
        .withProperty("formula", "COSP")
        .withProperty("name", "name1; name2; name3 (uuu);")
        .withProperty("pro", "abc")
        .withLinkTo(new SomeNodeFactory()
            .buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Charge, 0), 
            new SomeNodeFactory()
            .buildMetaboliteEdge(MetaboliteRelationshipType.has_charge))
        .withLinkTo(new SomeNodeFactory()
            .buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Name, "name1"), 
            new SomeNodeFactory()
            .buildMetaboliteEdge(MetaboliteRelationshipType.has_name))
        .withLinkTo(new SomeNodeFactory()
            .buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Name, "name2"), 
            new SomeNodeFactory()
            .buildMetaboliteEdge(MetaboliteRelationshipType.has_name))
        .withLinkTo(new SomeNodeFactory()
            .buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.MolecularFormula, "COSP"), 
            new SomeNodeFactory()
            .buildMetaboliteEdge(MetaboliteRelationshipType.has_molecular_formula))
        .buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);

    tx = service.beginTx();
    metaboliteDao.saveMetabolite("", metabolite);
    tx.success();
    tx.close();

    assertNotNull(metabolite.getId());
  }

  //	
  //	@BeforeClass
  //	public static void setUpBeforeClass() throws Exception {
  //		graphDatabaseService = HelperNeo4jConfigInitializer.initializeNeo4jDataDatabaseConstraints(NEO_DATA_DB_PATH);
  //		neo4jGraphMetaboliteDaoImpl = new Neo4jGraphMetaboliteDaoImpl(graphDatabaseService);
  //	}
  //
  //	@AfterClass
  //	public static void tearDownAfterClass() throws Exception {
  //		graphDatabaseService.shutdown();
  //	}


  //	
  //	@Test
  //	public void test_get_metabolite_entity_with_no_connected_link() {
  //		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
  //			.withEntry("CX9999")
  //			.withLabel(MetaboliteMajorLabel.LigandCompound)
  //			.withLabel(GlobalLabel.KEGG)
  //			.withProperty("formula", "CHO")
  //			.withProperty("name", "name1; name2;")
  //			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandCompound);
  //		
  //		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
  //		
  //		
  //		GraphMetaboliteEntity metabolite_ = neo4jGraphMetaboliteDaoImpl
  //				.getMetaboliteByEntry(metabolite.getMajorLabel(), metabolite.getEntry());
  //		
  //		assertEquals(metabolite.getEntry(), metabolite_.getEntry());
  //		assertEquals(metabolite.getFormula(), metabolite_.getFormula());
  //		assertEquals(metabolite.getName(), metabolite_.getName());
  //		assertEquals(metabolite.getId(), metabolite_.getId());
  //	}
  //	
  //	@Test
  //	public void test_save_metabolite_entity_with_crossreference() {
  //		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
  //			.withEntry("DX9998")
  //			.withLabel(MetaboliteMajorLabel.LigandDrug)
  //			.withLabel(GlobalLabel.KEGG)
  //			.withProperty("formula", "COSP")
  //			.withProperty("name", "name1; name2; name3 (uuu);")
  //			.withProperty("pro", "abc")
  //			.withLinkTo(new SomeNodeFactory()
  //				.withEntry("1-1-1")
  //				.buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.CAS), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_crossreference_to))
  //			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);
  //		
  //		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
  //		
  //		assertNotNull(metabolite.getId());
  //	}
  //	
  //	
  //	@Test
  //	public void test_get_metabolite_entity_with_crossreference() {
  //		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
  //			.withEntry("DX9998")
  //			.withLabel(MetaboliteMajorLabel.LigandDrug)
  //			.withLabel(GlobalLabel.KEGG)
  //			.withProperty("formula", "COSP")
  //			.withProperty("name", "name1; name2; name3 (uuu);")
  //			.withProperty("pro", "abc")
  //			.withLinkTo(new SomeNodeFactory()
  //				.withEntry("1-1-1")
  //				.buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.CAS), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_crossreference_to))
  //			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);
  //		
  //		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
  //		
  //		GraphMetaboliteEntity metabolite_ = neo4jGraphMetaboliteDaoImpl
  //				.getMetaboliteByEntry(metabolite.getMajorLabel(), metabolite.getEntry());
  //		
  //		assertEquals(metabolite.getEntry(), metabolite_.getEntry());
  //		assertEquals(metabolite.getFormula(), metabolite_.getFormula());
  //		assertEquals(metabolite.getName(), metabolite_.getName());
  //		assertEquals(metabolite.getId(), metabolite_.getId());
  //		assertEquals(1 , metabolite_.getConnectedEntities().size());
  //		 
  //	}
  //

  //	
  //	@Test
  //	public void test_get_metabolite_entity_with_properties() {
  //		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
  //			.withEntry("DX9997")
  //			.withLabel(MetaboliteMajorLabel.LigandDrug)
  //			.withLabel(GlobalLabel.KEGG)
  //			.withProperty("formula", "COSP")
  //			.withProperty("name", "name1; name2; name3 (uuu);")
  //			.withProperty("pro", "abc")
  //			.withLinkTo(new SomeNodeFactory()
  //				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Charge, 0), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_charge))
  //			.withLinkTo(new SomeNodeFactory()
  //				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Name, "name1"), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_name))
  //			.withLinkTo(new SomeNodeFactory()
  //				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Name, "name2"), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_name))
  //			.withLinkTo(new SomeNodeFactory()
  //				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.MolecularFormula, "COSP"), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_molecular_formula))
  //			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);
  //		
  //		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
  //		
  //		GraphMetaboliteEntity metabolite_ = neo4jGraphMetaboliteDaoImpl
  //				.getMetaboliteByEntry(metabolite.getMajorLabel(), metabolite.getEntry());
  //		
  //		System.out.println(metabolite_.getConnectionTypeCounter());
  //	}
  //	
  //	@Test
  //	public void test_get_metabolite_entity_with_many_properties_limit_set() {
  //		
  //		SomeNodeFactory nodeFactory = new SomeNodeFactory()
  //			.withEntry("DM1111")
  //			.withLabel(MetaboliteMajorLabel.LigandDrug)
  //			.withLabel(GlobalLabel.KEGG)
  //			.withProperty("formula", "Na1000")
  //			.withProperty("name", "name1; name2; name3 (uuu);")
  //			.withProperty("pro", "abc")
  //			.withLinkTo(new SomeNodeFactory()
  //				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Charge, 0), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_charge))
  //			.withLinkTo(new SomeNodeFactory()
  //				.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.MolecularFormula, "COSP"), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_molecular_formula));
  //		for (int i = 0; i < 100; i++) {
  //			nodeFactory.withLinkTo(
  //					new SomeNodeFactory()
  //					.buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel.Name, "name_" + i), 
  //					new SomeNodeFactory()
  //					.buildMetaboliteEdge(MetaboliteRelationshipType.has_name));
  //		}
  //		GraphMetaboliteEntity metabolite = nodeFactory.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);
  //		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
  //		
  //		GraphMetaboliteEntity metabolite_ = neo4jGraphMetaboliteDaoImpl
  //				.getMetaboliteByEntry(metabolite.getMajorLabel(), metabolite.getEntry());
  //		
  //		assertEquals(metabolite_.getEntry(), metabolite_.getEntry());
  //		assertEquals(metabolite_.getFormula(), metabolite_.getFormula());
  //		assertEquals(metabolite_.getName(), metabolite_.getName());
  //		assertEquals(metabolite_.getId(), metabolite_.getId());
  //		assertEquals(Neo4jGraphMetaboliteDaoImpl.RELATIONSHIP_TYPE_LIMIT, 
  //				metabolite_.getConnectedEntities().get(MetaboliteRelationshipType.has_name.toString()).size());
  //		for (String type : metabolite_.getConnectedEntities().keySet()) {
  //			System.out.println(type + ":" + metabolite_.getConnectionTypeCounter(type));
  ////			assertEquals(4 , metabolite_.getConnectedEntities().get(type).size());
  //		}
  //	}
  //	
  //	@Test
  //	public void test_save_metabolite_entity_proxy_state_preserve() {
  //		GraphMetaboliteEntity metabolite = new SomeNodeFactory()
  //			.withEntry("DP9998")
  //			.withLabel(MetaboliteMajorLabel.LigandDrug)
  //			.withLabel(GlobalLabel.KEGG)
  //			.withProperty("formula", "COSP")
  //			.withProperty("name", "name1; name2; name3 (uuu);")
  //			.withProperty("pro", "abc")
  //			.withLinkTo(new SomeNodeFactory()
  //				.withEntry("2-2-2")
  //				.buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.CAS), 
  //						new SomeNodeFactory()
  //				.buildMetaboliteEdge(MetaboliteRelationshipType.has_crossreference_to))
  //			.buildGraphMetaboliteEntity(MetaboliteMajorLabel.LigandDrug);
  //		
  //		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite);
  //		
  //		assertNotNull(metabolite.getId());
  //		
  //		GraphMetaboliteEntity metabolite_non_proxy = neo4jGraphMetaboliteDaoImpl.getMetaboliteById("", metabolite.getId());
  //		GraphMetaboliteEntity metabolite_is_proxy = neo4jGraphMetaboliteDaoImpl.getMetaboliteByEntry(MetaboliteMajorLabel.CAS.toString(), "2-2-2");
  //		
  //		assertFalse(metabolite_non_proxy.isProxy());
  //		assertTrue(metabolite_is_proxy.isProxy());
  //		
  //		GraphMetaboliteEntity metabolite_proxy_overlap = new SomeNodeFactory()
  //		.withEntry("CPD-PPPP")
  //		.withLabel(MetaboliteMajorLabel.MetaCyc)
  //		.withLabel(GlobalLabel.BioCyc)
  //		.withProperty("formula", "P100")
  //		.withProperty("name", "ppppppp")
  //		.withProperty("pro", "cbc")
  //		.withLinkTo(new SomeNodeFactory()
  //			.withEntry("DP9998")
  //			.buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel.LigandDrug), 
  //					new SomeNodeFactory()
  //			.buildMetaboliteEdge(MetaboliteRelationshipType.has_crossreference_to))
  //		.buildGraphMetaboliteEntity(MetaboliteMajorLabel.MetaCyc);
  //		
  //		neo4jGraphMetaboliteDaoImpl.saveMetabolite("", metabolite_proxy_overlap);
  //		
  //		assertNotNull(metabolite_proxy_overlap.getId());
  //		
  //		GraphMetaboliteEntity metabolite_proxy_overlap_non_proxy = neo4jGraphMetaboliteDaoImpl.getMetaboliteById("", metabolite_proxy_overlap.getId());
  //		GraphMetaboliteEntity metabolite_non_proxy_v2 = neo4jGraphMetaboliteDaoImpl.getMetaboliteById("", metabolite.getId());
  //		GraphMetaboliteEntity metabolite_is_proxy_v2 = neo4jGraphMetaboliteDaoImpl.getMetaboliteByEntry(MetaboliteMajorLabel.CAS.toString(), "2-2-2");
  //		
  //		assertFalse(metabolite_proxy_overlap_non_proxy.isProxy());
  //		assertFalse(metabolite_non_proxy_v2.isProxy());
  //		assertTrue(metabolite_is_proxy_v2.isProxy());
  //	}
}
