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
  public void test() {
//    Map<String, Object> o = (Map<String, Object>) service.getEntry_("P12345.xml");
//    System.out.println(o.get("entry"));
//    Object result = service.getEntry("P12345.xml");
//    System.out.println(result);
    
//    UniprotResult result = service.getGetEntriesByTaxonomy(160488);
    //9986
    UniprotResult result = service.getGetEntriesByTaxonomy(559292L);
    
    for (UniprotEntry e : result.entries) {
      System.out.println(e.name + "\t" + e.accession);
      System.out.println("\t" + e.getLocus());
    }
    System.out.println(result.entries.size());
    
//    System.out.println(result.dataset);
//    System.out.println(result.created);
//    System.out.println(result.modified);
//    System.out.println(result.version);
//    System.out.println(result.copyright.trim());
  }

}
