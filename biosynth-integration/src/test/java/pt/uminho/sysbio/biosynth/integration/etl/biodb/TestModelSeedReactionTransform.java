package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionEntity;

@Deprecated
public class TestModelSeedReactionTransform {

  private ModelSeedReactionTransform transform;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    transform = new ModelSeedReactionTransform();
  }

  @After
  public void tearDown() throws Exception {
  }

//  @Test
  public void testEmpty() {
    ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
    GraphReactionEntity grxn = transform.apply(rxn);
    assertNull(grxn);
  }
}
