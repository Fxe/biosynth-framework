package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;

public class TestRestBigg2MetaboliteDaoImpl {

  private RestBigg2MetaboliteDaoImpl dao = null;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = new RestBigg2MetaboliteDaoImpl();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    List<String> list = dao.getAllMetaboliteEntries();
    assertNotNull(list);
    assertEquals("5175 at 10/03/2017", 5175, list.size());
  }

  @Test
  public void getEntry_pyr() {
    Bigg2MetaboliteEntity cpd = dao.getMetaboliteByEntry("pyr");
    assertNotNull(cpd);
    assertEquals("pyr", cpd.getEntry());
    assertEquals("Pyruvate", cpd.getName());
    assertEquals("C3H3O3", cpd.getFormula());
    System.out.println(cpd.getOldIdentifiers());
  }
  
  @Test
  public void getEntry_pa120() {
    Bigg2MetaboliteEntity cpd = dao.getMetaboliteByEntry("pa120");
    assertNotNull(cpd);
    assertEquals("pa120", cpd.getEntry());
    assertEquals("1,2-didodecanoyl-sn-glycerol 3-phosphate", cpd.getName());
    assertEquals("C27H51O8P1", cpd.getFormula());
    System.out.println(cpd.getOldIdentifiers());
  }
}
