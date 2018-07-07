package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalReactionNode;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class Neo4jIntegrationStoichMetaboliteMatchReporter extends AbstractNeo4jReporter {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jIntegrationStoichMetaboliteMatchReporter.class);
  
//  private Map<Long, Long> idUniMap = null;
//  private Map<Long, Set<Long>> uidToIdsMap = null;
  public long uprotonId = -1;
  
  public Neo4jIntegrationStoichMetaboliteMatchReporter(GraphDatabaseService service) {
    super(service);
  }

//  public void rebuildData() {
//    logger.info("rebuild indexes");
//    idUniMap = new HashMap<>();
//    uidToIdsMap = new HashMap<>();
//    for (Node n : service.listNodes(CurationLabel.UniversalMetabolite)) {
//      BiosUniversalMetaboliteNode unode = new BiosUniversalMetaboliteNode(n, null);
//      Set<Long> ids = new HashSet<>();
//      for (BiodbMetaboliteNode cpdNode : unode.getMetabolites()) {
//       ids.add(cpdNode.getId());
//       idUniMap.put(cpdNode.getId(), unode.getId());
//       CollectionUtils.insertHS(unode.getId(), cpdNode.getId(), uidToIdsMap);
//      }
//    }
//  }
  
  public static class MatchHypothesis {
    public long usetId;
    public int free;
    public Set<List<Long>> hypothesis = new HashSet<>();
    public Map<Long, Map<Long, Long>> matchMatrix = new HashMap<>();
    public Map<Long, Map<Long, Set<Long>>> matchMatrixMaybe = new HashMap<>();
    public Map<Long, Set<Long>> freeIds = new HashMap<>();
  }
  
  public static class Report {
    public Map<Long, MatchHypothesis> reports = new HashMap<>();
  }
  
  public<T> Map<T, Character> getSlotTypes(Dataset<?, T, Character> groupSet) {
    Map<T, Character> map = new HashMap<>();
    Map<T, Set<Character>> match = new HashMap<>();
    for (Object rxnId : groupSet.dataset.keySet()) {
      for (T uid : groupSet.dataset.get(rxnId).keySet()) {
        CollectionUtils.insertHS(uid, groupSet.dataset.get(rxnId).get(uid), match);
      }
    }
    for (T uid : match.keySet()) {
      Set<Character> m = match.get(uid);
      m.remove(null);
      if (m.size() == 1) {
        map.put(uid, m.iterator().next());
      }
    }
    return map;
  }
  
  public Character getSlotType(Set<Long> uids, Dataset<Long, Long, Character> groupSet) {
    logger.debug("uids: {}", uids);
    Set<Character> group = new HashSet<>();
    for (long uid : uids) {
      for (long rxnId : groupSet.dataset.keySet()) {
        Map<Long, Character> map = groupSet.dataset.get(rxnId);
//        System.out.println(uid + " " + rxnId + " " + map.get(uid));
        group.add(map.get(uid));
      }
    }
    group.remove(null);
    if (group.size() != 1) {
      logger.debug("unable to decide group: {}", group);
      return null;
    }
    
    return group.iterator().next();
  }
  
  public MatchHypothesis match(long urxnId, Map<Long, Map<Long, Double>> rxnMap, BMap<Long, Long> umap) {
    Dataset<Long, Long, Long> matchSet = new Dataset<>();
    Dataset<Long, Long, Character> groupSet = new Dataset<>();
    Dataset<Long, Long, Set<Long>> matchSetMaybe = new Dataset<>();
    Map<Long, Set<Long>> matchSetMaybeFree = new HashMap<>();
    Set<List<Long>> hypothesis = new HashSet<>();
    Map<Long, Set<Long>> missing = new HashMap<>();
    Set<Long> uids = new HashSet<>();
    logger.debug("build mapped compounds");
    
    int min = Integer.MAX_VALUE;
    for (long rxnId : rxnMap.keySet()) {
      missing.put(rxnId, new HashSet<Long>());
      matchSet.dataset.put(rxnId, new HashMap<Long, Long>());
      matchSetMaybe.dataset.put(rxnId, new HashMap<Long, Set<Long>>());
      matchSetMaybeFree.put(rxnId, new HashSet<Long>());
      int misses = 0;
      Map<Long, Double> stoich = rxnMap.get(rxnId);
      for (long cpdId : stoich.keySet()) {
        Double v = stoich.get(cpdId);
        char group = '*';
        if (v != null && v > 0) {
          group = '+';
        } else if (v != null && v < 0) {
          group = '-';
        }
        Long ucpdId = umap.get(cpdId);
        if (ucpdId != null) {
          uids.add(ucpdId);
          matchSet.add(rxnId, ucpdId, cpdId);
          groupSet.add(rxnId, ucpdId, group);
        } else {
          misses++;
        }
      }
      if (misses < min) {
        min = misses;
      }
      logger.debug("Misses: {} -> {}", rxnId, misses);
    }

//    DataUtils.printData(groupSet.dataset, "rxn", "-", "+", "*");
    Map<Long, Character> uidSlots = getSlotTypes(groupSet);
    logger.debug("Free slots: {}", min);
    
    logger.debug("match unmapped compounds");
    for (long rxnId : rxnMap.keySet()) {
      for (long cpdId : rxnMap.get(rxnId).keySet()) {
//        BiodbMetaboliteNode cpd = service.getMetabolite(cpdId);
//        BiosUniversalMetaboliteNode ucpd = cpd.getUniversalMetabolite();
        Long ucpdId = umap.get(cpdId);
        if (ucpdId == null) {
          logger.debug("Reaction[{}] with unmapped compound {}", rxnId, cpdId);
          Set<Long> stoichGroupMembers = getMembers(cpdId, rxnMap.get(rxnId));
          Set<Long> memberUids = uids(stoichGroupMembers, umap);
          Character group = getSlotType(memberUids, groupSet);
          logger.debug("Reaction[{}] with unmapped compound {} -> {} >> {} @ {}", rxnId, cpdId, stoichGroupMembers, memberUids, group);
          Map<Long, Long> mapping = matchSet.dataset.get(rxnId);
          boolean found = false;
          for (long i : uids) {
            if (i != uprotonId) {
              Character iSlotType = uidSlots.get(i);
              if (iSlotType != null && group != null) {
                if (iSlotType.equals(group)) {
                  if (mapping.get(i) == null) {
                    logger.debug("maybe {} @ {} -> {} @ {}", cpdId, group, i, iSlotType);
                    Set<Long> cpdIds = umap.bget(i);
                    List<Long> l = new ArrayList<>(cpdIds);
                    l.add(cpdId);
                    hypothesis.add(l);
                    CollectionUtils.insertHS(i, cpdId, matchSetMaybe.dataset.get(rxnId));
                    //              matchSetFake.add(e.getEntry(), i, "??" + cpd.getEntry() + " " + cpd.getProperty("name", "") + "??");
                    found = true;
                  }
                }
              } else {
                if (mapping.get(i) == null) {
                  logger.debug("maybe {} @ {} -> {} @ {}", cpdId, group, i, iSlotType);
                  Set<Long> cpdIds = umap.bget(i);
                  List<Long> l = new ArrayList<>(cpdIds);
                  l.add(cpdId);
                  hypothesis.add(l);
                  CollectionUtils.insertHS(i, cpdId, matchSetMaybe.dataset.get(rxnId));
                  //              matchSetFake.add(e.getEntry(), i, "??" + cpd.getEntry() + " " + cpd.getProperty("name", "") + "??");
                  found = true;
                }
              }
            }
          }
          if (!found) {
            logger.debug("Reaction[{}] with free compound {}", rxnId, cpdId);
            matchSetMaybeFree.get(rxnId).add(cpdId);
            missing.get(rxnId).add(cpdId);
          }
        }
      }
    }
    
    List<Set<Long>> l = new ArrayList<>();
    for (Set<Long> s : matchSetMaybeFree.values()) {
      l.add(s);
    }
    
    Set<List<Long>> combinations = Sets.cartesianProduct(l);
    for (List<Long> c : combinations) {
      hypothesis.add(c);
    }
    
//    System.out.println(matchSetMaybeFree);
//    System.out.println(matchSet.dataset);
//    System.out.println(matchSetMaybe.dataset);
//    DataUtils.printData(matchSet.dataset, "rxn");
//    DataUtils.printData(matchSetMaybe.dataset, "rxn");
    
    MatchHypothesis result = new MatchHypothesis();
    result.usetId = urxnId;
    result.hypothesis.addAll(hypothesis);
    result.matchMatrix.putAll(matchSet.dataset);
    result.matchMatrixMaybe.putAll(matchSetMaybe.dataset);
    result.free = min;
    result.freeIds.putAll(matchSetMaybeFree);
    return result;
  }
  
  private static Set<Long> uids(Set<Long> ids, Map<Long, Long> umap) {
    Set<Long> uids = new HashSet<>();
    
    for (long i : ids) {
      Long uid = umap.get(i);
      if (uid != null) {
        uids.add(uid);
      }
    }
    
    return uids;
  }
  
  private static Set<Long> getMembers(long cpdId, Map<Long, Double> map) {
    Set<Long> members = new HashSet<>();
    Double v = map.get(cpdId);
    if (v != null && v != 0) {
      if (v > 0) {
        for (long m : map.keySet()) {
          Double mv = map.get(m);
          if (mv != null && mv > 0) {
            members.add(m);
          }
        }
      } else {
        for (long m : map.keySet()) {
          Double mv = map.get(m);
          if (mv != null && mv < 0) {
            members.add(m);
          }
        }
      }
    }
    return members;
  }

  public MatchHypothesis aaaa(BiosUniversalReactionNode unode) {
    Set<BiodbReactionNode> rxnNodes = unode.getReactions();
    Map<Long, Map<Long, Double>> rxnMap = new HashMap<>();
    BMap<Long, Long> umap = new BHashMap<>();
    Set<Long> cpdIds = new HashSet<>();
    for (BiodbReactionNode rxnNode : rxnNodes) {
      rxnMap.put(rxnNode.getId(), rxnNode.getStoichiometry());
      cpdIds.addAll(rxnNode.getStoichiometry().keySet());
    }
    for (long cpdId : cpdIds) {
      BiodbMetaboliteNode cpdNode = service.getMetabolite(cpdId);
      BiosUniversalMetaboliteNode ucpdNode = cpdNode.getUniversalMetabolite();
      if (ucpdNode != null) {
        for (long i : ucpdNode.getMetaboliteIds()) {
          umap.put(i, ucpdNode.getId());
        }
      }
    }
    
    return match(unode.getId(), rxnMap, umap);
  }
