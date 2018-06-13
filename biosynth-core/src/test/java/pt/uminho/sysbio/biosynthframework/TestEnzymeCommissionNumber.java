package pt.uminho.sysbio.biosynthframework;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEnzymeCommissionNumber {

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
  public void test1() {
    EnzymeCommissionNumber ecn = EnzymeCommissionNumber.parseEnzymeCommissionNumber("1.1.1.1");
    System.out.println(ecn);
    assertNotNull(ecn);
  }

  @Test
  public void test2() {
    EnzymeCommissionNumber ecn = EnzymeCommissionNumber.parseEnzymeCommissionNumber("1.1.1.-");
    System.out.println(ecn);
    assertNotNull(ecn);
  }
  
  @Test
  public void test3() {
    EnzymeCommissionNumber ecn = EnzymeCommissionNumber.parseEnzymeCommissionNumber("1.1.-.-");
    System.out.println(ecn);
    assertNotNull(ecn);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void test4() {
    EnzymeCommissionNumber.parseEnzymeCommissionNumber("1.1.-");
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void test5() {
    EnzymeCommissionNumber.parseEnzymeCommissionNumber("...");
  }
}
