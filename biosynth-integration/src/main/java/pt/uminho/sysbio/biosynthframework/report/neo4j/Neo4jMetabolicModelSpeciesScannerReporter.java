package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jMetabolicModelSpeciesScannerReporter {
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jMetabolicModelSpeciesScannerReporter.class);
  
  private final BiodbGraphDatabaseService service;
  
  public Neo4jMetabolicModelSpeciesScannerReporter(BiodbGraphDatabaseService service) {
    this.service = service;
  }
  
  public Dataset<String, String, Object> report(List<String> models, 
      SubcellularCompartment scmp, List<ExternalReference> exclude, Set<Long> excludeId, Map<String, Map<String, String>> manualspi) {
    
    Set<Long> manualMappedSpecies = new HashSet<>();
    for (String model : manualspi.keySet()) {
      BiosMetabolicModelNode modelNode = service.getMetabolicModel(model);
      if (modelNode != null) {
        for (String spiEntry : manualspi.get(model).keySet()) {
          BiosModelSpeciesNode spiNode = modelNode.getMetaboliteSpecie(spiEntry);
          if (spiNode != null) {
            manualMappedSpecies.add(spiNode.getId());
          } else {
            logger.warn("not found {} {}", model, spiEntry);
          }
        }
      }
    }
    
    excludeId.addAll(manualMappedSpecies);
    
//    System.out.println(excludeId);
    
    Dataset<String, String, Object> result = bigScan(models, scmp, exclude, excludeId);
    return result;
  }
  
  public static boolean isAny(BiosModelSpeciesNode spiNode, List<ExternalReference> exclude) {
    for (BiodbMetaboliteNode cpdNode : spiNode.getReferences()) {
      Integer score = spiNode.getAnnotationScore(cpdNode);
      if (score != null && score == 5) {
        for (ExternalReference e : exclude) {
          if (e.entry.equals(cpdNode.getEntry()) && e.source.equals(cpdNode.getDatabase().toString())) {
            logger.debug("{} is one of {}", spiNode.getSid(), exclude);
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public static Set<Long> exclude(Set<Long> spiIds, List<ExternalReference> exclude, Map<Long, BiosModelSpeciesNode> spiNodes, BiodbGraphDatabaseService service) {
    Set<Long> ok = new HashSet<>();
    for (long id : spiIds) {
      BiosModelSpeciesNode spiNode = spiNodes.get(id);
      if (!isAny(spiNode, exclude)) {
        ok.add(id);
      }
    }
    return ok;
  }
  
  public Dataset<String, String, Object> bigScan(List<String> models, 
      SubcellularCompartment scmp, 
      List<ExternalReference> exclude, Set<Long> excludeId) {
    Dataset<String, String, Object> result = new Dataset<>();
    for (String modelEntry : models) {
      BiosMetabolicModelNode modelNode = service.getMetabolicModel(modelEntry);
      
      Set<String> sboIgnore = new HashSet<>();
      sboIgnore.add("SBO:0000243");
      sboIgnore.add("SBO:0000297");
      
      if (!modelNode.isProxy()) {
        //java 1.7 ...
        //Map<Integer, Set<Long>> degreeToSpecies = new TreeMap<>(Comparator.reverseOrder());
        Map<Integer, Set<Long>> degreeToSpecies = new TreeMap<>();
        Map<Long, BiosModelSpeciesNode> spiNodes = new HashMap<>();
        for (BiosModelSpeciesNode spiNode : modelNode.getMetaboliteSpecies()) {
          int degree = spiNode.getDegree();
          Node sboNode = spiNode.getSystemsBiologyOntology();
          if (sboNode == null || !sboIgnore.contains(sboNode.getProperty("entry"))) {
            SubcellularCompartment cmp = spiNode.getSubcellularCompartment();
            if (scmp.equals(cmp)) {
              spiNodes.put(spiNode.getId(), spiNode);
              CollectionUtils.insertHS(degree, spiNode.getId(), degreeToSpecies);
            }
          }
        }
        
        if (!degreeToSpecies.isEmpty()) {
//          String modelEntry = modelNode.getEntry();
          List<Integer> its = new ArrayList<>(new TreeSet<>(degreeToSpecies.keySet()));
          its = Lists.reverse(its);
          Iterator<Integer> it = its.iterator();
//          Iterator<Integer> it = degreeToSpecies.keySet().iterator();
          int max = it.next();
          Set<Long> spiIds = degreeToSpecies.get(max);
          spiIds.removeAll(excludeId);
          spiIds = exclude(spiIds, exclude, spiNodes, service);
          
          while (it.hasNext() && spiIds.isEmpty()) {
            max = it.next();
            logger.debug("next degree: {}", max);
            spiIds = degreeToSpecies.get(max);
            spiIds.removeAll(excludeId);
            spiIds = exclude(spiIds, exclude, spiNodes, service);
          }
          
          result.add(modelEntry, "degree", max);
          result.add(modelEntry, "match", spiIds.size());
          
          int index = 0;
          for (long id : spiIds) {
            index++;
            BiosModelSpeciesNode spiNode = spiNodes.get(id);
            result.add(modelEntry, "sid " + index, spiNode.getSid());
            result.add(modelEntry, "name " + index, spiNode.getProperty("name", ""));
            List<ExternalReference> refs = new ArrayList<> ();
            
            int maxScore = 0;
            for (BiodbMetaboliteNode cpdNode : spiNode.getReferences()) {
              refs.add(new ExternalReference(cpdNode.getEntry(), cpdNode.getDatabase().toString()));
              Integer score = spiNode.getAnnotationScore(cpdNode);
              if (score != null && score > maxScore) {
                maxScore = score;
              }
            }
            
            result.add(modelEntry, "references " + index, refs);
            result.add(modelEntry, "score " + index, maxScore);
            result.add(modelEntry, "id " + index, id);
          }
//          System.out.println(modelNode.getEntry() + "\t" + max + "\t" + spiIds);
//
//          //annotation
//          //expected
        }
      }
    }
    
    return result;
//    DataUtils.printData(result.dataset, "model", "degree", "match", "sid 1", "name 1", "references 1", "score 1", "id 1");
  }
}