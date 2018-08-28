package pt.uminho.sysbio.biosynthframework.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEquationParser {

  private EquationParser parser = null;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    parser = new EquationParser();
  }

  @After
  public void tearDown() throws Exception {
    parser = null;
  }

  @Test
  public void test() {
    parser.splitTokens.put("->", EquationParser.LEFT_TO_RIGHT);
    String eq = "ACACP + 8 MALACP + 15 NADPH -> 15 NADP + C181ACP + 8  CO2 + 8 ACP";
    parser.parse(eq);
    assertNotNull(parser.leftBasic);
    assertNotNull(parser.rightBasic);
    assertTrue(parser.leftBasic.containsKey("ACACP"));
    assertTrue(parser.leftBasic.containsKey("MALACP"));
    assertTrue(parser.leftBasic.containsKey("NADPH"));
    assertTrue(parser.rightBasic.containsKey("NADP"));
    assertTrue(parser.rightBasic.containsKey("C181ACP"));
    assertTrue(parser.rightBasic.containsKey("CO2"));
    assertTrue(parser.rightBasic.containsKey("ACP"));
  }
  
  @Test
  public void test_eq1() {
    String eq = "H[e] <=>";
    parser.splitTokens.put("<=>", EquationParser.BIDIRECTIONAL);
    parser.parse(eq);
    assertNotNull(parser.leftBasic);
    assertNotNull(parser.rightBasic);
    assertTrue(parser.rightBasic.isEmpty());
  }

}
