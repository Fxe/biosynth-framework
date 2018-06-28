package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.curation.CurationLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;

public class BiosUniversalReactionNode extends BiodbEntityNode {

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
}
