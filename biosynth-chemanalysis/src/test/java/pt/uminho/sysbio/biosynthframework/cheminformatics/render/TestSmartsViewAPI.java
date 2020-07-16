package pt.uminho.sysbio.biosynthframework.cheminformatics.render;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.cheminformatics.ReactionSMARTS;
import pt.uminho.sysbio.biosynthframework.cheminformatics.SMARTS;

public class TestSmartsViewAPI {

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
    ReactionSMARTS rsmarts = new ReactionSMARTS("[O:7]=[#6:3]-1-[#6;h1:4]=[#6:5]-[#6:6]=[#6:1]-[#6:2]-1=[O:8].[#6:10]-[#7H2,#16H1:9]>>[#6:10]-[*:9]-[#6:4]-1=[#6:5]-[#6:6]=[#6:1]-[#6:2](-[#8:8])=[#6:3]-1-[#8:7]");
    System.out.println(rsmarts);
    SmartsViewAPI api = new SmartsViewAPI();
    api.toSvg(rsmarts.l.get(0));
    fail("Not yet implemented");
  }

}
