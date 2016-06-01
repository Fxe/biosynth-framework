package pt.uminho.sysbio.biosynth;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

public class TestNeo4jCore {
  
  private static GraphDatabaseService db;
  private static Transaction tx;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    
  }

  @Before
  public void setUp() throws Exception {
    db = new TestGraphDatabaseFactory().newImpermanentDatabase();
    tx = db.beginTx();
  }

  @After
  public void tearDown() throws Exception {
    tx.success(); tx.close();
    db.shutdown();
  }

  
  @Test
  public void testRelationshipDirection() {
    /**
     *   B <-- A <-> C
     *         ^
     *         |
     *         D
     */
    Node A = db.createNode(DynamicLabel.label("A"));
    Node B = db.createNode(DynamicLabel.label("B"));
    Node C = db.createNode(DynamicLabel.label("C"));
    Node D = db.createNode(DynamicLabel.label("D"));
    A.createRelationshipTo(C, DynamicRelationshipType.withName("link"));
    C.createRelationshipTo(A, DynamicRelationshipType.withName("link"));
    A.createRelationshipTo(B, DynamicRelationshipType.withName("link"));
    D.createRelationshipTo(A, DynamicRelationshipType.withName("link"));
    
    tx.success();
    tx.close();
    
    tx = db.beginTx();
    
    for (Relationship r : A.getRelationships()) {
      System.out.println(A.getLabels() + " <?> " + r.getOtherNode(A).getLabels() + "\t" + r.getId());
    }
    for (Relationship r : A.getRelationships(Direction.OUTGOING)) {
      System.out.println(A.getLabels() + " ==> " + r.getOtherNode(A).getLabels() + "\t" + r.getId());
    }
    for (Relationship r : A.getRelationships(Direction.INCOMING)) {
      System.out.println(A.getLabels() + " <== " + r.getOtherNode(A).getLabels() + "\t" + r.getId());
    }
    for (Relationship r : A.getRelationships(Direction.BOTH)) {
      System.out.println(A.getLabels() + " <?> " + r.getOtherNode(A).getLabels() + "\t" + r.getId());
    }
    
    assertEquals(true, true);
  }
}
