package pt.uminho.sysbio.biosynthframework.io.biodb.ncbi;

import static org.junit.Assert.fail;

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
  public void test() {
    EutilsAssemblyObject res = dao.getByEntry("GCF_000009425.1");
    
    fail("Not yet implemented");
  }

}
