package pt.uminho.sysbio.biosynthframework.integration;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;
import pt.uminho.sysbio.biosynthframework.integration.model.Neo4jMetabolicModelReactionIntegration;

public class TestReactionTMatcher {

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
    Long remove = 2651689L;
    ReactionTMatcher<Object, Long> matcher = new ReactionTMatcher<>();
    matcher.allowSingle = true;
    matcher.testReverse = true;
    Map<Long, Long> uspiMapTest = new HashMap<>();
    uspiMapTest.put(3152802L, 2651345L);
    uspiMapTest.put(3153090L, 2654215L);
    uspiMapTest.put(3153282L, 2656788L);
    uspiMapTest.put(3152871L, 2651689L);
    uspiMapTest.put(3153085L, 2654214L);
    CompartmentalizedStoichiometry<Long, Object> cstoich = new CompartmentalizedStoichiometry<>();
    cstoich.add(2651689L, 0, -1.0);
    cstoich.add(2651345L, 0, -1.0);
    cstoich.add(2656788L, 0,  1.0);
    cstoich.add(2654214L, 0,  1.0);
    cstoich.add(2654215L, 0, -1.0);

    if (remove != null) {
      cstoich.remove(remove);
    }

    matcher.addReaction(cstoich, 2678764L);


    CompartmentalizedStoichiometry<Long, Long> modelcstoich = new CompartmentalizedStoichiometry<>();
    modelcstoich.add(3153085L, 3151326L, 1.0);
    modelcstoich.add(3152871L, 3151326L, -1.0);
    modelcstoich.add(3152802L, 3151326L, -1.0);
    modelcstoich.add(3153090L, 3151326L, -1.0);
    modelcstoich.add(3153282L, 3151326L, 1.0);

    System.out.println(matcher.cstoichToRxnIds);
    System.out.println(modelcstoich);

    Set<Long> m1 = matcher.match(modelcstoich);
    modelcstoich = Neo4jMetabolicModelReactionIntegration.mapToUniversals(modelcstoich, uspiMapTest);


    if (remove != null) {
      modelcstoich.remove(remove);
    }

    System.out.println("m1 " + m1);

    System.out.println("remap: " + modelcstoich);

    Set<Long> m2 = matcher.match(modelcstoich);

    System.out.println("m2 " + m2);
////  {(3153085,3151326)=1.0, (3152871,3151326)=-1.0, (3152802,3151326)=-1.0, (3153090,3151326)=-1.0, (3153282,3151326)=1.0}
    fail("Not yet implemented");
  }

}