//  public MatchHypothesis aaaa(BiosUniversalReactionNode unode) {
//    Set<BiodbReactionNode> rxnNodes = unode.getReactions();
//    Dataset<Long, Long, Long> matchSet = new Dataset<>();
//    Dataset<Long, Long, Character> groupSet = new Dataset<>();
//    Dataset<Long, Long, Set<Long>> matchSetMaybe = new Dataset<>();
//    Map<Long, Set<Long>> matchSetMaybeFree = new HashMap<>();
////    Dataset<String, Long, String> matchSetFake = new Dataset<>();
//    
//    Set<List<Long>> hypothesis = new HashSet<>();
//    Map<Long, Set<Long>> missing = new HashMap<>();
//    Set<Long> uids = new HashSet<>();
//    logger.info("build mapped compounds");
//    
//    int min = Integer.MAX_VALUE;
//    for (BiodbReactionNode e : rxnNodes) {
//      long rxnId = e.getId();
//      missing.put(e.getId(), new HashSet<Long>());
//      matchSet.dataset.put(e.getId(), new HashMap<Long, Long>());
//      matchSetMaybe.dataset.put(e.getId(), new HashMap<Long, Set<Long>>());
//      matchSetMaybeFree.put(rxnId, new HashSet<Long>());
////      matchSetFake.dataset.put(e.getEntry(), new HashMap<>());
//      int misses = 0;
//      Map<Long, Double> stoich = e.getStoichiometry();
//      for (long cpdId : stoich.keySet()) {
//        Double v = stoich.get(cpdId);
//        char group = '*';
//        if (v != null && v > 0) {
//          group = '+';
//        } else if (v != null && v < 0) {
//          group = '-';
//        }
//        BiodbMetaboliteNode cpd = service.getMetabolite(cpdId);
//        BiosUniversalMetaboliteNode ucpd = cpd.getUniversalMetabolite();
//        if (ucpd != null) {
//          uids.add(ucpd.getId());
//          matchSet.add(e.getId(), ucpd.getId(), cpdId);
//          groupSet.add(e.getId(), ucpd.getId(), group);
////          matchSetFake.add(e.getEntry(), uid, cpd.getEntry() + " " + cpd.getProperty("name", ""));
//        } else {
//          misses++;
//        }
//      }
//      if (misses < min) {
//        min = misses;
//      }
//      logger.debug("Misses: {} -> {}", e, misses);
//    }
//    
//    DataUtils.printData(groupSet.dataset, "rxn", "-", "+", "*");
//    
//    logger.debug("Free slots: {}", min);
//    
////    for (BiodbReactionNode e : rxnNodes) {
////      long rxnId = e.getId();
////      for (int i = 0; i < min; i++) {
////        matchSetMaybeFree.get(rxnId).add(new HashSet<Long>());
////      }
////    }
//    
//    logger.debug("match unmapped compounds");
//    for (BiodbReactionNode e : rxnNodes) {
//      long rxnId = e.getId();
//      for (long cpdId : e.getStoichiometry().keySet()) {
//        BiodbMetaboliteNode cpd = service.getMetabolite(cpdId);
//        BiosUniversalMetaboliteNode ucpd = cpd.getUniversalMetabolite();
//        if (ucpd == null) {
//          logger.debug("Reaction[{}] with unmapped compound {}", rxnId, cpdId);
//          Map<Long, Long> mapping = matchSet.dataset.get(rxnId);
//          boolean found = false;
//          for (long i : uids) {
//            if (i != uprotonId) {
//              //            System.out.println(i + " " + mapping.get(i));
//              if (mapping.get(i) == null) {
//                logger.debug("maybe {} -> {}", cpdId, i);
//                ucpd = new BiosUniversalMetaboliteNode(service.getNodeById(i), null);
//                List<Long> l = new ArrayList<>(ucpd.getMetaboliteIds());
//                l.add(cpdId);
//                hypothesis.add(l);
//                CollectionUtils.insertHS(i, cpdId, matchSetMaybe.dataset.get(rxnId));
//                //              matchSetFake.add(e.getEntry(), i, "??" + cpd.getEntry() + " " + cpd.getProperty("name", "") + "??");
//                found = true;
//              }
//            }
//          }
//          if (!found) {
//            logger.debug("Reaction[{}] with free compound {}", rxnId, cpdId);
//            matchSetMaybeFree.get(rxnId).add(cpdId);
//            missing.get(e.getId()).add(cpdId);
//          }
//        }
//      }
//    }
//    
//    List<Set<Long>> l = new ArrayList<>();
//    for (Set<Long> s : matchSetMaybeFree.values()) {
//      l.add(s);
//    }
//    
//    Set<List<Long>> combinations = Sets.cartesianProduct(l);
//    for (List<Long> c : combinations) {
//      hypothesis.add(c);
//    }
//    
////    System.out.println(matchSetMaybeFree);
////    System.out.println(matchSet.dataset);
////    System.out.println(matchSetMaybe.dataset);
////    DataUtils.printData(matchSet.dataset, "rxn");
////    DataUtils.printData(matchSetMaybe.dataset, "rxn");
//    
//    MatchHypothesis result = new MatchHypothesis();
//    result.usetId = unode.getId();
//    result.hypothesis.addAll(hypothesis);
//    result.matchMatrix.putAll(matchSet.dataset);
//    result.matchMatrixMaybe.putAll(matchSetMaybe.dataset);
//    result.free = min;
//    result.freeIds.putAll(matchSetMaybeFree);
//    return result;
////    
//    
////    System.out.println(missing);
//  }
  
  public Report report() {
    BiodbMetaboliteNode proton = service.getMetabolite("h", MetaboliteMajorLabel.BiGGMetabolite);
    BiosUniversalMetaboliteNode uproton = null;
    if (proton != null && (uproton = proton.getUniversalMetabolite()) != null) {
      uprotonId = uproton.getId();
      logger.info("proton: {}", uprotonId);
    }
    
    Report report = new Report();
    for (Node n : service.listNodes(CurationLabel.UniversalReaction)) {
      BiosUniversalReactionNode unode = new BiosUniversalReactionNode(n, null);
      MatchHypothesis result = aaaa(unode);
      if (!result.hypothesis.isEmpty()) {
        report.reports.put(n.getId(), result);
      }
    }
    
    return report;
  }
}
