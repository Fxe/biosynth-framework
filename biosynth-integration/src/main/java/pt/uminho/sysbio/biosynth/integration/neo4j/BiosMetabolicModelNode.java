package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.util.Set;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class BiosMetabolicModelNode extends BiodbEntityNode {

  public BiosMetabolicModelNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public Set<Node> getMetaboliteSpecies() {
    Set<Node> result = 
        Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.has_metabolite_species);
    return result;
  }
  
  public Set<Node> getModelReactions() {
    Set<Node> result = 
        Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.has_model_reaction);
    return result;
  }
}
