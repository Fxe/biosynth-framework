package pt.uminho.sysbio.biosynth.integration.etl.biodb.kegg;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionLeftEntity;

public class TestKeggReactionTransform {

  @Test
  public void test() {
    
    KeggReactionTransform t = new KeggReactionTransform();
    
    KeggReactionEntity rxn = new KeggReactionEntity();
    List<KeggReactionLeftEntity> left = new ArrayList<>();
    KeggReactionLeftEntity l1 = new KeggReactionLeftEntity();
    l1.setCpdEntry("C00001");
    l1.setStoichiometry(1.0);
    l1.setCoefficient("1.0");
    l1.setModifier("in");
    left.add(l1);
    rxn.setLeft(left);
    t.apply(rxn);
    fail("Not yet implemented");
  }

}
