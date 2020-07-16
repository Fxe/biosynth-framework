package pt.uminho.sysbio.biosynthframework.report.neo4j;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalReactionNode;
import pt.uminho.sysbio.biosynthframework.report.neo4j.Neo4jIntegrationStoichMetaboliteMatchReporter.MatchHypothesis;

public class TestNeo4jIntegrationStoichMetaboliteMatchReporter {

  public static BiodbGraphDatabaseService service;
  private Transaction tx = null;
  private static Neo4jIntegrationStoichMetaboliteMatchReporter reporter;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
//    service = HelperNeo4jConfigInitializer.initializeBiosNeo4jDatabase("/var/biobase/neo4j/bios.db");
    reporter = new Neo4jIntegrationStoichMetaboliteMatchReporter(service);
    reporter.uprotonId = 2997652;
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
//    tx = service.beginTx();
  }

  @After
  public void tearDown() throws Exception {
//    tx.failure();
//    tx.close();
  }

//  @Test
//  public void test1() {
//    BiosUniversalReactionNode unode = new BiosUniversalReactionNode(service.getNodeById(3576878), null);
//    MatchHypothesis mh = reporter.aaaa(unode);
//    fail("Not yet implemented");
//  }
//
//  @Test
//  public void test2() {
//    BiosUniversalReactionNode unode = new BiosUniversalReactionNode(service.getNodeById(3576997), null);
//    MatchHypothesis mh = reporter.aaaa(unode);
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void test3() {
//    BiosUniversalReactionNode unode = new BiosUniversalReactionNode(service.getNodeById(3578850), null);
//    MatchHypothesis mh = reporter.aaaa(unode);
//    fail("Not yet implemented");
//  }
//  
//  @Test
//  public void test4() {
//    BiosUniversalReactionNode unode = new BiosUniversalReactionNode(service.getNodeById(3580591), null);
//    MatchHypothesis mh = reporter.aaaa(unode);
//    fail("Not yet implemented");
//  }

  
  @Test
  public void testA() {
    ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Neo4jIntegrationStoichMetaboliteMatchReporter.class)).setLevel(ch.qos.logback.classic.Level.DEBUG);
    //2760139={2706864=1.0, 2717874=-1.0, 2705858=-1.0, 2706978=1.0, 2707189=1.0, 2706788=-1.0, 2705220=1.0}, 

    //2670027={2647352=1.0, 2651689=1.0, 2652138=-1.0, 2652174=1.0, 2647666=-1.0, 2656242=1.0, 2657734=-1.0}, 2660012={2647352=1.0, 2652138=-1.0, 2652174=1.0, 2647666=-1.0, 2656242=1.0, 2657734=-1.0}, 2636780={2626137=-1.0, 2626173=-1.0, 2625438=1.0, 2625475=1.0, 2629764=1.0, 2625399=-1.0}, 2688863={2647352=1.0, 2652138=-1.0, 2652174=1.0, 2647666=-1.0, 2656242=1.0, 2657734=-1.0}, 2614042={1947868=-1.0, 1946428=1.0, 1948856=1.0, 1946379=-1.0, 1946597=-1.0, 1946466=1.0}, 2638037={2626137=-1.0, 2626173=-1.0, 2625438=1.0, 2625475=1.0, 2629764=1.0, 2625399=-1.0}}
