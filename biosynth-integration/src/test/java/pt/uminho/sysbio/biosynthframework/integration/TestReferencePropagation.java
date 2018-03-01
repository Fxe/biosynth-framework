package pt.uminho.sysbio.biosynthframework.integration;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.integration.model.IntegrationMap;

public class TestReferencePropagation {

  private ReferencePropagation propagation; 
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    propagation = new ReferencePropagation();
  }

  @After
  public void tearDown() throws Exception {
    propagation = null;
  }

  @Test
  public void test_add_conflict() {
    propagation.addReference("cpd_c", "c",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    propagation.addReference("cpd_e", "c",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    
    Set<Set<String>> result = propagation.getConflicts();
    
    assertTrue("expected non empty set", !result.isEmpty());
  }
  
  @Test
  public void test_add_ok() {
    propagation.addReference("cpd_c", "c",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    propagation.addReference("cpd_e", "e",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    
    Set<Set<String>> result = propagation.getConflicts();
    
    assertTrue("expected empty set", result.isEmpty());
  }

  @Test
  public void test_add_propagation() {
    propagation.addReference("cpd_c", "c",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    propagation.addReference("cpd_e", "e",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    propagation.addReference("cpd_e", "e",
                             MetaboliteMajorLabel.BiGG, "h2o");
    
    IntegrationMap<String, MetaboliteMajorLabel> result = propagation.propagate();
    
    assertNotNull(result.get("cpd_c"));
    assertNotNull(result.get("cpd_e"));
    assertTrue("expected two references", result.get("cpd_c").size() == 2);
    assertTrue("expected two references", result.get("cpd_e").size() == 2);
  }
  
  @Test
  public void test_add_propagation_w_ccs() {
    ConnectedComponents<String> ccs = new ConnectedComponents<>();
    Set<String> cc = new HashSet<>();
    cc.add("C00001@LigandCompound");
    cc.add("h2o@BiGG");
    cc.add("META:WATER@MetaCyc");
    ccs.add(cc);
    propagation.addReference("cpd_c", "c",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    propagation.addReference("cpd_e", "e",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    
    IntegrationMap<String, MetaboliteMajorLabel> result = 
        propagation.propagate(false, ccs);
    
    assertNotNull(result.get("cpd_c"));
    assertNotNull(result.get("cpd_e"));
    assertEquals(3, result.get("cpd_c").size());
    assertEquals(3, result.get("cpd_e").size());
  }
  
  @Test
  public void test_add_propagation_w_ccs_safe_ok() {
    ConnectedComponents<String> ccs = new ConnectedComponents<>();
    Set<String> cc = new HashSet<>();
    cc.add("C00001@LigandCompound");
    cc.add("h2o@BiGG");
    cc.add("META:WATER@MetaCyc");
    ccs.add(cc);
    propagation.addReference("cpd_c", "c",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    propagation.addReference("cpd_e", "e",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    
    IntegrationMap<String, MetaboliteMajorLabel> result = 
        propagation.propagate(true, ccs);
    
    assertNotNull(result.get("cpd_c"));
    assertNotNull(result.get("cpd_e"));
    assertEquals(3, result.get("cpd_c").size());
    assertEquals(3, result.get("cpd_e").size());
  }
  
  @Test
  public void test_add_propagation_w_ccs_safe_fail() {
    ConnectedComponents<String> ccs = new ConnectedComponents<>();
    Set<String> cc = new HashSet<>();
    cc.add("C00001@LigandCompound");
    cc.add("h2o@BiGG");
    cc.add("META:WATER@MetaCyc");
    cc.add("cpd00001@ModelSeed");
    ccs.add(cc);
    propagation.addReference("cpd_c", "c",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    propagation.addReference("cpd_b", "e",
                             MetaboliteMajorLabel.ModelSeed, "cpd00001");
    propagation.addReference("cpd_e", "e",
                             MetaboliteMajorLabel.LigandCompound, "C00001");
    
    IntegrationMap<String, MetaboliteMajorLabel> result = 
        propagation.propagate(true, ccs);
    
    assertNotNull(result.get("cpd_c"));
    assertNotNull(result.get("cpd_e"));
    assertEquals(4, result.get("cpd_c").size());
    assertEquals(1, result.get("cpd_e").size());
    assertEquals(1, result.get("cpd_b").size());
  }
}
