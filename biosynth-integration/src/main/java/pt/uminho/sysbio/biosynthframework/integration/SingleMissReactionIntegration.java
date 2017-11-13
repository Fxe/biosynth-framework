package pt.uminho.sysbio.biosynthframework.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.ReactionMath;

public class SingleMissReactionIntegration<MID, RID> extends BasicReactionIntegration<MID, RID>{

  private static final Logger logger = LoggerFactory.getLogger(SingleMissReactionIntegration.class);
  
  public Map<Map<Set<MID>, Double>, Set<RID>> singleMissStoich = new HashMap<> ();
  public Map<Map<Set<MID>, Double>, Set<MID>> singleMissValues = new HashMap<> ();
  
  public SingleMissReactionIntegration(ConnectedComponents<MID> ccs) {
    super(ccs);
  }
  
  public Set<Set<MID>> getSingles(Map<Set<MID>, ?> istoich) {
    Set<Set<MID>> result = new HashSet<>();
    for (Set<MID> k : istoich.keySet()) {
      if (k.size() == 1) {
        result.add(k);
      }
    }
    return result;
  }
  
  public void addMissStoich(Map<Set<MID>, Double> istoich, RID rxnId, MID missCpdId) {
    Map<Set<MID>, Double> istoichRev = ReactionMath.scale(istoich, -1);
    if (ignoreOrientation) {
      if (!singleMissStoich.containsKey(istoich) && !singleMissStoich.containsKey(istoichRev)) {
        singleMissStoich.put(istoich, new HashSet<RID>());
        singleMissValues.put(istoich, new HashSet<MID>());
      }
      
      if (singleMissStoich.containsKey(istoich)) {
        singleMissStoich.get(istoich).add(rxnId);
        singleMissValues.get(istoich).add(missCpdId);
      } else if (singleMissStoich.containsKey(istoichRev)) {
        singleMissStoich.get(istoichRev).add(rxnId);
        singleMissValues.get(istoichRev).add(missCpdId);
      } else {
        logger.error("error!");
      }
    } else {
      if (!singleMissStoich.containsKey(istoich)) {
        singleMissStoich.put(istoich, new HashSet<RID>());
        singleMissValues.put(istoich, new HashSet<MID>());
      }
      singleMissStoich.get(istoich).add(rxnId);
      singleMissValues.get(istoich).add(missCpdId);
    }
  }
  
  @Override
  public Map<Map<Set<MID>, Double>, Set<RID>> integrate() {
    Map<Map<Set<MID>, Double>, Set<RID>> result = new HashMap<>();
    
    for (RID rxnId : reactionStoichs.keySet()) {
      Map<MID, Double> stoich = reactionStoichs.get(rxnId);
      Map<Set<MID>, Double> istoich = expand(stoich);
      Map<Set<MID>, Double> istoichRev = ReactionMath.scale(istoich, -1);
      
      Set<Set<MID>> singles = getSingles(istoich);
      if (singles.size() == 1) {
        Map<Set<MID>, Double> istoichMiss = new HashMap<>(istoich);
        istoichMiss.keySet().removeAll(singles);
        System.out.println(istoich + " -> " + singles);
        System.out.println(istoichMiss);
        addMissStoich(istoichMiss, rxnId, singles.iterator().next().iterator().next());
      }
      
      if (ignoreOrientation) {
        if (!result.containsKey(istoich) && !result.containsKey(istoichRev)) {
          result.put(istoich, new HashSet<RID>());
        }
        if (result.containsKey(istoich)) {
          result.get(istoich).add(rxnId);
        } else if (result.containsKey(istoichRev)) {
          result.get(istoichRev).add(rxnId);
        } else {
          logger.error("error!");
        }
      } else {
        if (!result.containsKey(istoich)) {
          result.put(istoich, new HashSet<RID>());
        }
        result.get(istoich).add(rxnId);
      }

    }
    return result;
  }
}
