package pt.uminho.sysbio.biosynthframework.report.neo4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalReactionNode;
import pt.uminho.sysbio.biosynthframework.report.neo4j.Neo4jIntegrationStoichMetaboliteMatchReporter.MatchHypothesis;

public class TestNeo4jIntegrationStoichMetaboliteMatchReporter {

  public static BiodbGraphDatabaseService service;
  private Transaction tx = null;
  private static Neo4jIntegrationStoichMetaboliteMatchReporter reporter;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    service = HelperNeo4jConfigInitializer.initializeBiosNeo4jDatabase("/var/biobase/neo4j/bios.db");
    reporter = new Neo4jIntegrationStoichMetaboliteMatchReporter(service);
    reporter.uprotonId = 2997652;
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    tx = service.beginTx();
  }

  @After
  public void tearDown() throws Exception {
    tx.failure();
    tx.close();
  }

  @Test
  public void test1() {
    BiosUniversalReactionNode unode = new BiosUniversalReactionNode(service.getNodeById(3576878), null);
    MatchHypothesis mh = reporter.aaaa(unode);
    fail("Not yet implemented");
  }

  @Test
  public void test2() {
    BiosUniversalReactionNode unode = new BiosUniversalReactionNode(service.getNodeById(3576997), null);
    MatchHypothesis mh = reporter.aaaa(unode);
    fail("Not yet implemented");
  }
}
