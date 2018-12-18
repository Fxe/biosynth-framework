package pt.uminho.sysbio.biosynthframework.neo4j.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosUniversalMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.neo4j.OntologyDatabase;

public class MetabolicModelIntegrationTools {
  
  private static final Logger logger = LoggerFactory.getLogger(MetabolicModelIntegrationTools.class);
  
  public static void fixGeneProteinSbo(String modelEntry, BiodbGraphDatabaseService services) {
    BiosMetabolicModelNode model = services.getMetabolicModel(modelEntry);
    for (BiosModelSpeciesNode spi : model.getMetaboliteSpecies()) {
      String sid = spi.getSid();
      if (sid.startsWith("E_")) {
        Node sboterm = services.getNodeByEntryAndLabel("SBO:0000243", OntologyDatabase.SBO);
        spi.setSystemsBiologyOntology(sboterm);
      }
      if (sid.startsWith("Cx_")) {
        Node sboterm = services.getNodeByEntryAndLabel("SBO:0000297", OntologyDatabase.SBO);
        spi.setSystemsBiologyOntology(sboterm);
      }
    }
  }
  
  public static Set<Long> reduce(Map<String, Set<Long>> map) {
    if (map.isEmpty()) {
      return new HashSet<>();
    }
    
    Iterator<String> it = map.keySet().iterator();
    Set<Long> reduce = new HashSet<>(map.get(it.next()));
    
    while (it.hasNext()) {
      reduce = Sets.intersection(reduce, map.get(it.next()));
    }
    
    return reduce;
  }
  
  public static void expandSpiAnnotation(String model, int propagationCost, BiodbGraphDatabaseService service) {
    BiosMetabolicModelNode modelNode = service.getMetabolicModel(model);
    for (BiosModelSpeciesNode spiNode : modelNode.getMetaboliteSpecies()) {
      Map<Long, Integer> exists = new HashMap<>();
      
      Map<Long, Integer> propagate = new HashMap<>();

      for (BiodbMetaboliteNode cpdNode : spiNode.getReferences()) {
        Integer score = spiNode.getAnnotationScore(cpdNode);
        if (score == null) {
          score = 1;
        }
        
        exists.put(cpdNode.getId(), score);
        
        BiosUniversalMetaboliteNode ucpdNode = cpdNode.getUniversalMetabolite();
        if (ucpdNode != null) {
          for (BiodbMetaboliteNode refNode : ucpdNode.getMetabolites()) {
            Integer pscore = score - propagationCost;
            if (pscore < 1) {
              pscore = 1;
            }
            
            if (propagate.containsKey(refNode.getId())) {
              Integer prevScore = propagate.get(refNode.getId());
              if (prevScore > pscore) {
                pscore = prevScore;
              }
            }
            propagate.put(refNode.getId(), pscore);
          }
        }
      }
      
      for (long id : propagate.keySet()) {
        Integer score = propagate.get(id);
        BiodbMetaboliteNode cpdNode = service.getMetabolite(id);
        logger.info("ADD {}* {} <- {}:{}", score, spiNode.getSid(), cpdNode.getEntry(), cpdNode.getDatabase());
        spiNode.addAnnotation(cpdNode, score, "system");
      }
//      System.out.println(spiNode.getSid() + "\t" + exists5 + "\t" + exists + "\t" + exp5 + "\t" + exp);
    }
  }
}
