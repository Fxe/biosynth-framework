package pt.uminho.sysbio.biosynthframework.integration.model;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

public class TestSpecieIntegrationFacade {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    TrieIdBaseIntegrationEngine e1 = new TrieIdBaseIntegrationEngine();
    Set<String> nciDict = new HashSet<> ();
    nciDict.add("spi1");
    nciDict.add("spi2");
    nciDict.add("spi3");
    nciDict.add("spi4");
    
    SearchTable<MetaboliteMajorLabel, String> searchTable = new SearchTable<>();;
    IdBaseIntegrationEngine e2 = new IdBaseIntegrationEngine(searchTable);
    
    e1.setup(MetaboliteMajorLabel.NCI, nciDict);
    SpecieIntegrationFacade facade = new SpecieIntegrationFacade();
    facade.addSpecie("M_spi1_c", "c");
    facade.addSpecie("M_spi1_e", "e");
    facade.addSpecie("M_spi1_k", "k");
    facade.addSpecie("M_spi2_c", "c");
    facade.addSpecie("M_spi2_e", "e");
    facade.addSpecie("M_spi3_c", "c");
    facade.addSpecie("M_spi3_e", "e");
    facade.addSpecie("M_spi3_k", "k");
    facade.addSpecie("M_spi4_k", "k");
    facade.addSpecie("M_spi4_j", "k");
    facade.addSpecie("M_spiA_c", "c");
    e1.ids.addAll(facade.spiToCompartment.keySet());
    
    facade.baseEngines.put("trie", e1);
//    facade.baseEngines.put("idpattern", e2);
    facade.generatePatterns();
    
    e2.patterns = facade.getPatterns();
    
    facade.run();
    Map<String, Map<MetaboliteMajorLabel, String>> imap = facade.build();
    System.out.println(imap);
   
    fail("Not yet implemented");
  }

}