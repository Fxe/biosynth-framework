package pt.uminho.sysbio.biosynthframework.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEquationParser {

  private EquationParser parser = new EquationParser();
  
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

}
