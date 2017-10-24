package pt.uminho.sysbio.biosynth.integration.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

/**
 * Reports the attributes of the nodes (quantity + type)
 * @author Filipe Liu
 *
 */
public class NodeAttributeReporter implements GlobalReporter {
  
  private final MetaboliteMajorLabel[] databases;
  private final BiodbGraphDatabaseService graphDatabaseService;
  
  public NodeAttributeReporter(MetaboliteMajorLabel[] databases,
                               GraphDatabaseService graphDatabaseService) {
    this.databases = databases;
    this.graphDatabaseService = new BiodbGraphDatabaseService(graphDatabaseService);
  }

  @Override
  public void generateReport() {
    for (MetaboliteMajorLabel db : databases) {
      Map<String, Set<String>> attributeType = new HashMap<>();
      Map<String, Integer> attributeCount = new HashMap<>();
      Map<String, Set<String>> attributeType2 = new HashMap<>();
      Map<String, Integer> attributeCount2 = new HashMap<>();
      int total = 0;
      for (Node node : graphDatabaseService.listNodes(db)) {
  //      Node node = graphDataService.getNodeById(1321992L);
        if (node.hasLabel(GlobalLabel.Metabolite)) {
          Map<String, String> a1 = extractAttributes(node);
          Map<String, String> a2 = extractAttributes2(node);
          total++;
          for (String s : a1.keySet()) {
            CollectionUtils.increaseCount(attributeCount, s, 1);
          }
          joinMap(a1, attributeType);
          for (String s : a2.keySet()) {
            CollectionUtils.increaseCount(attributeCount2, s, 1);
          }
          joinMap(a2, attributeType2);
        } else if (node.hasLabel(GlobalLabel.Reaction)) {
          
        } else if (node.hasLabel(GlobalLabel.SubcellularCompartment) ||
                   node.hasLabel(GlobalLabel.MetabolicPathway)) {
          
        } else {
          //cry a lot
          System.out.println(Neo4jUtils.getLabels(node));
        }
      }
      System.out.println(db);
      System.out.println(total);
      for (String s : attributeType.keySet()) {
        String attribute = s;
        Set<String> type = attributeType.get(s);
        int count = attributeCount.get(s);
        Set<String> type2 = attributeType2.get(s);
        Integer count2 = attributeCount2.get(s);
        System.out.println(attribute + " " + type + " " + count + " " + type2 + " " + count2);
      }
//      System.out.println(attributeType);
//      System.out.println(attributeCount);
    }
  }
  
  public static<K, V> void joinMap(Map<K, V> m, Map<K, Set<V>> mm) {
    for (K k : m.keySet()) {
      V v = m.get(k);
      
      if (!mm.containsKey(k)) {
        mm.put(k, new HashSet<V> ());
      }
      
      mm.get(k).add(v);
    }
  }
  
  public static Map<String, String> extractAttributes(Node node) {
    Map<String, String> attributeTypeMap = new HashMap<> ();
    for (String s : node.getPropertyKeys()) {
      Object o = node.getProperty(s);
//      System.out.println(s + " " + o.getClass().getSimpleName());
      attributeTypeMap.put(s, o.getClass().getSimpleName());
    }
    
    return attributeTypeMap;
  }
  
  
  /**
   * Ignore empty strings
   * @param node
   * @return
   */
  public static Map<String, String> extractAttributes2(Node node) {
    Map<String, String> attributeTypeMap = new HashMap<> ();
    for (String s : node.getPropertyKeys()) {
      Object o = node.getProperty(s);
//      System.out.println(s + " " + o.getClass().getSimpleName());
      
      if (o instanceof String) {
        String v = (String) o;
        if (!v.trim().isEmpty()) {
          attributeTypeMap.put(s, o.getClass().getSimpleName());
        }
      } else {
        attributeTypeMap.put(s, o.getClass().getSimpleName());
      }
    }
    
    return attributeTypeMap;
  }
}
