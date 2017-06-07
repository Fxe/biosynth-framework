package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphReactionEntity;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionReagentEntity;

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

  @Test
  public void testEmpty() {
    ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
    GraphReactionEntity grxn = transform.apply(rxn);
    assertNotNull(grxn);
  }

  @Test
  public void testBasic() {
    ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
    rxn.setEntry("rxn99999");
    rxn.setName("rxn name");
    GraphReactionEntity grxn = transform.apply(rxn);
    assertNotNull(grxn);
    assertEquals("rxn99999", grxn.getEntry());
    assertEquals("rxn name", grxn.getName());
  }
  
  @Test
  public void testOnlyLeft() {
    ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
    rxn.setEntry("rxn99999");
    rxn.setName("rxn name");
    List<ModelSeedReactionReagentEntity> r = new ArrayList<> ();
    ModelSeedReactionReagentEntity lhs1 = new ModelSeedReactionReagentEntity();
    lhs1.setCoefficient(-1.1);
    lhs1.setCpdEntry("cpd00000");
    lhs1.setStoichiometry(1.1);
    r.add(lhs1);
    rxn.setReagents(r);
    GraphReactionEntity grxn = transform.apply(rxn);
    System.out.println(grxn);
    assertNotNull(grxn);
    assertEquals("rxn99999", grxn.getEntry());
    assertEquals("rxn name", grxn.getName());
  }
  
  @Test
  public void testBoth() {
    ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
    rxn.setEntry("rxn99999");
    rxn.setName("rxn name");
    List<ModelSeedReactionReagentEntity> r = new ArrayList<> ();
    ModelSeedReactionReagentEntity lhs1 = new ModelSeedReactionReagentEntity();
    lhs1.setCoefficient(-1.1);
    lhs1.setCpdEntry("cpd0000l");
    lhs1.setStoichiometry(1.1);
    ModelSeedReactionReagentEntity rhs1 = new ModelSeedReactionReagentEntity();
    rhs1.setCoefficient(+1.9);
    rhs1.setCpdEntry("cpd0000r");
    rhs1.setStoichiometry(1.9);
    r.add(lhs1);
    r.add(rhs1);
    rxn.setReagents(r);
    GraphReactionEntity grxn = transform.apply(rxn);
    System.out.println(grxn);
    assertNotNull(grxn);
    assertEquals("rxn99999", grxn.getEntry());
    assertEquals("rxn name", grxn.getName());
  }
  
  @Test
  public void testReactionMock1() {
    ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
    rxn.setEntry("rxn99999");
    rxn.setName("rxn name");
    List<ModelSeedReactionReagentEntity> r = new ArrayList<> ();
    ModelSeedReactionReagentEntity lhs1 = new ModelSeedReactionReagentEntity();
    lhs1.setCoefficient(-1.1);
    lhs1.setCpdEntry("cpd0000l");
    lhs1.setStoichiometry(1.1);
    ModelSeedReactionReagentEntity rhs1 = new ModelSeedReactionReagentEntity();
    rhs1.setCoefficient(+1.9);
    rhs1.setCpdEntry("cpd0000r");
    rhs1.setStoichiometry(1.9);
    r.add(lhs1);
    r.add(rhs1);
    rxn.setReagents(r);
    rxn.getEc().add("1.1.1.1");
    rxn.setAbbreviation("TALA");
    rxn.setDeltag(0.11);
    rxn.setEquation("E=MC^2");
    rxn.getNames().add("name1");
    rxn.getNames().add("name2");
    List<ModelSeedReactionCrossreferenceEntity> refs = new ArrayList<> ();
    ModelSeedReactionCrossreferenceEntity ref = new ModelSeedReactionCrossreferenceEntity();
    ref.setModelSeedReactionEntity(rxn);
    ref.setType(ReferenceType.DATABASE);
    ref.setRef("MetaCyc");
    ref.setValue("META:ALCOHOL-DEHYDROGENASE-NADP+-RXN.c.metaexp.CPD-7557_CPD-9101_NADP_NADPH");
    refs.add(ref);
    rxn.setCrossreferences(refs);
    GraphReactionEntity grxn = transform.apply(rxn);
    System.out.println(grxn.getProperties());
    System.out.println(grxn.getConnectedEntities());
    assertNotNull(grxn);
    assertEquals("rxn99999", grxn.getEntry());
    assertEquals("rxn name", grxn.getName());
  }
}
