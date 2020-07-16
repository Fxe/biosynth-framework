package pt.uminho.sysbio.biosynthframework.report.neo4j;

//import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class TestNeo4jMetabolicModelSpeciesScannerReporter {

  public static BiodbGraphDatabaseService service;
  private Transaction tx = null;
  private static Neo4jMetabolicModelSpeciesScannerReporter reporter;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    service = HelperNeo4jConfigInitializer.initializeBiosNeo4jDatabase("/var/biobase/neo4j/bios.db");
    reporter = new Neo4jMetabolicModelSpeciesScannerReporter(service);
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
  public void test() {
    List<String> models = new ArrayList<>();
    SubcellularCompartment scmp = SubcellularCompartment.CYTOSOL;
    Set<Long> excludeId = new HashSet<>();
    List<ExternalReference> exclude = new ArrayList<>();
    Map<String, Map<String, String>> manualspi = new HashMap<>();
    models.add("iMM904");
    Dataset<String, String, Object> report = reporter.report(models, scmp, exclude, excludeId, manualspi);
    DataUtils.printData(report.dataset);
    assertNotNull(report);
  }

}
