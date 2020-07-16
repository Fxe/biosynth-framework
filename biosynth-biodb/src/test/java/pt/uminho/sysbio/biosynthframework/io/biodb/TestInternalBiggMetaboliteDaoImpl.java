package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestInternalBiggMetaboliteDaoImpl {

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
    InternalBigg1MetaboliteDaoImpl dao = new InternalBigg1MetaboliteDaoImpl();
    for (String a : dao.getAllMetaboliteEntries()) {
      System.out.println(a);
    }
    fail("Not yet implemented");
  }

}
