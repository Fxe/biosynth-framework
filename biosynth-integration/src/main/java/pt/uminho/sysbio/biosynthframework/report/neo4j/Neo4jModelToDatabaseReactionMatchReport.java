package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.integration.ReactionMatcher;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelReactionNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;

public class Neo4jModelToDatabaseReactionMatchReport extends AbstractNeo4jReporter {

  public static class ModelToDatabaseReactionMatchReportResult {
    public Map<Long, Integer> mrxnIdToMissCount = new HashMap<>();
    public Map<Long, Boolean> mrxnIdToBasic = new HashMap<>();
    public Map<Long, Map<Long, Double>> rxnIdToStoich = new HashMap<>();
    public Map<Long, Long> translationMap = new HashMap<>();
    public Map<Long, Set<Long>> mrxnIdToRxnSet = new HashMap<>();
  }
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jModelToDatabaseReactionMatchReport.class);
  
  public Set<Long> exclude = new HashSet<>();
  
  public Neo4jModelToDatabaseReactionMatchReport(GraphDatabaseService service) {
    super(service);
  }
  
  public ModelToDatabaseReactionMatchReportResult report(String model, ReactionMajorLabel rxnDatabase) {
    ModelToDatabaseReactionMatchReportResult result = new ModelToDatabaseReactionMatchReportResult();
    
    Set<Long> validCpdIds = new HashSet<>();
    Map<Long, Map<Long, Double>> rxnMap = new HashMap<>();
    for (BiodbReactionNode rxnNode : service.listReactions(rxnDatabase)) {
      boolean obsolete = (boolean) rxnNode.getProperty("obsolete", false);
      if (rxnNode.isBasic() && !obsolete) {
        Map<Long, Double> stoich = rxnNode.getStoichiometry();
        validCpdIds.addAll(stoich.keySet());
        stoich.keySet().removeAll(exclude);
        rxnMap.put(rxnNode.getId(), stoich);
      }
    }
    
    BiosMetabolicModelNode modelNode = service.getMetabolicModel(model);
    Map<Long, Long> spiTranslationMap = new HashMap<>();
    for (BiosModelSpeciesNode spiNode : modelNode.getMetaboliteSpecies()) {
      logger.debug("{}:{}", spiNode, spiNode.getSid());
      Set<BiodbMetaboliteNode> dblinks = spiNode.getReferences();
      Set<Long> cpdIds = new HashSet<>();
      for (BiodbMetaboliteNode cpdNode : dblinks) {
        cpdIds.add(cpdNode.getId());
      }
      Set<Long> i = Sets.intersection(cpdIds, validCpdIds);
      if (i.size() == 1) {
        long translatedId = i.iterator().next();
        logger.debug("{}:{} -> {}", spiNode, spiNode.getSid(), translatedId);
        spiTranslationMap.put(spiNode.getId(), translatedId);
      }
    }
    
    for (BiosModelReactionNode mrxnNode : modelNode.getModelReactions()) {
      Map<Long, Double> s = mrxnNode.getStoichiometry();
      
      boolean basic = true;
      int misses = 0;
      Map<Long, Double> st = new HashMap<>();
      for (long spiId : s.keySet()) {
        if (spiTranslationMap.containsKey(spiId)) {
          if (st.put(spiTranslationMap.get(spiId), s.get(spiId)) != null) {
            basic = false;
          }
        } else {
          misses++;
        }
      }
      
      result.rxnIdToStoich.put(mrxnNode.getId(), s);
      result.mrxnIdToMissCount.put(mrxnNode.getId(), misses);
      result.mrxnIdToBasic.put(mrxnNode.getId(), basic);
    }
    
    result.translationMap.putAll(spiTranslationMap);
    
    ReactionMatcher<Long, Long, Long> matcher = new ReactionMatcher<>();
    matcher.exclude.addAll(this.exclude);
    for (long rxnId : rxnMap.keySet()) {
      matcher.addDatabaseReaction(rxnId, rxnMap.get(rxnId));
    }
    
    logger.info("Database Stoichs: {}", matcher.dbtoichDictionary.size());
    
    for (long mrxnId : result.mrxnIdToBasic.keySet()) {
      if (result.mrxnIdToBasic.get(mrxnId)) {
        Set<Long> match = matcher.match(result.rxnIdToStoich.get(mrxnId), spiTranslationMap);
        if (match != null) {
          result.mrxnIdToRxnSet.put(mrxnId, match);
        }
      }
    }
    return result;
  }
}
