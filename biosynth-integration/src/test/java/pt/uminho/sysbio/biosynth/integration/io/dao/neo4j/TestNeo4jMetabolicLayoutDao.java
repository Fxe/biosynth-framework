package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetabolicLayoutDao;
import pt.uminho.sysbio.biosynthframework.LayoutNode;
import pt.uminho.sysbio.biosynthframework.LayoutNode.LayoutNodeType;
import pt.uminho.sysbio.biosynthframework.MetabolicLayout;

public class TestNeo4jMetabolicLayoutDao {

  private GraphDatabaseService service;
  private Transaction tx;
  private MetabolicLayoutDao dao;
  
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
    dao = new Neo4jMetabolicLayoutDao(service);
    tx = service.beginTx();
  }

  @After
  public void tearDown() throws Exception {
    tx.failure(); tx.close();
    service.shutdown();
  }

  @Test
  public void test_save_layout_node_1() {
    MetabolicLayout metabolicLayout = new MetabolicLayout();
    metabolicLayout.setEntry("abc");
    metabolicLayout.setDescription("some layout");
    metabolicLayout.setName("ecoli");
    metabolicLayout.nodes.put(1L, new LayoutNode());

    Long id = dao.save(metabolicLayout);
    assertNotNull(id);
    assertNotNull(metabolicLayout.getId());
    assertEquals(id, metabolicLayout.getId());
  }

  @Test
  public void test_save_get_layout_node_1() {
    MetabolicLayout l1 = new MetabolicLayout();
    l1.setEntry("abc");
    l1.setDescription("some layout");
    l1.setName("ecoli");

    Long id = dao.save(l1);
    assertNotNull(id);
    assertNotNull(l1.getId());
    assertEquals(id, l1.getId());
    
    MetabolicLayout l2 = dao.findById(id);
    assertEquals(l1.getId(), l2.getId());
    assertEquals(l1.getEntry(), l2.getEntry());
    assertEquals(l1.getDescription(), l2.getDescription());
    assertEquals(l1.getName(), l2.getName());
  }
}
