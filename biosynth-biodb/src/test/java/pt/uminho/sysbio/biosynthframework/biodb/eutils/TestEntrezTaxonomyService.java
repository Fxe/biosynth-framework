package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEntrezTaxonomyService {

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
  public void test_get_yeast() {
    EntrezTaxonomyService service = new EntrezTaxonomyService();
    EntrezTaxon taxon = service.getTaxonomy(4932);
    System.out.println(taxon);
//    fail("Not yet implemented");
  }
  
  @Test
  public void test_get_sce_hsa_eco() {
    Set<Long> taxIds = new HashSet<> ();
    taxIds.add(4932L);
    taxIds.add(9606L);
    taxIds.add(562L);
    
    EntrezTaxonomyService service = new EntrezTaxonomyService();
    List<EntrezTaxon> taxons = service.getTaxonomy(taxIds);
    System.out.println(taxons);
//    fail("Not yet implemented");
  }
  
  @Test
  public void test_get_sce_eco() {
    Set<Long> taxIds = new HashSet<> ();
    taxIds.add(559292L);
    taxIds.add(511145L);
    
    EntrezTaxonomyService service = new EntrezTaxonomyService();
    List<EntrezTaxon> taxons = service.getTaxonomy(taxIds);
    System.out.println(taxons);
//    fail("Not yet implemented");
  }
}
