package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDefaultUniprotService {

  private DefaultUniprotService service;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    service = new DefaultUniprotService();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void getProtein() {
    //P77555
    UniprotResult o = service.service.getByEntry("P77555.xml");
    for (UniprotEntry e : o.entries) {
      System.out.println(e.getLocus());
      
    }
  }
//  @Test
  public void test() {
//    Map<String, Object> o = (Map<String, Object>) service.getEntry_("P12345.xml");
//    System.out.println(o.get("entry"));
//    Object result = service.getEntry("P12345.xml");
//    System.out.println(result);
    
//    UniprotResult result = service.getGetEntriesByTaxonomy(160488);
    //9986
    //566546
    //559292L
    //566546L ecoli W
    //1148L -> 1111708L
//    UniprotResult result = service.getGetEntriesByTaxonomy(63612L);
//    UniprotResult result = service.getGetEntriesByTaxonomy(343509L);
    //UP000031874
    //UP000001425 Synechocystis sp. (strain PCC 6803 / Kazusa)
    
    UniprotResult result = service.getGetEntriesByProteome("UP000031874");
    for (UniprotEntry e : result.entries) {
      System.out.println(e.dataset + "\t" + e.name + "\t" + e.accession + "\t" + e.getLocus());
      System.out.println("\t" + e.evidence);
//      System.out.println("\t" + e.sequence.sequence.trim());
      System.out.println("\t" + e.organism.getNCBITaxonomyId());
    }
    System.out.println(result.entries.size());
    
//    System.out.println(result.dataset);
//    System.out.println(result.created);
//    System.out.println(result.modified);
//    System.out.println(result.version);
//    System.out.println(result.copyright.trim());
  }

}
