package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;

public class BiosUniversalReactionNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosUniversalReactionNode.class);
  
  public BiosUniversalReactionNode(Node node, String databasePath) {
    super(node, databasePath);
    if (!this.node.hasLabel(CurationLabel.UniversalReaction)) {
      throw new IllegalArgumentException("Invalid UniversalReaction node: " + node);
    }
  }

  public Set<BiodbReactionNode> getReactions() {
    Set<BiodbReactionNode> reactions = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, IntegrationRelationshipType.has_universal_reaction)) {
      reactions.add(new BiodbReactionNode(n, databasePath));
    }
    return reactions;
  }
  
  public Set<BiodbReactionNode> getReactions(ReactionMajorLabel database) {
    Set<BiodbReactionNode> reactions = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, IntegrationRelationshipType.has_universal_reaction)) {
      if (n.hasLabel(database)) {
        reactions.add(new BiodbReactionNode(n, databasePath));        
      }
    }
    return reactions;
  }
  
  public Set<Long> getReactionIds() {
    Set<Long> ids = new HashSet<>();
    for (BiodbReactionNode rxn : this.getReactions()) {
      ids.add(rxn.getId());
    }
    return ids;
  }
  
  public Long addReaction(BiodbReactionNode rxnNode) {
    Relationship r = rxnNode.getSingleRelationship(
        IntegrationRelationshipType.has_universal_reaction, Direction.OUTGOING);
    if (r == null) {
      logger.info("ADD ENTITY TO UNODE {} <-[{}]- {}", this, IntegrationRelationshipType.has_universal_reaction, rxnNode);
      r = rxnNode.createRelationshipTo(this, IntegrationRelationshipType.has_universal_reaction);
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
    }
    return r.getId();
  }
  
  public Long deleteReaction(BiodbReactionNode rxnNode) {
    Relationship r = rxnNode.getSingleRelationship(
        IntegrationRelationshipType.has_universal_reaction, Direction.OUTGOING);
    if (r == null || r.getOtherNode(rxnNode).getId() != this.getId()) {
      return null;
    }
    logger.info("DELETE ENTITY TO UNODE {} <-[{}]- {}", this, IntegrationRelationshipType.has_universal_reaction, rxnNode);
    r.delete();
    return r.getId();
  }
  
  @Override
  public String toString() {
    return String.format("URxnNode[%d]", getId());
  }
}
