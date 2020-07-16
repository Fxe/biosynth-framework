package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;

public class BiosUniversalMetaboliteNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosUniversalMetaboliteNode.class);
  
  public BiosUniversalMetaboliteNode(Node node, String databasePath) {
    super(node, databasePath);
    if (!this.node.hasLabel(CurationLabel.UniversalMetabolite)) {
      throw new IllegalArgumentException("Invalid UniversalMetabolite node: " + node);
    }
  }

  public Set<BiodbMetaboliteNode> getMetabolites() {
    Set<BiodbMetaboliteNode> metabolites = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, IntegrationRelationshipType.has_universal_metabolite)) {
      metabolites.add(new BiodbMetaboliteNode(n, databasePath));
    }
    return metabolites;
  }
  
  public Set<BiodbMetaboliteNode> getMetabolites(MetaboliteMajorLabel database) {
    Set<BiodbMetaboliteNode> metabolites = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, IntegrationRelationshipType.has_universal_metabolite)) {
      if (n.hasLabel(database)) {
        metabolites.add(new BiodbMetaboliteNode(n, databasePath));        
      }
    }
    return metabolites;
  }
  
  public Long addMetabolite(BiodbMetaboliteNode cpdNode) {
    Relationship r = cpdNode.getSingleRelationship(
        IntegrationRelationshipType.has_universal_metabolite, Direction.OUTGOING);
    if (r == null) {
      logger.info("ADD ENTITY TO UNODE {} <-[{}]- {}", this, IntegrationRelationshipType.has_universal_metabolite, cpdNode);
      r = cpdNode.createRelationshipTo(this, IntegrationRelationshipType.has_universal_metabolite);
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
    }
    return r.getId();
  }
  
  public Long deleteMetabolite(BiodbMetaboliteNode cpdNode) {
    Relationship r = cpdNode.getSingleRelationship(
        IntegrationRelationshipType.has_universal_metabolite, Direction.OUTGOING);
    if (r == null || r.getOtherNode(cpdNode).getId() != this.getId()) {
      return null;
    }
    logger.info("DELETE ENTITY TO UNODE {} <-[{}]- {}", this, IntegrationRelationshipType.has_universal_metabolite, cpdNode);
    r.delete();
    return r.getId();
  }

  public Set<Long> getMetaboliteIds() {
    Set<Long> ids = new HashSet<>();
    for (BiodbMetaboliteNode cpd : this.getMetabolites()) {
      ids.add(cpd.getId());
    }
    return ids;
  }
  
  public Set<BiosUniversalMetaboliteNode> getInstances() {
    Map<Long, Node> ids = new HashMap<>();
    for (BiodbMetaboliteNode cpdNode : getMetabolites()) {
      for (BiodbMetaboliteNode i : cpdNode.getInstances()) {
        Node n = i.getUniversalMetabolite();
        if (n != null) {
          ids.put(n.getId(), n);
        }
      }
    }
    
    Set<BiosUniversalMetaboliteNode> instances = new HashSet<>();
    for (long id : ids.keySet()) {
      instances.add(new BiosUniversalMetaboliteNode(ids.get(id), databasePath));
    }
    return instances;
  }
  
  @Override
  public String toString() {
    return String.format("UCpdNode[%d]", getId());
  }
}
