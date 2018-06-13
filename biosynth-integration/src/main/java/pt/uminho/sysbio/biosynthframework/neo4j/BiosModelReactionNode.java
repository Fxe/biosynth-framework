package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;

public class BiosModelReactionNode extends BiodbEntityNode {

  public BiosModelReactionNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public String getSid() {
    return (String) this.getProperty("id", null);
  }
  
  public BiodbEntityNode getGpr() {
    Relationship r = this.getSingleRelationship(MetabolicModelRelationshipType.has_gpr, Direction.BOTH);
    if (r != null) {
      return new BiodbEntityNode(r.getOtherNode(node), databasePath);
    }
    
    return null;
  }
  
  public Set<BiodbReactionNode> getReferences() {
    Set<BiodbReactionNode> references = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      Node other = r.getOtherNode(node);
      references.add(new BiodbReactionNode(other, databasePath));
    }
    return references;
  }

  //stoich etc
}
