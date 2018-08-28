package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;

public class BiosModelCompartmentNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosModelCompartmentNode.class);
  
  public BiosModelCompartmentNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public String getSid() {
    return (String) this.getProperty("id", null);
  }
  
  public int getSize() {
    return Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.in_compartment).size();
  }
  
  public Set<BiosModelSpeciesNode> getMetaboliteSpecies() {
    Set<BiosModelSpeciesNode> result = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.in_compartment)) {
      if (n.hasLabel(MetabolicModelLabel.MetaboliteSpecie)) {
        result.add(new BiosModelSpeciesNode(n, databasePath));
      }
    }
    return result;
  }
  
  public void setUniversalCompartment(BiosUniversalCompartmentNode ucmpNode) {
    //get previous BiosUniversalCompartmentNode
    Relationship ur = this.getSingleRelationship(IntegrationRelationshipType.has_universal_compartment, Direction.BOTH);
    Node prev = null;
    //if previous exists
    if (ur != null) {
      prev = ur.getOtherNode(this);
      //same node do nothing
      if (prev.getId() == ucmpNode.getId()) {
        return;
      }
      //otherwise delete
      logger.warn("delete previous {}", prev.getAllProperties());
      ur.delete();
    }
    
    //at this point either ur is null or was deleted
    ur = this.createRelationshipTo(ucmpNode, IntegrationRelationshipType.has_universal_compartment);
    Neo4jUtils.setTimestamps(ur);
  }
  
  public BiosUniversalCompartmentNode getUniversalCompartment() {
    Relationship ur = this.getSingleRelationship(IntegrationRelationshipType.has_universal_compartment, Direction.BOTH);
    if (ur == null) {
      return null;
    }
    
    return new BiosUniversalCompartmentNode(ur.getOtherNode(this), databasePath);
  }
}
