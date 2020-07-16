package pt.uminho.sysbio.biosynthframework.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.util.MapUtils;

public class ReactionMatcher<MRXN, RXN, CPD> {
  
  private static final Logger logger = LoggerFactory.getLogger(ReactionMatcher.class);
  
  public Map<MRXN, Map<CPD, Double>> reactions = new HashMap<> ();
  public Map<Map<CPD, Double>, Set<RXN>> dbtoichDictionary = new HashMap<>();
  public Set<CPD> exclude = new HashSet<> ();
  
  public void addDatabaseReaction(RXN rxnId, Map<CPD, Double> stoich) {
    Map<CPD, Double> stoichFilter = new HashMap<>(stoich);
    stoichFilter.keySet().removeAll(exclude);
    
    if (!stoichFilter.isEmpty()) {
      if (!dbtoichDictionary.containsKey(stoich)) {
        dbtoichDictionary.put(stoich, new HashSet<RXN>());
      }
      
      logger.trace("added: {} -> {}", stoich, rxnId);
      
      dbtoichDictionary.get(stoich).add(rxnId);
    } else {
      logger.warn("unable to add empty reaction: {}", rxnId);
    }
  };
  
  public Map<CPD, Double> translate(Map<CPD, Double> stoich, Map<CPD, CPD> t) {
    Map<CPD, Double> translate = new HashMap<> ();
    for (CPD id : stoich.keySet()) {
      double val = stoich.get(id);
      //do swap
      CPD swap = t.get(id);
      if (swap != null) {
        logger.trace("swap {} -> {}", id, swap);
        id = swap;
      }
      if (!exclude.contains(id)) {            
        translate.put(id, val);
      }
    }
    
    return translate;
  }
  
  public Set<RXN> match(Map<CPD, Double> stoich, Map<CPD, CPD> spiTranslation) {
    Set<RXN> match = new HashSet<>();
    Map<CPD, Double> stoichTranslate = translate(stoich, spiTranslation);
    
    logger.trace("{} -> {}", stoich, stoichTranslate);
    
    if (dbtoichDictionary.containsKey(stoichTranslate)) {
      Set<RXN> dbRxns = dbtoichDictionary.get(stoichTranslate);
      if (dbRxns != null) {
        match.addAll(dbRxns);
      }
    } else {
      Map<CPD, Double> stoichTranslateRev = MapUtils.scale(stoichTranslate, -1);
      if (dbtoichDictionary.containsKey(stoichTranslateRev)) {
        Set<RXN> dbRxns = dbtoichDictionary.get(stoichTranslateRev);
        if (dbRxns != null) {
          match.addAll(dbRxns);
        }
      }
    }
    
    return match;
  }
  
  @Deprecated
  public void integrate(ReactionMajorLabel db, Map<CPD, CPD> spiMapping) {
    Map<MRXN, Set<RXN>> mapping = new HashMap<>();
//    MetaboliteMajorLabel cpdDb = rxnToCpdDbMatchMap.get(db);
//    if (dictMap.containsKey(db) && cpdDb != null) {
      
      Map<MRXN, Map<CPD, Double>> rxnEntries = new HashMap<> ();
      Map<Map<CPD, Double>, Set<MRXN>> mstoichDictionary = new HashMap<>();
      
//      Map<String, Map<String, Double>> treactions = new HashMap<> ();
//      Map<String, Map<String, Double>> xreactions = new HashMap<> ();
//      Map<String, String> spiToCmpMap = new HashMap<> ();
//      }
      
      for (MRXN rxnEntry : reactions.keySet()) {
        Map<CPD, Double> stoichOriginal = reactions.get(rxnEntry);
        Map<CPD, Double> stoichTranslate = new HashMap<> ();
        for (CPD id : stoichOriginal.keySet()) {
          double val = stoichOriginal.get(id);
          //do swap
          CPD swap = spiMapping.get(id);
          if (swap != null) {
            logger.trace("swap {} -> {}", id, swap);
            id = swap;
          }
          if (!exclude.contains(id)) {            
            stoichTranslate.put(id, val);
          }
        }
        
        rxnEntries.put(rxnEntry, stoichTranslate);
        if (!mstoichDictionary.containsKey(stoichTranslate)) {
          mstoichDictionary.put(stoichTranslate, new HashSet<MRXN>());
        }
        mstoichDictionary.get(stoichTranslate).add(rxnEntry);
      }
      
//      Map<Map<String, Double>, Set<String>> dbtoichDictionary = dictMap.get(db);
      for (Map<CPD, Double> stoich : mstoichDictionary.keySet()) {
        Set<MRXN> mrxnSet = mstoichDictionary.get(stoich);
        if (dbtoichDictionary.containsKey(stoich)) {
          Set<RXN> dbRxns = dbtoichDictionary.get(stoich);
//          System.out.println("yes! " + dbtoichDictionary.get(s));
          for (MRXN mrxnEntry : mrxnSet) {
//            System.out.println("[F] " + mrxnEntry + " " + dbtoichDictionary.get(stoich));
//            System.out.println(dbRxns + " " + resolver + " " + mrxnEntry );
//            if (dbRxns != null && dbRxns.size() > 1 && resolver != null) {
//              dbRxns = resolver.resolve(mrxnEntry, dbRxns);
//            }
            if (dbRxns != null && dbRxns.size() == 1) {
              RXN rxnId = dbRxns.iterator().next();
              if (mapping.containsKey(mrxnEntry)) {
                mapping.put(mrxnEntry, new HashSet<RXN>());
              }
              mapping.get(mrxnEntry).add(rxnId);
            }
          }
          rxnEntries.keySet().removeAll(mrxnSet);
        } else {
          Map<CPD, Double> rstoich = MapUtils.scale(stoich, -1);
          if (dbtoichDictionary.containsKey(rstoich)) {
            Set<RXN> dbRxns = dbtoichDictionary.get(rstoich);

            for (MRXN mrxnEntry : mrxnSet) {
//              System.out.println("[R] " + mrxnEntry + " " + dbRxns);
//              if (dbRxns != null && dbRxns.size() > 1 && resolver != null) {
//                dbRxns = resolver.resolve(mrxnEntry, dbRxns);
//              }
              if (dbRxns != null && dbRxns.size() == 1) {
                RXN rxnId = dbRxns.iterator().next();
                if (mapping.containsKey(mrxnEntry)) {
                  mapping.put(mrxnEntry, new HashSet<RXN>());
                }
                mapping.get(mrxnEntry).add(rxnId);
              }
            }
            rxnEntries.keySet().removeAll(mrxnSet);
          }
        }
      }
//      for (String o : rxnEntries.keySet()) {
//        System.out.println(o + " " +  rxnEntries.get(o) + " ?");
//      }
//    } else {
//      logger.warn("stoichiometry dictionary for {} not found", db);
//    }
    

//    System.out.println(mstoichDictionary);
  }
}
