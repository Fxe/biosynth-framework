package pt.uminho.sysbio.biosynthframework.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;

public class Neo4jReactionIntegrationService implements ReactionIntegrationService<Long>{

  private static final Logger logger = LoggerFactory.getLogger(Neo4jReactionIntegrationService.class);
  
  private final BiodbGraphDatabaseService service;
  
  protected final Set<ConnectedComponents<Long>> curationSets = new HashSet<>();
  protected final Set<ReactionMajorLabel> rxnDatabases = new HashSet<>();
  protected final Set<Long> exclude = new HashSet<>();
  protected ConnectedComponents<Long> complement;
  
  public Neo4jReactionIntegrationService(GraphDatabaseService service) {
    this.service = new BiodbGraphDatabaseService(service);
  }
  
  public Set<ConnectedComponents<Long>> getCurationSets() { return curationSets;}
  public Set<ReactionMajorLabel> getRxnDatabases() { return rxnDatabases;}
  public Set<Long> getExclude() { return exclude;}
  public ConnectedComponents<Long> getComplement() { return complement;}

  public static ConnectedComponents<Long> getComplement(Map<Map<Set<Long>, Double>, Set<Long>> singleMissStoich,
      Map<Map<Set<Long>, Double>, Set<Long>> singleMissValues) {
    ConnectedComponents<Long> ccComp = new ConnectedComponents<>();
    Set<Long> added = new HashSet<>();
    for (Map<Set<Long>, Double> ms : singleMissStoich.keySet()) {
      if (singleMissValues.get(ms).size() > 1) {
        Set<Long> cc = new HashSet<> ();
        if (Sets.intersection(added, singleMissValues.get(ms)).isEmpty()) {
          ccComp.add(cc);
          added.addAll(singleMissValues.get(ms));
        }

      }

    }

    return ccComp;
  }
  
  public static ConnectedComponents<Long> getRxnIntegration(Map<Map<Set<Long>, Double>, Set<Long>> result) {
    ConnectedComponents<Long> rxnIntegration = new ConnectedComponents<>();
    for (Object istoich : result.keySet()) {
      Set<Long> rxnIds = result.get(istoich);
      if (rxnIds.size() > 1) {
        rxnIntegration.add(rxnIds);
      }
    }
    return rxnIntegration;
  }
  
  public SingleMissReactionIntegration<Long, Long> setupReactionIntegrationMethod(
      ConnectedComponents<Long> cpdIntegration, 
//      IntegrationConfiguration configuration, 
      BiodbGraphDatabaseService service) {
    
    Set<Long> rxnStoichMetabolites = new HashSet<> ();
    Map<Long, Map<Long, Double>> rxnStoichMap = new HashMap<> ();
    for (ReactionMajorLabel database : rxnDatabases) {
      for (BiodbReactionNode rxnNode : service.listReactions(database)) {
        boolean valid = !rxnNode.isProxy() && rxnNode.isBasic();
        if (rxnNode.hasLabel(ReactionMajorLabel.BiGGReaction) && valid) {
          boolean pseudoreaction = (boolean) rxnNode.getProperty("pseudoreaction", false);
          valid = !pseudoreaction;
        }
        
        if (valid) {
          Map<Long, Double> stoich = rxnNode.getStoichiometry();
          if (!stoich.isEmpty()) {
            rxnStoichMetabolites.addAll(stoich.keySet());
            rxnStoichMap.put(rxnNode.getId(), stoich);
          }
        } else {
          
        }
      }
    }
    
    SingleMissReactionIntegration<Long, Long> reactionIntegration = 
        new SingleMissReactionIntegration<>(cpdIntegration);
    reactionIntegration.exclude.addAll(exclude);
    reactionIntegration.reactionStoichs.putAll(rxnStoichMap);
    
    return reactionIntegration;
  }
  
  @Override
  public ConnectedComponents<Long> entityResolution(ConnectedComponents<Long> cpdIntegration) {
    SingleMissReactionIntegration<Long, Long> method = 
        setupReactionIntegrationMethod(cpdIntegration, service);
    
    logger.info("resolving reactions ...");
    Map<Map<Set<Long>, Double>, Set<Long>> result = method.integrate();
    
    ConnectedComponents<Long> rxnIntegration = getRxnIntegration(result);
    
    complement = getComplement(method.singleMissStoich, method.singleMissValues);
    
    return rxnIntegration;
  }

}
