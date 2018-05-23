package pt.uminho.sysbio.biosynthframework.neo4j.report;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import pt.uminho.sysbio.biosynthframework.test.TestConfiguration;

public class TestSupplementaryFileReporter {

  private static GraphDatabaseService graphDatabaseService;
  private Transaction tx = null;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    graphDatabaseService = TestConfiguration.getTestGraphDatabaseService();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    graphDatabaseService.shutdown();
  }

  @Before
  public void setUp() throws Exception {
    tx = graphDatabaseService.beginTx();
  }

  @After
  public void tearDown() throws Exception {
    tx.failure();
    tx.close();
  }

  @Test
  public void test() {
    TotalLabelSetReporter reporter = new TotalLabelSetReporter(graphDatabaseService);
    System.out.println(reporter.report());
    
    fail("Not yet implemented");
  }

  @Test
  public void test1() {
    SupplementaryFileReporter reporter = new SupplementaryFileReporter(graphDatabaseService);
    System.out.println(reporter.report());
    
    fail("Not yet implemented");
  }
  
  @Test
  public void test2() {
    SupplementaryFileReporter reporter = new SupplementaryFileReporter(graphDatabaseService);
    System.out.println(reporter.reportJournalCount());
    
    fail("Not yet implemented");
  }
}
