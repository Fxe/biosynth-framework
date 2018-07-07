package pt.uminho.sysbio.biosynthframework.integration.assembler.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class BiggMetaboliteAssemblePlugin extends AbstractNeo4jAssemblePlugin {

  public BiggMetaboliteAssemblePlugin(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }

  @Override
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    Map<String, Object> result = new HashMap<>();
    Set<BiodbMetaboliteNode> cpds = filter(refs, MetaboliteMajorLabel.BiGGMetabolite.toString());
    
    Set<String> models = new HashSet<>();
    
    for (BiodbMetaboliteNode cpdNode : cpds) {
      models.addAll(getModels(cpdNode));
    }
    
    if (!models.isEmpty()) {
      result.put("models", models);
    }
    
    return result;
  }

  public Set<String> getModels(Node node) {
    Set<String> result = new HashSet<> ();
    
    for (Relationship r : node.getRelationships(ReactionRelationshipType.left_component)) {
      Node rxnNode = r.getOtherNode(node);
      Set<Node> models = Neo4jUtils.collectNodeRelationshipNodes(rxnNode, ReactionRelationshipType.included_in);
      for (Node model : models) {
        if (model.hasLabel(GlobalLabel.MetabolicModel)) {
          result.add((String) model.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
        }
      }
    }
    
    for (Relationship r : node.getRelationships(ReactionRelationshipType.right_component)) {
      Node rxnNode = r.getOtherNode(node);
      Set<Node> models = Neo4jUtils.collectNodeRelationshipNodes(rxnNode, ReactionRelationshipType.included_in);
      for (Node model : models) {
        if (model.hasLabel(GlobalLabel.MetabolicModel)) {
          result.add((String) model.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
        }
      }
    }
    
    return result;
  }
}
