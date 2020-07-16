package pt.uminho.sysbio.biosynthframework.integration.assembler.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class BiocycMetaboliteAssemblePlugin extends AbstractNeo4jAssemblePlugin {

  protected String pgdb;
  
  public BiocycMetaboliteAssemblePlugin(GraphDatabaseService graphDatabaseService, String pgdb) {
    super(graphDatabaseService);
    this.pgdb = pgdb;
  }

  @Override
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    
    /*
     * [, charge, , , , , description, gibbs, entry, proxy, updated_at, name, comment, cmlMolWeight, 
     * molWeight]
     * [, instance_of, parent_of, , has_charge, , , ]
     */
 
    Map<String, Object> result = new HashMap<>();
    
    Set<BiodbMetaboliteNode> cpdNodes = filter(refs, pgdb);
    for (BiodbMetaboliteNode cpdNode : cpdNodes) {
      Set<String> parents = new HashSet<>();
      Set<String> instances = new HashSet<>();
      Set<String> subclasses = new HashSet<>();
      Set<String> reactions = new HashSet<>();
      Set<String> activators = new HashSet<>();
      Set<String> inhibitors = new HashSet<>();
      Double gibbs = (Double) cpdNode.getProperty("gibbs", null);
      Double cmlMolWeight = (Double) cpdNode.getProperty("cmlMolWeight", null);
      Double molWeight = (Double) cpdNode.getProperty("cmlMolWeight", null);
//      String comment = (String) cpdNode.getProperty("comment", null);
      
      
      Set<Node> parentNodes = Neo4jUtils.collectNodeRelationshipNodes(
          cpdNode, MetaboliteRelationshipType.instance_of);
      for (Node p : parentNodes) {
        parents.add(
            (String) p.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
      }
      Set<Node> isntanceNodes = Neo4jUtils.collectNodeRelationshipNodes(
          cpdNode, MetaboliteRelationshipType.parent_of);
      for (Node i : isntanceNodes) {
        instances.add(
            (String) i.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
      }
      Set<Node> subclassNodes = Neo4jUtils.collectNodeRelationshipNodes(
          cpdNode, MetaboliteRelationshipType.subclass_of);
      for (Node i : subclassNodes) {
        subclasses.add(
            (String) i.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
      }
      Set<Node> reactionNodes = Neo4jUtils.collectNodeRelationshipNodes(
          cpdNode, ReactionRelationshipType.left_component, ReactionRelationshipType.right_component);
      for (Node i : reactionNodes) {
        reactions.add(
            (String) i.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
      }
      Set<Node> activatorNodes = Neo4jUtils.collectNodeRelationshipNodes(
          cpdNode, MetaboliteRelationshipType.is_activator_of);
      for (Node i : activatorNodes) {
        activators.add(
            (String) i.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
      }
      Set<Node> inhibitorNodes = Neo4jUtils.collectNodeRelationshipNodes(
          cpdNode, MetaboliteRelationshipType.is_inhibitor_of);
      for (Node i : inhibitorNodes) {
        inhibitors.add(
            (String) i.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
      }
      
      if (gibbs != null) {
        result.put("gibbs", gibbs);
      }
      if (cmlMolWeight != null) {
        result.put("cmlMolWeight", cmlMolWeight);
      }
      if (molWeight != null) {
        result.put("molWeight", molWeight);
      }
      if (!parents.isEmpty()) {
        result.put("parents", parents);
      }
      if (!instances.isEmpty()) {
        result.put("instances", instances);
      }
      if (!subclasses.isEmpty()) {
        result.put("subclasses", subclasses);
      }
      if (!reactions.isEmpty()) {
        result.put("reactions", reactions);
      }
      if (!activators.isEmpty()) {
        result.put("activators", activators);
      }
      if (!inhibitors.isEmpty()) {
        result.put("inhibitors", inhibitors);
      }
    }
    
    return result;
  }
}