package pt.uminho.sysbio.biosynthframework.report.neo4j;

import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.report.neo4j.Neo4jIntegrationStoichMatchReporter.Report;

public class TestNeo4jIntegrationStoichMatchReporter {

  public static BiodbGraphDatabaseService service;
  private Transaction tx = null;
  private static Neo4jIntegrationStoichMatchReporter reporter;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    service = HelperNeo4jConfigInitializer.initializeBiosNeo4jDatabase("/var/biobase/neo4j/bios.db");
    reporter = new Neo4jIntegrationStoichMatchReporter(service);
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
//    reporter = new Neo4jIntegrationStoichMatchReporter(service);
    Report r = reporter.report("pep", MetaboliteMajorLabel.BiGGMetabolite);
    for (Set<ReactionMajorLabel> databases : r.matchSets.keySet()) {
      System.out.println(databases);
    }
    fail("Not yet implemented");
  }
  
  @Test
  public void test2() {
//    reporter = new Neo4jIntegrationStoichMatchReporter(service);
    reporter.report("pep", MetaboliteMajorLabel.BiGGMetabolite);
    fail("Not yet implemented");
  }
}
