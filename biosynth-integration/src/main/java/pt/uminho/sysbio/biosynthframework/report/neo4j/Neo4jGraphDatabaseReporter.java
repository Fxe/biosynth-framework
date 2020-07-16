package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class Neo4jGraphDatabaseReporter extends AbstractNeo4jReporter {

  public Neo4jGraphDatabaseReporter(GraphDatabaseService service) {
    super(service);
  }
  
  public static String getDatabase(Set<String> labels) {
    Set<String> b = new HashSet<>(labels);
    if (labels.contains(GlobalLabel.Metabolite.toString())) {
      b.remove(GlobalLabel.Metabolite.toString());
    }
    
    return StringUtils.join(b, "|");
  }
  
  public void proxy(MetaboliteMajorLabel database) {
    Map<MetaboliteMajorLabel, Integer> counter = new HashMap<> ();
    for (Node node : service.listNodes(database)) {
      
      if (node.hasLabel(GlobalLabel.Metabolite)) {
        
        boolean proxy = (boolean) node.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
        if (proxy) {
//          System.out.println(Neo4jUtils.getPropertiesMap(node));
//          for (Relationship r : node.getRelationships()) {
//            System.out.println(r.getType().name());
//          }
          System.out.println(node.getProperty("entry"));
          for (Node n : Neo4jUtils.collectNodeRelationshipNodes(node, MetaboliteRelationshipType.has_crossreference_to)) {
            String ml = (String) n.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
            MetaboliteMajorLabel db = MetaboliteMajorLabel.valueOf(ml);
            CollectionUtils.increaseCount(counter, db, 1);
          }
        }
      } else if (node.hasLabel(GlobalLabel.Reaction)) {
        BiodbEntityNode enode = new BiodbEntityNode(node, null);
        if (enode.isProxy()) {
          System.out.println(enode.getEntry());
          for (Node n : Neo4jUtils.collectNodeRelationshipNodes(node, MetaboliteRelationshipType.has_crossreference_to)) {
            String ml = (String) n.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
            System.out.println(ml);
//            MetaboliteMajorLabel db = MetaboliteMajorLabel.valueOf(ml);
//            CollectionUtils.increaseCount(counter, db, 1);
          }
        }
      } else if (node.hasLabel(GlobalLabel.MetabolicPathway)) {
        
      } else {
        System.out.println(Neo4jUtils.getLabels(node) + " " + Neo4jUtils.getPropertiesMap(node));
        
      }
    }
    
    System.out.println(DataUtils.toString(counter, "\n", "\t"));
    
  }
  
  public Dataset<?, ?, ?> report() {
    Dataset<String, String, Object> dataset = new Dataset<>();
    
    Map<Set<String>, Integer> count = new HashMap<> ();
    
    Map<String, Integer> mproxy = new HashMap<> ();
    Map<String, Integer> mnotproxy = new HashMap<> ();
    
    Map<String, Integer> metabolites = new HashMap<> ();
    Map<String, Integer> reactions = new HashMap<> ();
    Map<String, Integer> others = new HashMap<> ();
    
    for (Node n : service.getAllNodes()) {
      Set<String> labels = Neo4jUtils.getLabelsAsString(n);
      if (n.hasLabel(GlobalLabel.Metabolite)) {
        boolean proxy = (boolean) n.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
        if (proxy) {
          CollectionUtils.increaseCount(mproxy, getDatabase(labels), 1);
        } else {
          CollectionUtils.increaseCount(mnotproxy, getDatabase(labels), 1);
        }
      }
      CollectionUtils.increaseCount(count, labels, 1);
    }
    
    for (Set<String> k : count.keySet()) {
      Set<String> b = new HashSet<>(k);
      if (k.contains(GlobalLabel.Metabolite.toString())) {
        String db = getDatabase(k);
        metabolites.put(db, count.get(k));
        Integer proxy = mproxy.get(db);
        Integer nproxy = mnotproxy.get(db);
        if (proxy == null) {
          proxy = 0;
        }
        if (nproxy == null) {
          nproxy = 0;
        }
        dataset.add(db, "proxy", proxy);
        dataset.add(db, "loaded", nproxy);
      } else if (k.contains(GlobalLabel.Reaction.toString())) {
        b.remove(GlobalLabel.Reaction.toString());
        reactions.put(StringUtils.join(b, "|"), count.get(k));
      } else {
        others.put(StringUtils.join(b, "|"), count.get(k));
      }
    }
    
    DataUtils.printData(dataset.dataset, "Database");
    
    System.out.println("METABOLITES:");
    System.out.println(DataUtils.toString(metabolites, "\n", "\t"));
    System.out.println("REACTIONS:");
    System.out.println(DataUtils.toString(reactions, "\n", "\t"));
    System.out.println("OTHERS:");
    System.out.println(DataUtils.toString(others, "\n", "\t"));
    
    return null;
  }
  
  public Dataset<MetaboliteMajorLabel, String, Integer> reportAttributes() {
    Dataset<MetaboliteMajorLabel, String, Integer> result = new Dataset<>();
    
    for (BiodbMetaboliteNode cpdNode : service.listMetabolites()) {
      if (!cpdNode.isProxy()) {
        MetaboliteMajorLabel db = cpdNode.getDatabase();
        if (!result.dataset.containsKey(db)) {
          result.dataset.put(db, new HashMap<String, Integer>());
        }
        Map<String, Integer> count = result.dataset.get(db);
        for (String p : cpdNode.getAllProperties().keySet()) {
          Object o = cpdNode.getProperty(p);
          if (o != null) {
            if (o instanceof String) {
              if (!DataUtils.empty(o)) {
                CollectionUtils.increaseCount(count, p, 1);
              }
            } else {
              CollectionUtils.increaseCount(count, p, 1);
            }
          }
        }
      }
    }
    
    return result;
  }

  public Dataset<MetaboliteMajorLabel, String, Integer> reportProxies() {
    Dataset<MetaboliteMajorLabel, String, Integer> result = new Dataset<>();
    
    for (BiodbMetaboliteNode cpdNode : service.listMetabolites()) {
      MetaboliteMajorLabel db = cpdNode.getDatabase();
      if (!result.dataset.containsKey(db)) {
        result.dataset.put(db, new HashMap<String, Integer>());
      }
      Map<String, Integer> count = result.dataset.get(db);
      if (!cpdNode.isProxy()) {
        CollectionUtils.increaseCount(count, "entity", 1);
      } else {
        CollectionUtils.increaseCount(count, "proxy", 1);
      }
    }
    
    return result;
  }
}
