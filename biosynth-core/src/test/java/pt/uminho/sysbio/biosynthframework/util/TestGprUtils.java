package pt.uminho.sysbio.biosynthframework.util;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGprUtils {

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

  public static String GPR_LONG_1_AND_OR = "( Rv0131c or Rv0154c or Rv0215c or Rv0231 or Rv0244c or Rv0271c or Rv0400c or Rv0752c or Rv0873 or Rv0972c or Rv0975c or Rv1346 or Rv1467c or Rv1679 or Rv1933c or Rv1934c or Rv2724c or Rv2789c or Rv3061c or Rv3139 or Rv3140 or Rv3274c or Rv3504 or Rv3505 or Rv3543c or Rv3544c or Rv3560c or Rv3562 or Rv3563 or Rv3564 or Rv3573c or Rv3761c or Rv3797 or Rv0672 or Rv2500c ) and ( Rv0222 or Rv0456c or Rv0632c or Rv0673 or Rv0675 or Rv0971c or Rv1070c or Rv1071c or Rv1141c or Rv1142c or Rv1472 or Rv1935c or Rv2486 or Rv2679 or Rv2831 or Rv3039c or Rv3516 or Rv3550 or Rv3774 or Rv0905 or Rv3374 or Rv3373 ) and ( Rv0468 or Rv1715 or Rv1912c or Rv3141 ) and ( Rv0243 or Rv1074c or Rv3546 or Rv3556c or Rv1323 or Rv0859 )";
  
  @Test
  public void test_get_variables_long1() {
    Set<String> genes = new HashSet<>();
    try {
      genes = GprUtils.getVariables(GPR_LONG_1_AND_OR);
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertNotNull(genes);
    assertTrue(!genes.isEmpty());
    assertEquals(genes.size(), 67);
//    fail("Not yet implemented");
  }
  
  @Test
  public void test_preprocess1() {
    String str = GprUtils.preprocess("G1 and G2");
    assertEquals(str, "G1 & G2");
  }
  
  @Test
  public void test_preprocess2() {
    String str = GprUtils.preprocess("(NP1244A ^ NP2584A) v (NP4226A ^ NP2584A)");
    assertEquals(str, "(NP1244A & NP2584A) | (NP4226A & NP2584A)");
  }

//  @Test
  public void test_to_lexicographic_string_long1() {
    for (String b : GPR_LONG_1_AND_OR.split(" and ")) {
      System.out.println(GprUtils.getVariables(b).size());
      System.out.println(b);
    }
    System.out.println(GPR_LONG_1_AND_OR.length());
//    String lexi = GprUtils.toLexicographicString(GPR_LONG_1_AND_OR);
//    assertNotNull(genes);
//    assertTrue(!genes.isEmpty());
//    assertEquals(genes.size(), 67);
//    fail("Not yet implemented");
  }
}
