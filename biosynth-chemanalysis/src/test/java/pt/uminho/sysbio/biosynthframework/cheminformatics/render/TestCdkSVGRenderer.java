package pt.uminho.sysbio.biosynthframework.cheminformatics.render;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.cheminformatics.TestData;

public class TestCdkSVGRenderer {

  private CdkSVGRenderer renderer;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    renderer = new CdkSVGRenderer();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
    String mol = TestData.WATER_MOL;
    String svg = renderer.convertMolToSvg(mol);
    assertNotNull(svg);
    assertTrue(svg.contains("class='atom'"));
    assertTrue(svg.contains("class='bond'"));
    System.out.println(svg);
  }

}
