package pt.uminho.sysbio.biosynthframework.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.ReactionMath;

public class BasicReactionIntegration<MID, RID> {
  
  private static final Logger logger = LoggerFactory.getLogger(BasicReactionIntegration.class);
  
  private final ConnectedComponents<MID> ccs; 
  public Set<MID> exclude = new HashSet<> ();
  public Map<RID, Map<MID, Double>> reactionStoichs = new HashMap<>();
  public boolean ignoreOrientation = true;
  public Function<Double, Double> stoichFunction = new Function<Double, Double>() {
    
    @Override
    public Double apply(Double t) {
      return t;
    }
  };
  
  public BasicReactionIntegration(ConnectedComponents<MID> ccs) {
    this.ccs = ccs;
  }
  
  public Map<Set<MID>, Double> expand(Map<MID, Double> stoich) {
    Map<Set<MID>, Double> istoich = new HashMap<>();
    for (MID cpdId : stoich.keySet()) {
      if (!exclude.contains(cpdId)) {
        double value = stoichFunction.apply(stoich.get(cpdId));
        Set<MID> cc = ccs.getConnectedComponentOf(cpdId);
        if (cc == null) {
          cc = new HashSet<>();
          cc.add(cpdId);
        }
        
        istoich.put(cc, value);
      }
    }
    
    return istoich;
  }
  
  public Map<Map<Set<MID>, Double>, Set<RID>> integrate() {
    Map<Map<Set<MID>, Double>, Set<RID>> result = new HashMap<>();
    
    for (RID rxnId : reactionStoichs.keySet()) {
      Map<MID, Double> stoich = reactionStoichs.get(rxnId);
      Map<Set<MID>, Double> istoich = expand(stoich);
      Map<Set<MID>, Double> istoichRev = ReactionMath.scale(istoich, -1);
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
