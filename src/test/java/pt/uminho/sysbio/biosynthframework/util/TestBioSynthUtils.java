package pt.uminho.sysbio.biosynthframework.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.DefaultReaction;
import pt.uminho.sysbio.biosynthframework.Orientation;

public class TestBioSynthUtils {

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
  public void testHashReaction1() {
    DefaultReaction reaction1 = new DefaultReaction(0L, "rxn0001", "rxn0001",
                                                    new String[] {"A"}, new double[] {},
                                                    new String[] {"B"}, new double[] {},
                                                    Orientation.Reversible);
    DefaultReaction reaction2 = new DefaultReaction(0L, "rxn0001", "rxn0001",
                                                    new String[] {"A"}, new double[] {},
                                                    new String[] {"B"}, new double[] {},
                                                    Orientation.Reversible);
    
    assertEquals(BioSynthUtils.hashReaction(reaction1),
                 BioSynthUtils.hashReaction(reaction2));
  }
  
  @Test
  public void testHashReaction2() {
    DefaultReaction reaction1 = new DefaultReaction(0L, "rxn0001", "rxn0001",
                                                    new String[] {"B"}, new double[] {},
                                                    new String[] {"A"}, new double[] {},
                                                    Orientation.Reversible);
    DefaultReaction reaction2 = new DefaultReaction(0L, "rxn0001", "rxn0001",
                                                    new String[] {"A"}, new double[] {},
                                                    new String[] {"B"}, new double[] {},
                                                    Orientation.Reversible);
    
    assertEquals(BioSynthUtils.hashReaction(reaction1),
                 BioSynthUtils.hashReaction(reaction2));
  }
  
  @Test
  public void testHashReaction3() {
    DefaultReaction reaction1 = 
        new DefaultReaction(0L, "rxn0001", "rxn0001",
                            new String[] {"A"}, new double[] {1.0},
                            new String[] {"B", "C"}, new double[] {2.0, 1.0},
                            Orientation.LeftToRight);
    DefaultReaction reaction2 = 
        new DefaultReaction(0L, "rxn0001", "rxn0001",
                            new String[] {"B", "C"}, new double[] {1.0, 2.0},
                            new String[] {"A"}, new double[] {},
                            Orientation.RightToLeft);
    
    int hash1 = BioSynthUtils.hashReaction(reaction1);
    int hash2 = BioSynthUtils.hashReaction(reaction2);
    assertNotEquals(String.format("Expected distinct hash %d - %d", hash1, hash2),
                    hash1, hash2);
  }

  @Test
  public void testHashReaction4() {
    DefaultReaction reaction1 = 
        new DefaultReaction(0L, "rxn0001", "rxn0001",
                            new String[] {"A"}, new double[] {1.0},
                            new String[] {"B", "C"}, new double[] {1.0, 2.0},
                            Orientation.LeftToRight);
    DefaultReaction reaction2 = 
        new DefaultReaction(0L, "rxn0001", "rxn0001",
                            new String[] {"C", "B"}, new double[] {2.0, 1.0},
                            new String[] {"A"}, new double[] {},
                            Orientation.RightToLeft);
    
    int hash1 = BioSynthUtils.hashReaction(reaction1);
    int hash2 = BioSynthUtils.hashReaction(reaction2);
    assertEquals(String.format("Expected equal hash %d - %d", hash1, hash2),
                 hash1, hash2);
  }
}
