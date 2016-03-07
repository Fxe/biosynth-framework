package pt.uminho.sysbio.biosynth.integration.report;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

public class TestUnifiedDatabaseReporter {

  private static GraphDatabaseService db;
  private static Transaction tx;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    db = new TestGraphDatabaseFactory().newImpermanentDatabase();
    tx = db.beginTx();
  }

  @After
  public void tearDown() throws Exception {
    tx.success(); tx.close();
    db.shutdown();
  }

  @Test
  public void test() {
    UnifiedDatabaseReporter reporter = 
        new UnifiedDatabaseReporter(db);
    reporter.generateReport();
    assertEquals(true, true);
  }

}
