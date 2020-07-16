package pt.uminho.sysbio.biosynthframework.io.biodb.ncbi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsAssemblyObject;

public class TestEutilsNcbiAssemblyDaoImpl {

  private EutilsNcbiAssemblyDaoImpl dao;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = new EutilsNcbiAssemblyDaoImpl();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test_live_success1() {
    try {
      Thread.sleep(5000);
      EutilsAssemblyObject res = dao.getByEntry("GCF_000009425.1");
      assertNotNull(res);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void test_live_success2() {
    try {
      Thread.sleep(5000);
      EutilsAssemblyObject res = dao.getByEntry("GCA_001021085.1");
      assertNotNull(res);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}
