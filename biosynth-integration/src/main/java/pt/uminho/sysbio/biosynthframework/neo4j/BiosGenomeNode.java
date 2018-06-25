package pt.uminho.sysbio.biosynthframework.neo4j;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;

public class BiosGenomeNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosGenomeNode.class);
  
  public BiosGenomeNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public boolean clearTaxonomy() {
    Relationship r = this.getSingleRelationship(GenericRelationship.has_taxonomy, Direction.BOTH);
    if (r != null) {
      r.delete();
      return true;
    }
    return false;
  }

  public Long setTaxonomy(Node taxNode) {
    if (taxNode == null) {
      return null;
    }
    
    if (Neo4jUtils.exitsRelationshipBetween(this, taxNode, Direction.BOTH)) {
      return null;
    }
    
    clearTaxonomy();
    
    if (!Neo4jUtils.exitsRelationshipBetween(taxNode, this, Direction.BOTH)) {
      logger.info("[LINK] [{}] -[{}]-> [{}]", this, GenericRelationship.has_taxonomy, taxNode);
      Relationship r = this.createRelationshipTo(taxNode, GenericRelationship.has_taxonomy);
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
      return r.getId();
    }
    
    return null;
  }
  
  public Node getTaxonomy() {
    Node taxNode = null;
    Relationship r = this.getSingleRelationship(GenericRelationship.has_taxonomy, Direction.BOTH);
    if (r != null) {
      taxNode = r.getOtherNode(this);
    }
    
    return taxNode;
  }
}
