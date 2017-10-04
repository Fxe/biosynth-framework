package pt.uminho.sysbio.biosynthframework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.BFunction;
import pt.uminho.sysbio.biosynthframework.integration.model.IntegrationMap;

public class IntegrationMapUtils {
  
  public static Function<List<Set<String>>, List<Set<String>>> reduceBigg = null;
  
  private static void addIntegration(
      String spiEntry, 
      Map<MetaboliteMajorLabel, Set<String>> i, 
      int index, 
      int max,
      Map<String, Map<MetaboliteMajorLabel, List<Set<String>>>> data) {
    
    if (!data.containsKey(spiEntry)) {
      data.put(spiEntry, new HashMap<MetaboliteMajorLabel, List<Set<String>>> ());
    }
    for (MetaboliteMajorLabel db : i.keySet()) {
      if (!data.get(spiEntry).containsKey(db)) {
        data.get(spiEntry).put(db, new ArrayList<Set<String>> ());
        for (int j = 0; j < max; j++) {
          data.get(spiEntry).get(db).add(new HashSet<String> ());
        }
      }
      data.get(spiEntry).get(db).get(index).addAll(i.get(db));
    }
  }
  
  public static Map<String, Map<MetaboliteMajorLabel, List<Set<String>>>> merge(
      List<IntegrationMap<String, MetaboliteMajorLabel>> integration) {
    
    Map<String, Map<MetaboliteMajorLabel, List<Set<String>>>> result = new HashMap<> ();
    int size = integration.size();
    Set<String> keys = new HashSet<> ();
    for (Map<String, Map<MetaboliteMajorLabel, Set<String>>> itg : integration) {
      keys.addAll(itg.keySet());
    }
    
    for (String k : keys) {
      for (int index = 0; index < size; index++) {
        Map<MetaboliteMajorLabel, Set<String>> i = integration.get(index).get(k);
        if (i != null) {
          addIntegration(k, i, index, size, result);
        }
      }
    }
    
    return result;
  }
  
  public static Set<String> bestMatch(List<Set<String>> matches) {
    Set<String> best = new HashSet<> ();
    Map<String, Double> scores = new HashMap<> ();
    for (Set<String> m : matches) {
      for (String s : m) {
        CollectionUtils.increaseCount(scores, s, 1.0);
      }
    }
    
    Double h = 0.0;
    for (Double v : scores.values()) {
      if (v > h) {
        h = v;
      }
    }
    for (String id : scores.keySet()) {
      if (scores.get(id) >= h) {
        best.add(id);
      }
    }
    return best;
  }
  
  public static Set<String> bigg2Match(List<Set<String>> matches) {
    matches = reduceBigg.apply(matches);
    Set<String> best = new HashSet<> ();
    Map<String, Double> scores = new HashMap<> ();
    for (Set<String> m : matches) {
      for (String s : m) {
        CollectionUtils.increaseCount(scores, s, 1.0);
      }
    }
    Double h = 0.0;
    for (Double v : scores.values()) {
      if (v > h) {
        h = v;
      }
    }
    for (String id : scores.keySet()) {
      if (scores.get(id) >= h) {
        best.add(id);
      }
    }
    return best;
  }
  
  public static Map<String, Map<MetaboliteMajorLabel, String>> consensus(
      Map<String, Map<MetaboliteMajorLabel, List<Set<String>>>> integrations) {
    Map<String, Map<MetaboliteMajorLabel, String>> consensus = new HashMap<> ();
    
//    Transaction dataTx = graphDataService.beginTx();
    
    for (String id : integrations.keySet()) {
      consensus.put(id, new HashMap<MetaboliteMajorLabel, String> ());
      Map<MetaboliteMajorLabel, List<Set<String>>> sets = integrations.get(id);
      for (MetaboliteMajorLabel database : sets.keySet()) {
        List<Set<String>> matches = sets.get(database);
        Set<String> best = null;
        switch (database) {
          case BiGG2:
            if (reduceBigg == null) {
              best = bestMatch(matches);
            } else {
              best = bigg2Match(matches);
            }
            break;
          default:
            best = bestMatch(matches);
            break;
        }
        if (best.size() == 1) {
          consensus.get(id).put(database, best.iterator().next());
        }
      }
    }
    
//    dataTx.failure();
//    dataTx.close();
    
    return consensus;
  }
  
  public static Map<String, Map<MetaboliteMajorLabel, String>> consensus2(
      Map<String, Map<MetaboliteMajorLabel, List<Set<String>>>> integrations,
      Map<MetaboliteMajorLabel, BFunction<List<Set<String>>, List<Set<String>>>> matchResolver) {
    Map<String, Map<MetaboliteMajorLabel, String>> consensus = new HashMap<> ();
    
//    Transaction dataTx = graphDataService.beginTx();
    
    for (String id : integrations.keySet()) {
      consensus.put(id, new HashMap<MetaboliteMajorLabel, String> ());
      Map<MetaboliteMajorLabel, List<Set<String>>> sets = integrations.get(id);
      for (MetaboliteMajorLabel database : sets.keySet()) {
        List<Set<String>> matches = sets.get(database);
        BFunction<List<Set<String>>, List<Set<String>>> f = null;
        if (matchResolver != null) {
          f = matchResolver.get(database);
        }
        if (f != null) {
          matches = f.apply(matches);
        }
        Set<String> best = bestMatch(matches);
        if (best.size() == 1) {
          consensus.get(id).put(database, best.iterator().next());
        } else {
//          System.out.println("discarded " + id + " " + best);
        }
      }
    }
    
//    dataTx.failure();
//    dataTx.close();
    
    return consensus;
  }
}
