package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestEntrezTaxonomyService {

  private static final Logger logger = LoggerFactory.getLogger(TestEntrezTaxonomyService.class);
  
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
  public void test_search_yeast() {
    EntrezTaxonomyService service = new EntrezTaxonomyService();
    EntrezSearchResult taxon = service.searchGenes(326442L, 2, 0);
    logger.debug("!");
    System.out.println(taxon.Count);
    System.out.println(taxon.RetMax);
    System.out.println(taxon.RetStart);
    System.out.println(taxon.QueryTranslation);
    System.out.println(taxon.IdList);
    for (long  id :taxon.IdList) {
      Object o =service.fetch(EntrezDatabase.gene, id);
      System.out.println("\t" + o);
    }
    for (EntrezGene o : service.getGenes(taxon.IdList)) {
      System.out.println("\t" + o.Entrezgene_track_info);
      System.out.println("\t" + o.Entrezgene_gene);
      System.out.println("\t" + o.Entrezgene_gene_source);
      System.out.println("\t" + o.Entrezgene_locus);
      System.out.println("\t" + o.Entrezgene_prot);
      System.out.println("\t" + o.Entrezgene_source);
      System.out.println("---------");
    }
    
    
//    fail("Not yet implemented");
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
