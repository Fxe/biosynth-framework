package pt.uminho.sysbio.biosynthframework.neo4j;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;

public class BiosVersionNode extends BiodbEntityNode {

  public BiosVersionNode(Node node, String databasePath) {
    super(node, databasePath);
    if (!node.hasLabel(GlobalLabel.VERSION)) {
      throw new IllegalArgumentException("invalid node, expected VERSION node");
    }
  }

  public BiosVersionNode getPreviousVersion() {
    Relationship relationship = 
        this.getSingleRelationship(GenericRelationship.has_version, Direction.OUTGOING);
    if (relationship != null) {
      Node versionNode = relationship.getOtherNode(this);
      return new BiosVersionNode(versionNode, databasePath);
    }
    return null;
  }
  
//  public BiosVersionNode getNextVersion() {
//    Relationship relationship = 
//        this.getSingleRelationship(GenericRelationship.has_version, Direction.INCOMING);
//    if (relationship != null) {
//      Node versionNode = relationship.getOtherNode(this);
//      return new BiosVersionNode(versionNode, databasePath);
//    }
//    return null;
//  }
  
  public BiodbEntityNode getLatestVersion() {
    Relationship next = 
        this.getSingleRelationship(GenericRelationship.has_version, Direction.INCOMING);
    Node nextNode = next.getOtherNode(this);
    while (next != null) {
      next = nextNode.getSingleRelationship(GenericRelationship.has_version, Direction.INCOMING);
    }
    return new BiodbEntityNode(nextNode, databasePath);
  }
  
  public String getVersion() {
    return (String) this.getProperty(Neo4jDefinitions.ENTITY_VERSION, null);
  }
}
