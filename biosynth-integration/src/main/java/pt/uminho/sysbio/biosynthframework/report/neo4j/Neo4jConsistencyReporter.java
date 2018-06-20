package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.Iterators;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class Neo4jConsistencyReporter extends AbstractNeo4jReporter {

  public Neo4jConsistencyReporter(GraphDatabaseService service) {
    super(service);
  }

  public boolean isMetabolite(Node n) {
    return n.hasLabel(GlobalLabel.Metabolite) &&
        Neo4jUtils.hasAnyLabel(n, MetaboliteMajorLabel.values());
  }

  public boolean isReaction(Node n) {
    return n.hasLabel(GlobalLabel.Reaction) &&
        Neo4jUtils.hasAnyLabel(n, ReactionMajorLabel.values());
  }

  public boolean isEntity(Node n) {
    return isMetabolite(n) || 
        isReaction(n) ||
        n.hasLabel(GlobalLabel.MetabolicModel) ||
        Neo4jUtils.hasAnyLabel(n, MetabolicModelLabel.values());
  }

  public boolean checkEntity(Node n) {
    return n.hasProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT) &&
        n.hasProperty(Neo4jDefinitions.PROXY_PROPERTY) && 
        n.hasProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
  }

  public boolean checkProperty(Node n) {
    return n.hasProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT) &&
        n.hasProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
  }

  public boolean checkMetabolite(Node n) {
    return checkEntity(n) &&
        n.hasLabel(GlobalLabel.Metabolite);
  }

  public boolean checkReaction(Node n) {
    return checkEntity(n) &&
        n.hasLabel(GlobalLabel.Reaction);
  }

  public boolean isProperty(Node n) {
    return n.hasLabel(GlobalLabel.MetaboliteProperty) ||
        n.hasLabel(GlobalLabel.ReactionProperty);
  }

  public static void print(Node n) {
    System.out.println(Neo4jUtils.getPropertiesMap(n));
    for (Relationship r : n.getRelationships()) {
      Node other = r.getOtherNode(n);
      System.out.println("\t" + r.getType().name() + "\t" + Neo4jUtils.getPropertiesMap(other));
    }
  }

  public void fixHmdb() {
    for (MetaboliteMajorLabel database : MetaboliteMajorLabel.values()) {
      for (Node n : service.listNodes(database)) { 
        if (!(n.hasLabel(GlobalLabel.Metabolite) || n.hasLabel(GlobalLabel.Reaction))) {
          if (n.hasLabel(MetaboliteMajorLabel.HMDB) && n.hasProperty("secondary_accession")) {
            n.addLabel(GlobalLabel.Metabolite);
            System.out.println("FIX " + Neo4jUtils.getLabelsAsString(n) + " " + Neo4jUtils.getPropertiesMap(n));
          } else {
            System.err.println(Neo4jUtils.getLabelsAsString(n) + " " + Neo4jUtils.getPropertiesMap(n));
          }
        }
      }
    }
  }
  
  public static Map<String, Integer> reportHmdbSecondaryAccessions(GraphDatabaseService graphDatabaseService) {
    
    Map<String, Integer> result = new HashMap<>();
    
    for (Node hmdbNode : Iterators.asIterable(
        graphDatabaseService.findNodes(MetaboliteMajorLabel.HMDB))) {
      boolean proxy = (boolean) hmdbNode.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
      if (proxy) {
        CollectionUtils.increaseCount(result, "proxy", 1);
      } else {
        Boolean secondaryAccession = (Boolean) hmdbNode.getProperty("secondary_accession", null);
        if (secondaryAccession == null) {
          CollectionUtils.increaseCount(result, "regular", 1);
        } else if (secondaryAccession) {
          CollectionUtils.increaseCount(result, "secondaryAccession", 1);
        } else {
          CollectionUtils.increaseCount(result, "unknown", 1);
        }
//        System.out.println(Neo4jUtils.getPropertiesMap(hmdbNode).keySet());
      }
    }
    
    return result;
  }
  
  public void report() {

    //  RestBiocycMetaboliteDaoImpl dao = FliuThesisInstanses.getMetacycMetaboliteDao();

    for (MetaboliteMajorLabel database : MetaboliteMajorLabel.values()) {
      for (Node n : service.listNodes(database)) { 
        if (!(n.hasLabel(GlobalLabel.Metabolite) || n.hasLabel(GlobalLabel.Reaction))) {
          System.err.println(Neo4jUtils.getLabelsAsString(n) + " " + n.getAllProperties());
        }
      }
    }

    for (Node n : service.getAllNodes()) {
//      Set<Label> labels = Neo4jUtils.getLabels(n);
      if (isEntity(n) ) {
        if (isMetabolite(n)) {
          checkMetabolite(n);
          if (n.hasLabel(MetaboliteMajorLabel.MetaCyc)) {
            String entry = (String) n.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT);
            boolean proxy = (boolean) n.getProperty(Neo4jDefinitions.PROXY_PROPERTY);
            if (proxy) {
              System.out.println("FETCH PROXY " + entry);
              //            dao.getMetaboliteByEntry(entry);
            }
            if (!entry.startsWith("META:")) {
              print(n);
            }
          }
          if (n.hasLabel(MetaboliteMajorLabel.MetaCyc) && 
              !n.hasLabel(GlobalLabel.BioCyc)) {
            n.addLabel(GlobalLabel.BioCyc);
            print(n);
          }
        } else if (isReaction(n)) {
          checkReaction(n);
        } else {

        }
      } else if (isProperty(n)) {
        checkProperty(n);
      } else {
        System.err.println(Neo4jUtils.getLabelsAsString(n) + " " + n.getAllProperties());
      }
    }
  }
}
