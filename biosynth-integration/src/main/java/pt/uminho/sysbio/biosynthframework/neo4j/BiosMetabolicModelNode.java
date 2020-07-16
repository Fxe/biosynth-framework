package pt.uminho.sysbio.biosynthframework.neo4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class BiosMetabolicModelNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosMetabolicModelNode.class);
  
  public BiosMetabolicModelNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public<T extends BiodbEntityNode> Set<T> getModelEntity(MetabolicModelRelationshipType r, Class<T> clazz) {
    Set<T> result = new HashSet<>();
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, r)) {
      try {
        T o = clazz.getConstructor(Node.class, String.class).newInstance(n, databasePath);
        result.add(o);
      } catch (InstantiationException | IllegalAccessException | 
               IllegalArgumentException | InvocationTargetException | 
               NoSuchMethodException | SecurityException e) {
        e.printStackTrace();
      }
      
    }
    return result;
  }
  
  public Set<BiosModelCompartmentNode> getModelCompartments() {
    return getModelEntity(MetabolicModelRelationshipType.has_model_compartment, BiosModelCompartmentNode.class);
  }
  
  public BiosModelCompartmentNode getModelCompartment(SubcellularCompartment scmp) {
    BiosModelCompartmentNode result = null;
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, MetabolicModelRelationshipType.has_model_compartment)) {
      BiosModelCompartmentNode cmpNode = new BiosModelCompartmentNode(n, databasePath);
      BiosUniversalCompartmentNode ucmpNode = cmpNode.getUniversalCompartment();
      if (ucmpNode != null && scmp.equals(ucmpNode.getCompartment())) {
        result = cmpNode;
      }
    }
    
    return result;
  }
  
   public BiosModelSpeciesNode getMetaboliteSpecie(long id) {
    BiosModelSpeciesNode result = null;
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, MetabolicModelRelationshipType.has_metabolite_species)) {
      if (n.getId() == id) {
        result = new BiosModelSpeciesNode(n, databasePath);
      }
    }
    return result;
  }
  
  public BiosModelSpeciesNode getMetaboliteSpecie(String sid) {
    BiosModelSpeciesNode result = null;
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, MetabolicModelRelationshipType.has_metabolite_species)) {
      if (sid.equals(n.getProperty("id", null))) {
        result = new BiosModelSpeciesNode(n, databasePath);
      }
    }
    return result;
  }
  
  public BiosModelReactionNode getModelReaction(long id) {
    BiosModelReactionNode result = null;
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, MetabolicModelRelationshipType.has_model_reaction)) {
      if (n.getId() == id) {
        result = new BiosModelReactionNode(n, databasePath);
      }
    }
    return result;
  }
  
  public BiosModelReactionNode getModelReaction(String sid) {
    BiosModelReactionNode result = null;
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, MetabolicModelRelationshipType.has_model_reaction)) {
      if (sid.equals(n.getProperty("id", null))) {
        result = new BiosModelReactionNode(n, databasePath);
      }
    }
    return result;
  }
  
  public Set<BiosModelSpeciesNode> getMetaboliteSpecies() {
    return getModelEntity(MetabolicModelRelationshipType.has_metabolite_species, BiosModelSpeciesNode.class);
//    Set<BiosModelSpeciesNode> result = new HashSet<>();
//    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.has_metabolite_species)) {
//      result.add(new BiosModelSpeciesNode(n, databasePath));
//    }
//    return result;
  }
  
  public Set<BiosModelReactionNode> getModelReactions() {
    return getModelEntity(MetabolicModelRelationshipType.has_model_reaction, BiosModelReactionNode.class);
//    Set<BiosModelReactionNode> result = new HashSet<>();
//    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(this, MetabolicModelRelationshipType.has_model_reaction)) {
//      result.add(new BiosModelReactionNode(n, databasePath));
//    }
//    return result;
  }
  
  public Set<BiodbEntityNode> getModelGenes() {
    return getModelEntity(MetabolicModelRelationshipType.has_gpr_gene, BiodbEntityNode.class);
  }
  
  public Set<BiodbEntityNode> getModelSubsystems() {
    return getModelEntity(MetabolicModelRelationshipType.has_subsystem, BiodbEntityNode.class);
  }
  
  public BiosGenomeNode getGenome(GenomeDatabase database) {
    BiosGenomeNode gnode = null;
    for (Node n : Neo4jUtils.collectNodeRelationshipNodes(
        this, MetabolicModelRelationshipType.has_genome)) {
      if (n.hasLabel(database)) {
        gnode = new BiosGenomeNode(n, databasePath);
      }
    }
    
    return gnode;
  }
  
  public Long addLiterature(BiosLiteratureNode literatureNode) {
    if (literatureNode != null && !Neo4jUtils.exitsRelationshipBetween(this, literatureNode, Direction.BOTH)) {
      logger.info("LINK {} -[{}]> {}", this, GenericRelationship.has_publication, literatureNode);
      Relationship r = this.createRelationshipTo(literatureNode, GenericRelationship.has_publication);
      Neo4jUtils.setTimestamps(r);
      return r.getId();
    }
    
    return null;
  }
}
