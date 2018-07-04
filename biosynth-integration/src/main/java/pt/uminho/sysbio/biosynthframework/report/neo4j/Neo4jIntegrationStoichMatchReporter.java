package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.integration.ReactionMatcher;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalReactionNode;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.MapUtils;

public class Neo4jIntegrationStoichMatchReporter extends AbstractNeo4jReporter {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jIntegrationStoichMatchReporter.class);

  //  private ConnectedComponents<Long> ccs = null;
  private Map<Long, Long> cpdUniMap = null;
  private ReactionMatcher<Long, Long, Long> matcher = null;
  //  private Map<Long, Map<Long, Double>> rxnIdToStoich = null;
  //  private Map<Long, Set<Long>> missing = null;
  private Map<Long, Set<Long>> cpdToRxnMap = new HashMap<>();
  private Map<ReactionMajorLabel, Map<Long, Map<Long, Double>>> reactionStoichCache = new HashMap<>();

  public Neo4jIntegrationStoichMatchReporter(GraphDatabaseService service) {
    super(service);
  }

  public void rebuildData() {
//    cpdUniMap = new Neo4jUniMap(service, CurationLabel.UniversalMetabolite);
    logger.info("rebuild indexes");
    //    ccs = new ConnectedComponents<>();
    
    cpdUniMap = new HashMap<>();
    for (Node n : service.listNodes(CurationLabel.UniversalMetabolite)) {
      BiosUniversalMetaboliteNode unode = new BiosUniversalMetaboliteNode(n, null);
      Set<Long> ids = new HashSet<>();
      for (BiodbMetaboliteNode cpdNode : unode.getMetabolites()) {
        ids.add(cpdNode.getId());
        cpdUniMap.put(cpdNode.getId(), unode.getId());
      }
      //      ccs.add(ids);
    }

    matcher = new ReactionMatcher<>();
    BiodbMetaboliteNode proton = service.getMetabolite("h", MetaboliteMajorLabel.BiGGMetabolite);
    if (proton != null && cpdUniMap.containsKey(proton.getId())) {
      matcher.exclude.add(cpdUniMap.get(proton.getId()));
    }

    Set<ReactionMajorLabel> rxnDatabases = new HashSet<>();
    rxnDatabases.add(ReactionMajorLabel.LigandReaction);
    rxnDatabases.add(ReactionMajorLabel.MetaCyc);
    rxnDatabases.add(ReactionMajorLabel.ModelSeedReaction);
    rxnDatabases.add(ReactionMajorLabel.BiGGReaction);
    rxnDatabases.add(ReactionMajorLabel.BiGG);

    //    rxnIdToStoich = new HashMap<>();
    //    missing = new HashMap<>();

    for (ReactionMajorLabel database : rxnDatabases) {
      if (!reactionStoichCache.containsKey(database)) {
        logger.info("build stoich cache for {}", database);
        reactionStoichCache.put(database, new HashMap<Long, Map<Long, Double>>());
        for (BiodbReactionNode rxnNode : service.listReactions(database)) {
          if (rxnNode.isBasic()) {
            Map<Long, Double> s = rxnNode.getStoichiometry();
            reactionStoichCache.get(database).put(rxnNode.getId(), s);
            for (long cpdId : s.keySet()) {
              CollectionUtils.insertHS(cpdId, rxnNode.getId(), cpdToRxnMap);
            }
          }
        }
      }
    }

    long start = System.currentTimeMillis();
    for (ReactionMajorLabel database : rxnDatabases) {
      for (long rxnId : reactionStoichCache.get(database).keySet()) {
        Map<Long, Double> s = getRxnTranslatedStoich(reactionStoichCache.get(database).get(rxnId));
        if (s != null) {
          //            rxnIdToStoich.put(rxnNode.getId(), s);
          matcher.addDatabaseReaction(rxnId, s);
          //            Set<Long> m = Sets.difference(s.keySet(), cpdUniMap.keySet());
          //            missing.put(rxnNode.getId(), m);
          //            Map<Long, Double> st = matcher.translate(s, cpdUniMap);
        }
      }
    }

    logger.info("took {} / s", (System.currentTimeMillis() - start) / 1000.0);
  }

  public Map<Long, Double> getRxnTranslatedStoich(BiodbReactionNode rxnNode) {
    return getRxnTranslatedStoich(rxnNode.getStoichiometry());
  }
  
  public Map<Long, Double> getRxnTranslatedStoich(Map<Long, Double> s) {
    Map<Long, Double> st = null;
    if (s != null && !s.isEmpty()) {
      st = matcher.translate(s, cpdUniMap);
    }
    return st;
  }

  public static class Report {
    public Map<Set<ReactionMajorLabel>, Set<Set<Long>>> matchSets = new HashMap<>();
    public Set<Long> cpdSet = new HashSet<>();
  }

  public Report report(String cpdEntry, MetaboliteMajorLabel database) {
    rebuildData();

    Report report = new Report();
    Set<Long> visited = new HashSet<>();
    BiodbMetaboliteNode cpdNode = service.getMetabolite(cpdEntry, database);
    BiosUniversalMetaboliteNode ucpd = cpdNode.getUniversalMetabolite();
    Set<Long> cc = new HashSet<>();
    if (ucpd == null) {
      cc.add(cpdNode.getId());
    } else {
      cc.addAll(ucpd.getMetaboliteIds());
    }

    logger.info("Set: {}", cc);
    for (long cpdId : cc) {
      if (cpdToRxnMap.containsKey(cpdId)) {
        for (long rxnId : cpdToRxnMap.get(cpdId)) {
          if (!visited.contains(rxnId)) {
            BiodbReactionNode rxnNode = service.getReaction(rxnId);
            logger.debug("rxn: {}", rxnNode);
            Map<Long, Double> s = rxnNode.getStoichiometry();
            Set<Long> rxnIdsFor = matcher.match(s, cpdUniMap);
            Set<Long> rxnIdsRev = matcher.match(MapUtils.scale(s, -1), cpdUniMap);
            logger.debug("rxn: {} -> F: {} R: {}", rxnNode, rxnIdsFor.size(), rxnIdsRev.size());
            Set<Long> rxnIds = Sets.union(rxnIdsFor, rxnIdsRev);
            visited.addAll(rxnIds);
            Set<BiodbReactionNode> rxnNodes = new HashSet<>();
            Set<ReactionMajorLabel> databases = new HashSet<>();
            Set<Long> idsFromUniversal = new HashSet<>();
            Set<ReactionMajorLabel> databasesFromUniversal = new HashSet<>();
            for (long i : rxnIds) {
              BiodbReactionNode r = service.getReaction(i);
              databases.add(r.getDatabase());
              rxnNodes.add(r);
              BiosUniversalReactionNode urxn = r.getUniversalReaction();
              if (urxn != null) {
                for (BiodbReactionNode o : urxn.getReactions()) {
                  idsFromUniversal.add(o.getId());
                  databasesFromUniversal.add(o.getDatabase());
                }
              }
            }

            rxnIds = Sets.union(rxnIds, idsFromUniversal);
            databases = Sets.union(databases, databasesFromUniversal);
            CollectionUtils.insertHS(databases, rxnIds, report.matchSets);
            logger.debug("rxn: {} -> {} :: {}", rxnNode, databases, rxnNodes);
          }
        }
      }
    }

    report.cpdSet.addAll(cc);
    return report;
  }
}
