package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;

public class BiosMetabolicModelNode extends BiodbEntityNode {

  public BiosMetabolicModelNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public Set<BiodbEntityNode> getModelEntity(MetabolicModelRelationshipType r) {
    Set<BiodbEntityNode> result = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, r)) {
      result.add(new BiodbEntityNode(n, databasePath));
    }
    return result;
  }
  
  public Set<BiodbEntityNode> getModelCompartments() {
    return getModelEntity(MetabolicModelRelationshipType.has_model_compartment);
  }
  
  public Set<BiosModelSpeciesNode> getMetaboliteSpecies() {
    Set<BiosModelSpeciesNode> result = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.has_metabolite_species)) {
      result.add(new BiosModelSpeciesNode(n, databasePath));
    }
    return result;
  }
  
  public Set<BiosModelReactionNode> getModelReactions() {
    Set<BiosModelReactionNode> result = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.has_model_reaction)) {
      result.add(new BiosModelReactionNode(n, databasePath));
    }
    return result;
  }
  
  public Set<BiodbEntityNode> getModelGenes() {
    return getModelEntity(MetabolicModelRelationshipType.has_gpr_gene);
  }
  
  public Set<BiodbEntityNode> getModelSubsystems() {
    return getModelEntity(MetabolicModelRelationshipType.has_subsystem);
  }
}