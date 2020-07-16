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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
//import pt.uminho.sysbio.biosynthframework.Source;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class TableModelIntegration<T> {
  
  private static final Logger logger = LoggerFactory.getLogger(TableModelIntegration.class);
  
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
  
  public Dataset<String, String, String> remap(Dataset<String, String, String> dataset, String attribute,
      BiodbGraphDatabaseService service) {
    Dataset<String, String, String> table = new Dataset<>();
    
    for (String modelEntry : dataset.keySet()) {
      BiosMetabolicModelNode modelNode = service.getMetabolicModel(modelEntry);
      if (modelNode != null) {
        for (String col : dataset.getColumns()) {
          table.add(modelEntry, col, "");
          if (!DataUtils.empty(dataset.get(modelEntry).get(col))) {
            String sid = dataset.get(modelEntry).get(col);
            BiosModelSpeciesNode spiNode = modelNode.getMetaboliteSpecie(sid);
            if (spiNode != null) {
              String value = spiNode.getProperty(attribute, "").toString();
              table.add(modelEntry, col, value);              
            } else {
              logger.warn("not found {} - {}", modelEntry, sid);
            }
          }
        }
      }
    }
    
    return table;
  }
  
  public Dataset<String, String, String> report(Set<String> models, Set<String> metabolites, MetaboliteMajorLabel database,
      SubcellularCompartment scmp, int minScore,
      BiodbGraphDatabaseService service) {
    Dataset<String, String, String> table = new Dataset<>();
    
    for (String modelEntry : models) {
      BiosMetabolicModelNode modelNode = service.getMetabolicModel(modelEntry);
      if (modelNode != null) {
        for (String a : metabolites) {
          table.add(modelEntry, a, "");
        }
        for (BiosModelSpeciesNode spiNode : modelNode.getMetaboliteSpecies()) {
          SubcellularCompartment scmp_ = spiNode.getSubcellularCompartment();
          if (scmp.equals(scmp_)) {
            Set<BiodbMetaboliteNode> refs = spiNode.getReferences(database);
            for (BiodbMetaboliteNode ref : refs) {
              Integer score = spiNode.getAnnotationScore(ref);
              if (metabolites.contains(ref.getEntry()) && score != null && score >= minScore) {
                table.add(modelEntry, ref.getEntry(), spiNode.getSid());
              }
            }
          }
        }
      }
    }
    
    return table;
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
      Map<Long, Set<Long>> species = lookupSpecies(modelEntry, ucompIds, 5, false);
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
  
  public Map<Long, Set<Long>> lookupSpecies(String modelEntry, Set<Long> ucompIds, int minScore, boolean expand) {
//    BiodbService service = new Neo4jBiodbDataServiceImpl(
//        DATA_NEO4J_DAO, META_NEO4J_DAO, CURA_NEO4J_DAO, INTEGRATION_SET);
    
    Node modelNode_ = Neo4jUtils.getNodeByEntry(GlobalLabel.MetabolicModel, modelEntry, graphDataService);
    BiosMetabolicModelNode modelNode = new BiosMetabolicModelNode(modelNode_, null);
    
    Map<Long, Set<Long>> result = new HashMap<> ();
    
    for (BiosModelSpeciesNode spiNode : modelNode.getMetaboliteSpecies()) {

      Map<Long, String> refIds = new HashMap<> ();
      
      for (BiodbMetaboliteNode cpdNode : spiNode.getReferences()) {
        Integer score = spiNode.getAnnotationScore(cpdNode);
        if (score == null) {
          score = -1;
        }
        
        if (score >= minScore) {
          Map<String, Integer> scores = spiNode.getAnnotationUsers(cpdNode);
          String source = Joiner.on(";").join(scores.keySet());
          refIds.put(cpdNode.getId(), source);
        }
      }
//      for (Relationship relationship : spiNode.getRelationships(
//          MetabolicModelRelationshipType.has_crossreference_to)) {
//        Node cpdNode = relationship.getOtherNode(spiNode);
//        
//        Source source = Source.valueOf(relationship.getProperty("source", Source.Undefined.toString()).toString());
////        System.out.println("\t" + cpdNode.getProperty("entry") + "\t" + source);
//        
//        refIds.put(cpdNode.getId(), source);
//      }
      
      
      
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
//              Source source = refIds.get(id);
//              if (source != null) {
////                System.out.println("\t" + cpdNode.getProperty("entry") + "\tConflict");
//              } else {
////                System.out.println("\t" + cpdNode.getProperty("entry") + "\tIntegration");
//              }
              
            }
          }
        }
//        System.out.println(exp);
      }
    }
    
 
    
    return result;
  }
}
