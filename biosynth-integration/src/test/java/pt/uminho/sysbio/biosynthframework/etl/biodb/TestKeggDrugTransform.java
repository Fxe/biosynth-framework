package pt.uminho.sysbio.biosynthframework.etl.biodb;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg.KeggDrugTransform;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;

public class TestKeggDrugTransform {

  private KeggDrugTransform transform;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    transform = new KeggDrugTransform();
  }

  @After
  public void tearDown() throws Exception {
  }

  public void evaluateProperties(Map<String, Object> properties) {
    for (String k : properties.keySet()) {
      Object v = properties.get(k);
      if (v instanceof String) {
        assertTrue("property too big: " + k, v.toString().length() < 30000);
      }
    }
  }
  
  @Test
  public void test() {
    RestKeggDrugMetaboliteDaoImpl dao = new RestKeggDrugMetaboliteDaoImpl();
    dao.setLocalStorage("D:/var/biodb/kegg");
    dao.setDatabaseVersion("84.0");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    KeggDrugMetaboliteEntity cpd = dao.getByEntry("D05511");
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
//    System.out.println(gcpd.getConnectedEntities());
    evaluateProperties(gcpd.getProperties());
    for (String t : gcpd.getConnectedEntities().keySet()) {
      System.out.println(t);
      for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : gcpd.getConnectedEntities().get(t)) {
        evaluateProperties(p.getLeft().getProperties());
        evaluateProperties(p.getRight().getProperties());
      }
    }
  }

}
