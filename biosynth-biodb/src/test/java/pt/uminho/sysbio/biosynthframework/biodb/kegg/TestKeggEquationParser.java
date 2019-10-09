package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestKeggEquationParser {

  
  @Test
  public void test_eq1() {
    String eq = "C00002 + C00022 <=> C00008 + C00074";
    KeggEquationParser parser = new KeggEquationParser(eq);
    
    String[][] l = parser.getLeftTriplet();
    String[][] r = parser.getRightTriplet();
    
    assertNotNull(l);
    assertNotNull(r);
    assertEquals(2, l.length);
    assertEquals(2, r.length);
    
    /*
    assertArrayEquals(new String[][] {
      {"C00002", "1.0", "1.0"}, 
      {"C00022", "1.0", "1.0"},
    }, l);
    assertArrayEquals(new String[][] {
      {"C00008", "1.0", "1.0"}, 
      {"C00074", "1.0", "1.0"},
    }, r);
    */
    //assertTrue(Arrays.asList(l).contains(new String[] {"C00002", "1.0", "1.0"}));
    //assertTrue(Arrays.asList(l).contains(new String[] {"C00022", "1.0", "1.0"}));
    //assertTrue(Arrays.asList(r).contains(new String[] {"C00008", "1.0", "1.0"}));
    //assertTrue(Arrays.asList(r).contains(new String[] {"C00074", "1.0", "1.0"}));
  }

  @Test
  public void test_eq2() {
    String eq = "C00013 + C00001 <=> 2 C00009";
    KeggEquationParser parser = new KeggEquationParser(eq);
    
    String[][] l = parser.getLeftTriplet();
    String[][] r = parser.getRightTriplet();
    
    assertNotNull(l);
    assertNotNull(r);
    assertEquals(2, l.length);
    assertEquals(1, r.length);
  }
  
  @Test
  public void test_eq3() {
    String eq = "16 C00002 + 16 C00001 + 8 C00138 <=> 8 C05359 + 16 C00009 + 16 C00008 + 8 C00139";
    KeggEquationParser parser = new KeggEquationParser(eq);
    
    String[][] l = parser.getLeftTriplet();
    String[][] r = parser.getRightTriplet();
    
    assertNotNull(l);
    assertNotNull(r);
    assertEquals(3, l.length);
    assertEquals(4, r.length);
  }
  
  @Test
  public void test_eq4() {
    String eq = "C00404 + n C00001 <=> (n+1) C02174";
    KeggEquationParser parser = new KeggEquationParser(eq);
    
    String[][] l = parser.getLeftTriplet();
    String[][] r = parser.getRightTriplet();
    
    assertNotNull(l);
    assertNotNull(r);
    assertEquals(2, l.length);
    assertEquals(1, r.length);
  }
  
  @Test
  public void test_eq5() {
    String eq = "C04488 + C03576 + 2 C01330(in) <=> C01217 + C03920 + 2 C01330(out)";
    KeggEquationParser parser = new KeggEquationParser(eq);
    
    String[][] l = parser.getLeftTriplet();
    String[][] r = parser.getRightTriplet();
    
    assertNotNull(l);
    assertNotNull(r);
    assertEquals(3, l.length);
    assertEquals(3, r.length);
  }
}
