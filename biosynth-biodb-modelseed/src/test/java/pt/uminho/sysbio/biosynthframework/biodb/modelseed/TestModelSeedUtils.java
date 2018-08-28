package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestModelSeedUtils {

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
  public void test_getIdNumberValue1() {
    int value = ModelSeedUtils.getIdNumberValue("cpd00010");
    assertEquals(10, value);
  }

  @Test
  public void test_getIdNumberValue2() {
    int value = ModelSeedUtils.getIdNumberValue("cpd00001");
    assertEquals(1, value);
  }
  
  @Test
  public void test_getIdNumberValue3() {
    int value = ModelSeedUtils.getIdNumberValue("cpd00000");
    assertEquals(0, value);
  }
  
  @Test(expected=NumberFormatException.class)
  public void test_getIdNumberValue4() {
    int value = ModelSeedUtils.getIdNumberValue("cpd0000A");
    assertEquals(-1, value);
  }
  
  @Test
  public void test_getIdNumberValue5() {
    int value = ModelSeedUtils.getIdNumberValue("CPD00000");
    assertEquals(-1, value);
  }
  
  @Test
  public void test_selectLowestId1() {
    Set<String> ids = new HashSet<>();
    ids.add("cpd10000");
    ids.add("cpd01000");
    ids.add("cpd00100");
    String low = ModelSeedUtils.selectLowestId(ids);
    assertEquals("cpd00100", low);
  }
  
  @Test
  public void test_selectLowestId2() {
    Set<String> ids = new HashSet<>();
    ids.add("cpd10000");
    ids.add("cpd01000");
    ids.add("cpd00100");
    ids.add("cpd00000");
    String low = ModelSeedUtils.selectLowestId(ids);
    assertEquals("cpd00100", low);
  }
  
  @Test
  public void test_selectLowestId3() {
    Set<String> ids = new HashSet<>();
    ids.add("cpd00001");
    ids.add("cpd00000");
    ids.add("CPD00000");
    String low = ModelSeedUtils.selectLowestId(ids);
    assertEquals("cpd00001", low);
  }
  
  @Test
  public void test_selectLowestId4() {
    Set<String> ids = new HashSet<>();
    ids.add("rxn00001");
    ids.add("rxn00000");
    ids.add("rxn00000");
    String low = ModelSeedUtils.selectLowestId(ids);
    assertEquals("rxn00001", low);
  }
}
