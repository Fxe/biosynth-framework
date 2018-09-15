package pt.uminho.sysbio.biosynthframework.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;

public class ReactionTMatcher<CMP, ID> {
  
  private static final Logger logger = LoggerFactory.getLogger(ReactionTMatcher.class);
  
  public boolean allowSingle = false;
  public boolean testReverse = true;
  
  public Map<CompartmentalizedStoichiometry<ID, Integer>, Set<ID>> cstoichToRxnIds = new HashMap<>();
  
  public Set<ID> match(CompartmentalizedStoichiometry<ID, ?> cstoich) {
    CompartmentalizedStoichiometry<ID, Integer> ctoich = toAbstractCompartments(cstoich);
    Set<CompartmentalizedStoichiometry<ID, Integer>> perm = getPermutations(ctoich);
    for (Object o : perm) {
      Set<ID> ids = cstoichToRxnIds.get(o);
      if (ids != null) {
        return ids;
      }
    }
    return null;
  }
  
  public CompartmentalizedStoichiometry<ID, Integer> toAbstractCompartments(CompartmentalizedStoichiometry<ID, ?> cstoich) {
    CompartmentalizedStoichiometry<ID, Integer> result = new CompartmentalizedStoichiometry<>();
    
//    Map<BiosModelSpeciesNode, Double> stoich = mrxnNode.getStoichiometryAsNodes(1.0);
    BMap<Object, Integer> cmap = new BHashMap<>();
    int index = 0;
    for (Pair<ID, ?> p : cstoich.stoichiometry.keySet()) {
      Object cmpEntry = p.getRight();
      ID spiEntry = p.getLeft();
      Double value = cstoich.stoichiometry.get(p);
      logger.debug("{} {} {}", cmpEntry, spiEntry, value);
      if (!cmap.keySet().contains(cmpEntry)) {
        cmap.put(cmpEntry, index++);
      }
      if (value < 0.0) {
        result.addLeft(spiEntry, cmap.get(cmpEntry), value);
      } else if (value > 0.0) {
        result.addRight(spiEntry, cmap.get(cmpEntry), value);
      } else {
        logger.warn("error value = 0");
      }
    }
    return result;
  }
  
  public void addReaction(CompartmentalizedStoichiometry<ID, CMP> rxn, ID rxnId) {
    CompartmentalizedStoichiometry<ID, Integer> ctoich = toAbstractCompartments(rxn);
    addReactionInternal(ctoich, rxnId);
  }
  
  public void addReactionInternal(CompartmentalizedStoichiometry<ID, Integer> rxn, ID rxnId) {
    Set<CompartmentalizedStoichiometry<ID, Integer>> perm = getPermutations(rxn);
    boolean found = false;
    for (CompartmentalizedStoichiometry<ID, Integer> o : perm) {
      logger.trace("test  {}", o);
      CompartmentalizedStoichiometry<ID, Integer> rev = null;
      if (testReverse) {
        rev = new CompartmentalizedStoichiometry<>();
        for (Pair<ID, Integer> p : o.stoichiometry.keySet()) {
          double v = o.stoichiometry.get(p);
          rev.stoichiometry.put(p, -1 * v);
        }
      }
      if (cstoichToRxnIds.containsKey(o)) {
        logger.debug("found LR {}", o);
        cstoichToRxnIds.get(o).add(rxnId);
        found = true;
        break;
      } else if (rev != null && cstoichToRxnIds.containsKey(rev)) {
        logger.debug("found RL {}", rev);
        cstoichToRxnIds.get(rev).add(rxnId);
        found = true;
        break;
      }
    }
    if (!found) {
      if (!cstoichToRxnIds.containsKey(rxn)) {
        cstoichToRxnIds.put(rxn, new HashSet<ID>());
      }
      cstoichToRxnIds.get(rxn).add(rxnId);
    }
  }
  
  public Set<CompartmentalizedStoichiometry<ID, Integer>> getPermutations(CompartmentalizedStoichiometry<ID, Integer> cstoich) {
    Set<Integer> cmps = new HashSet<>();
    for (Pair<ID, Integer> p : cstoich.stoichiometry.keySet()) {
      cmps.add(p.getRight());
    }
    
    Set<CompartmentalizedStoichiometry<ID, Integer>> result = new HashSet<>();
    if (cmps.size() == 2) {
      CompartmentalizedStoichiometry<ID, Integer> swap = new CompartmentalizedStoichiometry<>();
      Map<Integer, Integer> cswap = new HashMap<>();
      List<Integer> ocmp = new ArrayList<> (cmps);
      cswap.put(ocmp.get(0), ocmp.get(1));
      cswap.put(ocmp.get(1), ocmp.get(0));
      for (Pair<ID, Integer> p : cstoich.stoichiometry.keySet()) {
        double value = cstoich.stoichiometry.get(p);
        if (value < 0.0) {
          swap.addLeft(p.getLeft(), cswap.get(p.getRight()), value);
        } else {
          swap.addRight(p.getLeft(), cswap.get(p.getRight()), value);
        }
      }
      
      result.add(cstoich);
      result.add(swap);
    } else if (allowSingle && cmps.size() <= 2) {
      result.add(cstoich);
    } else { 
      result.add(cstoich);
      logger.warn("not implemetned for higher than 2");
    }
    
    return result;
  }
}
