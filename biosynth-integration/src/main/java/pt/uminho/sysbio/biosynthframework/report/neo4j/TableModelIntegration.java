package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbNode;
//import pt.uminho.sysbio.biosynthframework.Source;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.Tuple2;

public class TableModelIntegration<T> {
  
  public static enum Source {
    Resource, Manual, Curation, Inferred, Undefined, Predicted, 
  }

  public Function<List<Tuple2<String>>, T> printer;
  
  private final BiodbService biodbService;
  private final GraphDatabaseService graphDataService;
  private final long integrationSetId;
  
  public TableModelIntegration(GraphDatabaseService graphDataService, 
                               BiodbService biodbService,
                               long integrationSetId) {
    this.biodbService = biodbService;
    this.graphDataService = graphDataService;
    this.integrationSetId = integrationSetId;
  }
  
  public Dataset<String, String, T> getTable(String[] models, String[] ucomp, MetaboliteMajorLabel database) {
    Dataset<String, String, T> result = new Dataset<>();
    Set<Long> ucompIds = new HashSet<> ();
    for (String entry : ucomp) {
      Node cpdNode = Neo4jUtils.getNodeByEntry(database, entry, graphDataService);
      ucompIds.add(cpdNode.getId());
    }
    
    Map<String, Map<Long, Set<Long>>> data = new HashMap<> ();
    
    for (String modelEntry : models) {
      Map<Long, Set<Long>> species = lookupSpecies(modelEntry, ucompIds);
      data.put(modelEntry, species);
    }
    
    for (String modelEntry : data.keySet()) {
      for (long cpdId : data.get(modelEntry).keySet()) {
        BiodbNode cpdNode = new BiodbNode(graphDataService.getNodeById(cpdId));
        String cpdEntry = cpdNode.getEntry();
        
        List<Tuple2<String>> match = new ArrayList<> ();
        
        for (long spiId : data.get(modelEntry).get(cpdId)) {
          Node spiNode = graphDataService.getNodeById(spiId);
          String spiEntry = (String) spiNode.getProperty("entry");
          String spiName = (String) spiNode.getProperty("name", null);
          match.add(new Tuple2<String>(spiEntry, spiName));
        }
        
        T label = null;
        if (printer != null) {
          label = printer.apply(match);
        }
        
        result.add(modelEntry, cpdEntry, label);
      }
    }
    
    return result;
  }
  
  public Map<Long, Set<Long>> lookupSpecies(String modelEntry, Set<Long> ucompIds) {
//    BiodbService service = new Neo4jBiodbDataServiceImpl(
//        DATA_NEO4J_DAO, META_NEO4J_DAO, CURA_NEO4J_DAO, INTEGRATION_SET);
    
    Node modelNode = Neo4jUtils.getNodeByEntry(GlobalLabel.MetabolicModel, modelEntry, graphDataService);
    
    
    Map<Long, Set<Long>> result = new HashMap<> ();
    
    for (Node spiNode : Neo4jUtils.collectNodeRelationshipNodes(
        modelNode, MetabolicModelRelationshipType.has_specie)) {
      
//      System.out.println(spiNode.getProperty("entry") + "\t" + spiNode.getProperty("name", ""));
      
      
      Map<Long, Source> refIds = new HashMap<> ();
      
      for (Relationship relationship : spiNode.getRelationships(
          MetabolicModelRelationshipType.has_crossreference_to)) {
        Node cpdNode = relationship.getOtherNode(spiNode);
        
        Source source = Source.valueOf(relationship.getProperty("source", Source.Undefined.toString()).toString());
//        System.out.println("\t" + cpdNode.getProperty("entry") + "\t" + source);
        
        refIds.put(cpdNode.getId(), source);
      }
      
      
      
      if (!refIds.isEmpty()) {
        Map<Set<Long>, Set<Long>> exp = 
            biodbService.expandReferences(refIds.keySet(), integrationSetId);
        for (Set<Long> k : exp.keySet()) {
          for (long id : k) {
            if (ucompIds.contains(id)) {
              if (!result.containsKey(id)) {
                result.put(id, new HashSet<Long> ());
              }
              result.get(id).add(spiNode.getId());
            }
//            Node cpdNode = graphDataService.getNodeById(id);
//            System.out.println("\t" + cpdNode.getProperty("entry") + "\t" + refIds.get(id));
          }
          for (long id : exp.get(k)) {
            if (!k.contains(id)) {
              if (ucompIds.contains(id)) {
                if (!result.containsKey(id)) {
                  result.put(id, new HashSet<Long> ());
                }
                result.get(id).add(spiNode.getId());
              }
              
//              Node cpdNode = graphDataService.getNodeById(id);
              Source source = refIds.get(id);
              if (source != null) {
//                System.out.println("\t" + cpdNode.getProperty("entry") + "\tConflict");
              } else {
//                System.out.println("\t" + cpdNode.getProperty("entry") + "\tIntegration");
              }
              
            }
          }
        }
//        System.out.println(exp);
      }
    }
    
 
    
    return result;
  }
}
