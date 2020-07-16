package pt.uminho.sysbio.biosynthframework.integration.neo4j;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosModelSpeciesNode;
import pt.uminho.sysbio.biosynthframework.util.GraphUtils;

public class Neo4jAnnotationPropagation {
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jAnnotationPropagation.class);
  
  private final BiodbGraphDatabaseService service;
  
  public Neo4jAnnotationPropagation(BiodbGraphDatabaseService service) {
    this.service = service;
  }
  
  public List<Set<Long>> generateMetaboliteSetsFromModelSpecies(int minScore) {
    UndirectedGraph<Long, Object> g = new SimpleGraph<>(Object.class);
    
    for (Node n : service.listNodes(MetabolicModelLabel.MetaboliteSpecie)) {
      Set<Long> refSet = new HashSet<>();
      for (Relationship r : n.getRelationships(
          MetabolicModelRelationshipType.has_crossreference_to, Direction.OUTGOING)) {
        String users = (String) r.getProperty("authors", null);
        Map<String, Integer> scores = BiosModelSpeciesNode.parse(users);
        int maxScore = 0;
        for (int s : scores.values()) {
          if (s > maxScore) {
            maxScore = s;
          }
        }

        if (!scores.isEmpty() && maxScore >= minScore) {
          refSet.add(r.getOtherNode(n).getId());
        }
      }

      if (refSet.size() > 1) {
        GraphUtils.addConnectedSet(g, refSet);
      }
    }
    
    return GraphUtils.getConnectedComponents(g);
  }
  
  public void propagate() {

  }
  
  public static void splash(Set<BiodbMetaboliteNode> cpdNodes, BiodbGraphDatabaseService service) {
    Set<Node> unodes = new HashSet<>();

    //collect unodes
    for (BiodbMetaboliteNode cpdNode : cpdNodes) {
      Relationship r = cpdNode.getSingleRelationship(
          IntegrationRelationshipType.has_universal_metabolite, Direction.OUTGOING);
      if (r != null) {
        unodes.add(r.getOtherNode(cpdNode));
      }
    }

    if (unodes.isEmpty()) {
      //create
      logger.info("CREATE UNIVERSAL METABOLITE");
      Node unode = service.createNode(CurationLabel.UniversalMetabolite);
      for (BiodbMetaboliteNode cpdNode : cpdNodes) {
        logger.info("ADD METABOLITE {} <- {}", unode, cpdNode);
        Relationship r = cpdNode.createRelationshipTo(unode, IntegrationRelationshipType.has_universal_metabolite);
        Neo4jUtils.setCreatedTimestamp(r);
        Neo4jUtils.setUpdatedTimestamp(r);
      }
    } else if (unodes.size() == 1) {
      Node unode = unodes.iterator().next();
      for (BiodbMetaboliteNode cpdNode : cpdNodes) {
        if (!Neo4jUtils.exitsRelationshipBetween(unode, cpdNode, Direction.BOTH)) {
          logger.info("ADD METABOLITE {} <- {}", unode, cpdNode);
          Relationship r = cpdNode.createRelationshipTo(unode, IntegrationRelationshipType.has_universal_metabolite);
          Neo4jUtils.setCreatedTimestamp(r);
          Neo4jUtils.setUpdatedTimestamp(r);
        }
      }
    } else {
      Iterator<Node> it = unodes.iterator();
      //merge
      Node unode = it.next();
      for (BiodbMetaboliteNode cpdNode : cpdNodes) {
        if (!Neo4jUtils.exitsRelationshipBetween(unode, cpdNode, Direction.BOTH)) {
          logger.info("ADD METABOLITE {} <- {}", unode, cpdNode);
          Relationship r = cpdNode.createRelationshipTo(unode, IntegrationRelationshipType.has_universal_metabolite);
          Neo4jUtils.setCreatedTimestamp(r);
          Neo4jUtils.setUpdatedTimestamp(r);
        }
      }
      while (it.hasNext()) {
        logger.warn("DELETE UNIVERSAL METABOLITE");
        Node n = it.next();
        for (Relationship r : n.getRelationships()) {
          logger.warn("DELETE UNIVERSAL METABOLITE LINK");
          r.delete();
        }
        logger.warn("DELETE UNIVERSAL METABOLITE NODE");
        n.delete();
      }
    }
  }
  
  public void aaa(int minScore) {
    List<Set<Long>> ccList = generateMetaboliteSetsFromModelSpecies(minScore);

    for (Set<Long> cc : ccList) {
      Set<BiodbMetaboliteNode> cpdNodes = new HashSet<>();
      for (long cpdId : cc) {
        BiodbMetaboliteNode cpdNode = service.getMetabolite(cpdId);
        cpdNodes.add(cpdNode);
      }
      splash(cpdNodes, service);
    }
  }
}
