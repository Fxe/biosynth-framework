package pt.uminho.sysbio.biosynthframework.etl.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.bigg.Bigg2ReactionTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionEntity;

public class TestBiggReactionTransform {

  private Bigg2ReactionTransform transform;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    transform = new Bigg2ReactionTransform();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testBadReaction1() {
    Bigg2ReactionEntity rxn = null;
    GraphReactionEntity grxn = transform.apply(rxn);
    assertNull(grxn);
  }
  
  @Test
  public void testBadReaction2() {
    Bigg2ReactionEntity rxn = new Bigg2ReactionEntity();
    GraphReactionEntity grxn = transform.apply(rxn);
    assertNull(grxn);
  }

  @Test
  public void testReaction1() {
    Bigg2ReactionEntity rxn = new Bigg2ReactionEntity();
    rxn.setEntry("PYK");
    GraphReactionEntity grxn = transform.apply(rxn);
    assertNotNull(grxn);
    assertEquals("PYK", grxn.getProperties().get(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
  }
  
  @Test
  public void testReaction2() {
    Bigg2ReactionEntity rxn = new Bigg2ReactionEntity();
    rxn.setEntry("PYK");
    List<Bigg2ReactionCrossreferenceEntity> crossreferences = new ArrayList<>();
    Bigg2ReactionCrossreferenceEntity xref1 = new Bigg2ReactionCrossreferenceEntity();
    xref1.setLink("http://identifiers.org/ec-code/2.7.1.40");
    xref1.setValue("2.7.1.40");
    xref1.setRef("EC Number");
    xref1.setType(ReferenceType.ECNUMBER);
    crossreferences.add(xref1);
    rxn.setCrossreferences(crossreferences);
    GraphReactionEntity grxn = transform.apply(rxn);
    assertNotNull(grxn);
    assertEquals("PYK", grxn.getProperties().get(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
  }
}
