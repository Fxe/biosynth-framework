package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.CompartmentalizedStoichiometry;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedUtils;
import pt.uminho.sysbio.biosynthframework.integration.ReactionTMatcher;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelReactionNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;

public class Neo4jMetabolicModelReactionIntegration implements MetabolicModelReactionIntegration {
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jMetabolicModelReactionIntegration.class);
  
  private final BiodbGraphDatabaseService service;
  
  public Function<BiodbReactionNode, CompartmentalizedStoichiometry<Long, Object>> nodeConverter;
  
  public Dataset<String, String, Tuple2<Object>> report = new Dataset<>();
  public Dataset<String, String, Object> treport = new Dataset<>();
  public Dataset<String, String, Object> mreport = new Dataset<>();
  
  public Neo4jMetabolicModelReactionIntegration(BiodbGraphDatabaseService service) {
    this.service = service;
  }
  
  public static CompartmentalizedStoichiometry<Long, Long> mapToUniversals(CompartmentalizedStoichiometry<Long, Long> aa, Map<Long, Long> map) {
    CompartmentalizedStoichiometry<Long, Long> ustoich = new CompartmentalizedStoichiometry<>();
//    logger.debug("[{}] {}", mrxnNode.getSid(), aa);
    for (Pair<Long, Long> p : aa.stoichiometry.keySet()) {
      long spiId = p.getLeft();
      double value = aa.stoichiometry.get(p);
      if (map.containsKey(spiId)) {
        long cpdId = map.get(spiId);
        ustoich.add(cpdId, p.getRight(), value);
      } else {
        ustoich.add(spiId, p.getRight(), value);
      }
//      BiosModelSpeciesNode spiNode = new BiosModelSpeciesNode(service.getNodeById(spiId), null);
//      long id = spiNode.getId();
//      logger.debug("[{}] {} {} -> {}", mrxnNode.getSid(), p, value, id);
    }
    
    return ustoich;
  }
  
  public<T> void aaa(BiosModelReactionNode mrxnNode, ReactionTMatcher<T, Long> matcher, 
      Map<Long, Long> uspiMap, Set<BiodbMetaboliteNode> removes, Dataset<String, String, Object> report) {
    String sid = mrxnNode.getSid();
    CompartmentalizedStoichiometry<Long, Long> aa = mrxnNode.getCompartmentalizedStoichiometry(1.0);
    
    if (aa.getCompartments().size() > 0) {
      CompartmentalizedStoichiometry<Long, Long> ustoich = mapToUniversals(aa, uspiMap);
      if (removes != null) {
        for (BiodbMetaboliteNode remove : removes) {
          ustoich.remove(remove.getId());          
        }
      }
//      System.out.println("universal " + ustoich);
      if (ustoich.getCompartments().size() > 0) {
        Set<Long> mresult = matcher.match(ustoich);
        
        logger.debug("{} {}", ustoich, mresult);
//        print(ustoich);
        if (mresult == null || mresult.isEmpty()) {
          CompartmentalizedStoichiometry<Long, Long> ustoichRev = new CompartmentalizedStoichiometry<>();
          for (Pair<Long, Long> p : ustoich.stoichiometry.keySet()) {
            double v = ustoich.stoichiometry.get(p);
            ustoichRev.stoichiometry.put(p, -1 * v);
          }
          
          mresult = matcher.match(ustoichRev);
          
          logger.debug("{} {}", ustoichRev, mresult);
          
        }
        Set<String> names = new HashSet<>();
        if (mresult != null) {
          for (long i : mresult) {
            names.add(service.getReaction(i).getEntry());
          }
        }
        String best = ModelSeedUtils.selectLowestId(names);
//        int s = 0;
//        for (String n : names) {
//          if (sid.contains(n) && s < n.length()) {
//            best = n;
//            s = n.length();
//          }
//        }
//        if (best == null) {
//          for (String n : names) {
//            if (sid.toLowerCase().contains(n.toLowerCase()) && s < n.length()) {
//              best = n;
//              s = n.length();
//            }
//          }
//        }
        if (mresult != null && !mresult.isEmpty()) {
          report.add(sid, "ustoich", ustoich);
          report.add(sid, "mresult", mresult);
          report.add(sid, "all", names);
          report.add(sid, "best", best);
        }
      }
    }
  }
  
  public Map<Long, Long> getIdTranslation(
      Dataset<String, String, String> mapping, 
      BiosMetabolicModelNode modelNode, 
      MetaboliteMajorLabel target) {
    
    Map<Long, Long> uspiMap = new HashMap<>();
    
    for (String spiEntry : mapping.dataset.keySet()) {
      for (String db : mapping.dataset.get(spiEntry).keySet()) {
        if (db.equals(target.toString())) {
          BiodbMetaboliteNode cpdNode = service.getMetabolite(mapping.dataset.get(spiEntry).get(db), target);
          BiosModelSpeciesNode spiNode = modelNode.getMetaboliteSpecie(spiEntry);
          if (cpdNode != null && spiNode != null) {
            uspiMap.put(spiNode.getId(), cpdNode.getId());
          } else {
            logger.warn("spi: {} cpd: {}", spiNode, cpdNode);
          }
        }
      }
    }
    
    return uspiMap;
  }
  
  @SuppressWarnings("unchecked")
  public IntegrationMap<String, ReactionMajorLabel> reactionIntegration(
      Dataset<String, String, String> mapping, 
      BiosMetabolicModelNode modelNode, 
      MetaboliteMajorLabel cpdDatabase, ReactionMajorLabel rxnDatabase,
      Set<BiodbMetaboliteNode> exclude) {
    
    IntegrationMap<String, ReactionMajorLabel> integration = new IntegrationMap<>();
    Map<Long, Long> uspiMap = getIdTranslation(mapping, modelNode, cpdDatabase);
    
    logger.info("uspiMap (size) {}", uspiMap.size());
    
//    Dataset<String, String, Object> trxnReport = new Dataset<>(); 
//    Dataset<String, String, Object> mrxnReport = new Dataset<>();
    
    {
      ReactionTMatcher<Object, Long> matcher = new ReactionTMatcher<>();
      matcher.allowSingle = false;
      matcher.testReverse = true;
      for (BiodbReactionNode rxnNode : service.listReactions(rxnDatabase)) {
        if (!rxnNode.isBasic()) {
          CompartmentalizedStoichiometry<Long, Object> cstoich = nodeConverter.apply(rxnNode);
          matcher.addReaction(cstoich, rxnNode.getId());
        }
      }
      
      logger.info("translocation matcher (size) {}", matcher.cstoichToRxnIds.size());
      
      for (BiosModelReactionNode mrxnNode : modelNode.getModelReactions()) {
        if (mrxnNode.isTranslocation()) {
          aaa(mrxnNode, matcher, uspiMap, null, treport);
        }
      }
    }
    {
      ReactionTMatcher<Object, Long> matcher = new ReactionTMatcher<>();
      matcher.allowSingle = true;
      matcher.testReverse = true;
      
//      Set<BiodbMetaboliteNode> exclude = new HashSet<>();
//      BiodbMetaboliteNode hydron = proton;
      
      for (BiodbReactionNode rxnNode : service.listReactions(rxnDatabase)) {
        if (rxnNode.isBasic()) {
          CompartmentalizedStoichiometry<Long, Object> cstoich = nodeConverter.apply(rxnNode);
          if (exclude != null) {
            for (BiodbMetaboliteNode ex : exclude) {
              cstoich.remove(ex.getId());
            }
          }

          matcher.addReaction(cstoich, rxnNode.getId());
        }
      }
      
      logger.info("matcher (size) {}", matcher.cstoichToRxnIds.size());
      
      
      
      for (BiosModelReactionNode mrxnNode : modelNode.getModelReactions()) {
        if (!mrxnNode.isTranslocation()) {
          aaa(mrxnNode, matcher, uspiMap, exclude, mreport);
        }
      }
    }
    
    Set<String> both = Sets.intersection(treport.keySet(), mreport.keySet());
    Set<String> columns = Sets.union(treport.getColumns(), mreport.getColumns());
    
    for (String rxnEntry : both) {
      for (String col : columns) {
        report.add(rxnEntry, col, 
            new Tuple2<Object>(
                treport.get(rxnEntry).get(col), 
                mreport.get(rxnEntry).get(col)));
      }
    }
    for (String rxnEntry : treport.keySet()) {
      if (!both.contains(rxnEntry)) {
        for (String col : columns) {
          report.add(rxnEntry, col, 
              new Tuple2<Object>(
                  treport.get(rxnEntry).get(col), 
                  null));
        }
      }
    }
    for (String rxnEntry : mreport.keySet()) {
      if (!both.contains(rxnEntry)) {
        for (String col : columns) {
          report.add(rxnEntry, col, 
              new Tuple2<Object>(
                  null, 
                  mreport.get(rxnEntry).get(col)));
        }
      }
    }
    
    for (String rxnEntry : report.dataset.keySet()) {
      
      Tuple2<Object> tuple = report.get(rxnEntry).get("all");
//      System.out.println(rxnEntry + " " + tuple);
      if (tuple.e1 != null) {
        for (Object o : (Iterable<Object>)tuple.e1) {
          integration.addIntegration(rxnEntry, rxnDatabase, o.toString());
        }
      }
      if (tuple.e2 != null) {
        for (Object o : (Iterable<Object>)tuple.e2) {
          integration.addIntegration(rxnEntry, rxnDatabase, o.toString());
        }
      }
    }
    
    return integration;
  }
  
  public IntegrationMap<String, ReactionMajorLabel> reactionIntegration(Dataset<String, String, String> mapping, BiosMetabolicModelNode modelNode) {
    Set<BiodbMetaboliteNode> exclude = new HashSet<>();
    exclude.add(service.getMetabolite("cpd00067", MetaboliteMajorLabel.ModelSeed));
    return reactionIntegration(mapping, modelNode, 
                               MetaboliteMajorLabel.ModelSeed, 
                               ReactionMajorLabel.ModelSeedReaction, 
                               exclude);
  }
}
