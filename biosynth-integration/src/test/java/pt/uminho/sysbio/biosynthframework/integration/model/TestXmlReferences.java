package pt.uminho.sysbio.biosynthframework.integration.model;

import static org.junit.Assert.fail;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestXmlReferences {

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
  public void test() {
    XmlReferencesBaseIntegrationEngine engine = new XmlReferencesBaseIntegrationEngine(null);
    Pair<?, ?> p = engine.extractRdfAnnotation("urn:miriam:kegg.compound:C02737");
    System.out.println(p);
    fail("Not yet implemented");
  }

}
