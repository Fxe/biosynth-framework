package pt.uminho.sysbio.biosynthframework.io.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionEntity;

public class TestRestBigg2ReactionDaoImpl {
  
  private RestBigg2ReactionDaoImpl dao = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = new RestBigg2ReactionDaoImpl();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testListReactions() {
    Set<String> list = dao.getAllReactionEntries();
    
    assertNotNull(list);
    assertEquals("15288 at 10/03/2017", 15288, list.size());
  }
  
  @Test
  public void getEntry_PYK() {
    Bigg2ReactionEntity rxn = dao.getReactionByEntry("PYK");
    
    assertNotNull(rxn);
    assertEquals("PYK", rxn.getEntry());
    assertEquals("Pyruvate kinase", rxn.getName());
    assertEquals("adp_c +  h_c +  pep_c  &#8652;  atp_c +  pyr_c", rxn.getReactionString());
    assertEquals(false, rxn.getPseudoreaction());
    assertNotNull(rxn.getModels());
    assertEquals(79, rxn.getModels().size());
//    System.out.println(rxn.getMetabolites());
//    System.out.println(rxn.getCrossreferences());
  }

}
