package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynthframework.LayoutNode;
import pt.uminho.sysbio.biosynthframework.LayoutNode.LayoutNodeType;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class TestNeo4jLayoutNodeDao {

  private GraphDatabaseService service;
  private Transaction tx;
  private Neo4jLayoutNodeDao dao;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    service = new TestGraphDatabaseFactory().newImpermanentDatabase();
    HelperNeo4jConfigInitializer.executeNeo4jLayoDatabaseConstraints(service);
    dao = new Neo4jLayoutNodeDao(service);
    tx = service.beginTx();
  }

  @After
  public void tearDown() throws Exception {
    tx.failure(); tx.close();
    service.shutdown();
  }

  @Test
  public void test_create_node() {
    Node node = service.createNode();
    assertNotNull(node);
  }

  @Test
  public void test_save_layout_node_1() {
    LayoutNode node = new LayoutNode();
    node.type = LayoutNodeType.SPECIE;
    node.x = 80.9;
    node.y = 90.9;
    node.label = "hi";
    Long id = dao.save(node);
    assertNotNull(id);
  }
  
  @Test
  public void test_save_get_layout_node_1() {
    LayoutNode node = new LayoutNode();
    node.type = LayoutNodeType.SPECIE;
    node.x = 80.9;
    node.y = 90.9;
    node.label = "hi";
    Long id = dao.save(node);
    
    assertNotNull(id);
    
    node = dao.findById(id);
    assertNotNull(node);
  }
  
  @Test
  public void test_save_get_layout_node_2() {
    LayoutNode node1 = new LayoutNode();
    node1.setDescription("abc");
    node1.setSource("biosystems");
    node1.compartment = SubcellularCompartment.NUCLEUS;
    node1.type = LayoutNodeType.SPECIE;
    node1.x = 80.9;
    node1.y = 90.9;
    node1.label = "hi";
    Long id = dao.save(node1);
    
    assertNotNull(id);
    
    LayoutNode node2 = dao.findById(id);
    
    assertNotNull(node2);
    assertEquals("wrong value", node1.getId(), node2.getId());
    assertEquals("wrong value", node1.getDescription(), node2.getDescription());
    assertEquals("wrong value", node1.getSource(), node2.getSource());
    assertEquals("wrong value", node1.label, node2.label);
    assertEquals("wrong value", node1.type, node2.type);
    assertEquals("wrong value", node1.compartment, node2.compartment);
    assertEquals("wrong value", node1.x, node2.x);
    assertEquals("wrong value", node1.y, node2.y);
  }
  
  @Test
  public void test_save_get_layout_node_3() {
    LayoutNode node1 = new LayoutNode();
    node1.setDescription("abc");
    node1.setSource("biosystems");
    node1.compartment = SubcellularCompartment.NUCLEUS;
    node1.type = LayoutNodeType.SPECIE;
    node1.x = 80.9;
    node1.y = 90.9;
    node1.label = "hi";
    node1.addAnnotation(MetaboliteMajorLabel.BiGG.toString(), 9L, "h2o");
    node1.addAnnotation(MetaboliteMajorLabel.ChEBI.toString(), 1L, "15444");
    node1.addAnnotation(MetaboliteMajorLabel.ChEBI.toString(), 0L, "15445");
    node1.addAnnotation(MetaboliteMajorLabel.LigandCompound.toString(), 5L, "C00001");
    Long id = dao.save(node1);
    
    assertNotNull(id);
    
    LayoutNode node2 = dao.findById(id);
    
    assertNotNull(node2);
    assertEquals("wrong value", node1.getId(), node2.getId());
    assertEquals("wrong value", node1.getDescription(), node2.getDescription());
    assertEquals("wrong value", node1.getSource(), node2.getSource());
    assertEquals("wrong value", node1.label, node2.label);
    assertEquals("wrong value", node1.type, node2.type);
    assertEquals("wrong value", node1.compartment, node2.compartment);
    assertEquals("wrong value", node1.x, node2.x);
    assertEquals("wrong value", node1.y, node2.y);
    assertNotNull("", node2.annotation.get("BiGG"));
    assertNotNull("", node2.annotation.get("ChEBI"));
    assertNotNull("", node2.annotation.get("LigandCompound"));
  }
  
  @Test(expected=NotFoundException.class)
  public void test_get_layout_node_fail_1() {
    dao.findById(343);
  }
  
  @Test
  public void test_update_annotation_1() {
    Map<String, Map<Long, String>> annotation = new HashMap<> ();
    annotation.put("DB1", new HashMap<Long, String> ());
    annotation.put("DB2", new HashMap<Long, String> ());
    annotation.put("DB3", new HashMap<Long, String> ());
    annotation.get("DB1").put(9L, "h2o");
    annotation.get("DB2").put(1L, "15444");
    annotation.get("DB2").put(0L, "15445");
    annotation.get("DB3").put(5L, "C00001");
    LayoutNode node1 = new LayoutNode();
    node1.setDescription("abc");
    node1.setSource("biosystems");
    node1.compartment = SubcellularCompartment.NUCLEUS;
    node1.type = LayoutNodeType.SPECIE;
    node1.x = 80.9;
    node1.y = 90.9;
    node1.label = "hi";
    node1.annotation.putAll(annotation);
    
    Long id = dao.save(node1);
    
    assertNotNull(id);
    
    dao.updateAnnotation(id, annotation, Neo4jLayoutLabel.MetaboliteReference.toString());
  }
  
  @Test
  public void test_update_annotation_2() {
    Map<String, Map<Long, String>> annotation = new HashMap<> ();
    annotation.put("DB1", new HashMap<Long, String> ());
    annotation.put("DB2", new HashMap<Long, String> ());
    annotation.put("DB3", new HashMap<Long, String> ());
    annotation.get("DB1").put(9L, "h2o");
    annotation.get("DB2").put(1L, "15444");
    annotation.get("DB2").put(0L, "15445");
    annotation.get("DB3").put(5L, "C00001");
    LayoutNode node1 = new LayoutNode();
    node1.setDescription("abc");
    node1.setSource("biosystems");
    node1.compartment = SubcellularCompartment.NUCLEUS;
    node1.type = LayoutNodeType.SPECIE;
    node1.x = 80.9;
    node1.y = 90.9;
    node1.label = "hi";
    node1.annotation.putAll(annotation);
    
    Long id = dao.save(node1);
    annotation.remove("DB2");
    assertNotNull(id);
    
    dao.updateAnnotation(id, annotation, Neo4jLayoutLabel.MetaboliteReference.toString());
  }
}
