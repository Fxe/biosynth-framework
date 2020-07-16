package pt.uminho.sysbio.biosynthframework.etl.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.ModelSeedMetaboliteTransform;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedMetaboliteEntity;

public class TestModelSeedMetaboliteTransform {

  private ModelSeedMetaboliteTransform transform;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    transform = new ModelSeedMetaboliteTransform();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testBadMetabolite1() {
    ModelSeedMetaboliteEntity cpd = null;
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
    assertNull(gcpd);
  }
  
  @Test
  public void testBadMetabolite2() {
    ModelSeedMetaboliteEntity cpd = new ModelSeedMetaboliteEntity();
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
    assertNull(gcpd);
  }
  
  @Test
  public void testMetabolite1() {
    ModelSeedMetaboliteEntity cpd = new ModelSeedMetaboliteEntity();
    cpd.setEntry("cpd00001");
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
    assertNotNull(gcpd);
    assertEquals("cpd00001", gcpd.getProperties().get(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
  }

  @Test
  public void testMetabolite2() {
    
    ModelSeedMetaboliteEntity cpd = new ModelSeedMetaboliteEntity();
    cpd.setEntry("cpd00001");
    ModelSeedMetaboliteCrossreferenceEntity xref1 = 
        new ModelSeedMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "BiGG1", "oh1");
    ModelSeedMetaboliteCrossreferenceEntity xref2 = 
        new ModelSeedMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "BiGG", "oh1");
    List<ModelSeedMetaboliteCrossreferenceEntity> crossreferences = new ArrayList<>();
    crossreferences.add(xref1);
    crossreferences.add(xref2);
    cpd.setCrossreferences(crossreferences);
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
    assertNotNull(gcpd);
    assertEquals("cpd00001", gcpd.getProperties().get(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
    assertNotNull(gcpd.getConnectedEntities());
    assertTrue(gcpd.getConnectedEntities().containsKey("has_crossreference_to"));
    System.out.println(gcpd.getConnectedEntities().get("has_crossreference_to"));
//    assertTrue(condition);
//    org.hamcrest.CoreMatchers.any(gcpd.getConnectedEntities().get("has_crossreference_to"));
//    System.out.println(gcpd.getConnectedEntities().get("has_crossreference_to").stream().filter(e -> e.getRight().getMajorLabel().equals("BiGG")));
    
  }
}
