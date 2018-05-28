package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;

public class TestRestBiggMetaboliteDaoImpl {

  private RestBiggMetaboliteDaoImpl dao = null;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = new RestBiggMetaboliteDaoImpl("1.5", "/var/biodb/bigg2");
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    Set<String> list = dao.getAllEntries();
    assertNotNull(list);
//    assertEquals("5175 at 10/03/2017", 5175, list.size());
    assertEquals("7339 at 05/23/2018", 7339, list.size());
  }

  @Test
  public void getEntry_pyr() {
    Bigg2MetaboliteEntity cpd = dao.getByEntry("pyr");
    assertNotNull(cpd);
    assertEquals("pyr", cpd.getEntry());
    assertEquals("Pyruvate", cpd.getName());
    assertEquals("C3H3O3", cpd.getFormula());
    System.out.println(cpd.getOldIdentifiers());
  }
  
  @Test
  public void getEntry_pa120() {
    Bigg2MetaboliteEntity cpd = dao.getByEntry("pa120");
    assertNotNull(cpd);
    assertEquals("pa120", cpd.getEntry());
    assertEquals("1,2-didodecanoyl-sn-glycerol 3-phosphate", cpd.getName());
    assertEquals("C27H51O8P1", cpd.getFormula());
    System.out.println(cpd.getOldIdentifiers());
  }
}
