package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;

public class BiosTaxonomyNode extends BiodbEntityNode {

  public BiosTaxonomyNode(Node node, String databasePath) {
    super(node, databasePath);
  }

  public String getName() {
    return (String) this.getProperty("scientific_name", null);
  }
  
  public Long getTaxId() {
    return (Long) this.getProperty("tax_id", null);
  }
  
  public BiosTaxonomyNode getParent() {
    Relationship r = this.getSingleRelationship(GenericRelationship.has_parent_taxonomy, Direction.OUTGOING);
    if (r == null) {
      return null;      
    }
    return new BiosTaxonomyNode(r.getOtherNode(this), databasePath);
  }
  
  public List<String> getLineage() {
    List<String> lineage = new ArrayList<>();
    lineage.add(getName());
    
    BiosTaxonomyNode parent = getParent();
    
    while (parent != null) {
      lineage.add(parent.getName());
      parent = parent.getParent();
    }
    
    return lineage;
  }
}
