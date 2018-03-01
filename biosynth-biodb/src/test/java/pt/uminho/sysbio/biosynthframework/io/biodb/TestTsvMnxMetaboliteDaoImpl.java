package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;

public class TestTsvMnxMetaboliteDaoImpl {

  private TsvMnxMetaboliteDaoImpl dao = null;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = null;
  }

  @After
  public void tearDown() throws Exception {
    dao = null;
  }

  @Test
  public void testInitializeFromFolder1_0() {
    dao = new TsvMnxMetaboliteDaoImpl("/var/biodb/mnx/1.0");
    MnxMetaboliteEntity mxnm1 = dao.getMetaboliteByEntry("MNXM1");
    assertNotNull(mxnm1);
    assertEquals("MNXM1", mxnm1.getEntry());
    assertEquals("H(+)", mxnm1.getName());
  }
  
  @Test
  public void testInitializeFromFolder2_0() {
    dao = new TsvMnxMetaboliteDaoImpl("/var/biodb/mnx/2.0");
    MnxMetaboliteEntity mxnm1 = dao.getMetaboliteByEntry("MNXM1");
    assertNotNull(mxnm1);
    assertEquals("MNXM1", mxnm1.getEntry());
    assertEquals("H(+)", mxnm1.getName());
  }
  
  @Test
  public void testInitializeFromFolder3_0() {
    dao = new TsvMnxMetaboliteDaoImpl("/var/biodb/mnx/3.0");
    MnxMetaboliteEntity mxnm1 = dao.getMetaboliteByEntry("MNXM1");
    assertNotNull(mxnm1);
    assertEquals("MNXM1", mxnm1.getEntry());
    assertEquals("H(+)", mxnm1.getName());
  }

  @Test
  public void test3_0() {
    dao = new TsvMnxMetaboliteDaoImpl("/var/biodb/mnx/3.0");
    dao.setBulkAccess(true);
    MnxMetaboliteEntity mxnm1 = dao.getMetaboliteByEntry("MNXM1");
    assertNotNull(mxnm1);
    assertEquals("MNXM1", mxnm1.getEntry());
    assertEquals("H(+)", mxnm1.getName());
    
    Map<String, Set<String>> refData = new HashMap<> ();
    
    for (String entry : dao.getAllMetaboliteEntries()) {
      MnxMetaboliteEntity cpd = dao.getMetaboliteByEntry(entry);
      for (MnxMetaboliteCrossreferenceEntity xref : cpd.getCrossreferences()) {
        String refDb = xref.getRef();
        String refEntry = xref.getValue();
        if (!refData.containsKey(refDb)) {
          refData.put(refDb, new HashSet<String> ());
        }
        refData.get(refDb).add(refEntry.trim());
      }
    }
    
    System.out.println(refData.keySet());
    for (String db : refData.keySet()) {
      System.out.println(db + " " + refData.get(db).size());
    }
  }
}