//    BiosUniversalReactionNode unode = new BiosUniversalReactionNode(service.getNodeById(3578850), null);
    Map<Long, Map<Long, Double>> rxnMap = new HashMap<>();
    Map<Long, Double> m2667560 = new HashMap<>();
    m2667560.put(2647352L, 1.0);
    m2667560.put(2652138L, -1.0);
    m2667560.put(2652174L, 1.0);
    m2667560.put(2647666L, -1.0);
    m2667560.put(2656242L, 1.0);
    m2667560.put(2657734L, -1.0);
    rxnMap.put(2667560L, m2667560);
    Map<Long, Double> m2760139 = new HashMap<>();
    m2760139.put(2706864L, 1.0);
    m2760139.put(2717874L, -1.0);
    m2760139.put(2706978L, 1.0);
    m2760139.put(2705858L, -1.0);
    m2760139.put(2707189L, 1.0);
    m2760139.put(2706788L, -1.0);
    m2760139.put(2705220L, 1.0);
    rxnMap.put(2760139L, m2667560);
    //2936879={2980366=1.0, 2985866=1.0, 2985861=-1.0, 2898438=-1.0, 2898439=1.0, 2980448=-1.0},
    Map<Long, Double> m2936879 = new HashMap<>();
    m2936879.put(2980366L, 1.0);
    m2936879.put(2985866L, 1.0);
    m2936879.put(2898439L, 1.0);
    m2936879.put(2980448L, -1.0);
    m2936879.put(2985861L, -1.0);
    m2936879.put(2898438L, -1.0);
    rxnMap.put(2936879L, m2936879);
    BMap<Long, Long> umap = new BHashMap<>();
    umap.put(2625768L, 2997652L);
    umap.put(2651689L, 2997652L);
    umap.put(2652138L, 3576401L);
    umap.put(2706978L, 2997683L);
    umap.put(2980448L, 3576401L);
    umap.put(2706788L, 2997696L);
    umap.put(2647352L, 3576246L);
    umap.put(2626173L, 3576401L);
    umap.put(1946379L, 2997696L);
    umap.put(2706864L, 3576246L);
    umap.put(2647666L, 2997696L);
    umap.put(2656242L, 2997683L);
    umap.put(2707189L, 2997652L);
    umap.put(2625399L, 2997696L);
    umap.put(1946428L, 2997683L);
    umap.put(2980366L, 2997683L);
    umap.put(2652174L, 3576400L);
    umap.put(2985866L, 3576246L);
    umap.put(2985861L, 2997696L);
    umap.put(2983045L, 2997652L);
    umap.put(2625475L, 3576246L);
    umap.put(2705858L, 3576401L);
    umap.put(2629764L, 3576400L);
    umap.put(2657734L, 3576655L);
    umap.put(2626137L, 3576655L);
    umap.put(2625438L, 2997683L);
    umap.put(1946538L, 2997652L);
    umap.put(1946597L, 3576401L);
    umap.put(1946466L, 3576246L);
    MatchHypothesis mh = reporter.match(0, rxnMap, umap);
    fail("Not yet implemented");
  }
  
  @Test
  public void testB() {
    ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Neo4jIntegrationStoichMetaboliteMatchReporter.class)).setLevel(ch.qos.logback.classic.Level.DEBUG);
    Map<Long, Map<Long, Double>> rxnMap = new HashMap<>();
    Map<Long, Double> rxn1 = new HashMap<>();
    rxn1.put(11L,  1.0); //10
    rxn1.put(12L,  1.0);
    rxn1.put(13L, -1.0);
    rxn1.put(14L, -1.0);    
    rxnMap.put(-1L, rxn1);
    Map<Long, Double> rxn2 = new HashMap<>();
    rxn2.put(21L,  1.0); //10
    rxn2.put(22L,  1.0);
    rxn2.put(23L, -1.0);
    rxn2.put(24L, -1.0);
    rxnMap.put(-2L, rxn2);
    Map<Long, Double> rxn3 = new HashMap<>();
    rxn3.put(31L,  1.0); //10
    rxn3.put(32L,  1.0);
    rxn3.put(33L, -1.0);
    rxn3.put(34L, -1.0);
    rxnMap.put(-2L, rxn3);
    BMap<Long, Long> umap = new BHashMap<>();
    umap.put(11L, 10L);
    umap.put(21L, 10L);
    umap.put(31L, 10L);
    umap.put(13L, 30L);
    umap.put(23L, 30L);
    umap.put(33L, 30L);
    umap.put(32L, 20L);
    MatchHypothesis mh = reporter.match(0, rxnMap, umap);
    fail("Not yet implemented");
  }
}
