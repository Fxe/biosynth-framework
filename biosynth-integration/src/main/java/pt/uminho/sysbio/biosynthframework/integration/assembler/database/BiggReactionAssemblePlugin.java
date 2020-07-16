package pt.uminho.sysbio.biosynthframework.integration.assembler.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class BiggReactionAssemblePlugin extends AbstractNeo4jAssemblePlugin {

  private static final Logger logger = LoggerFactory.getLogger(BiggReactionAssemblePlugin.class);
  
  public BiggReactionAssemblePlugin(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
    ignoreAttributes.add("reactionString");
  }

  @Override
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    Map<String, Object> result = new HashMap<>();
    Set<BiodbReactionNode> rxns = getReactionNodes(refs, ReactionMajorLabel.BiGGReaction);
    
    result.put("pseudoreaction", null);
    
    Set<String> models = new HashSet<>();
    for (BiodbReactionNode rxnNode : rxns) {
      collectAttributes(result, rxnNode);
//      models.addAll(getModels(rxnNode));
      
      for (Relationship r : rxnNode.getRelationships()) {
        Node other = r.getOtherNode(rxnNode);
        if (!ignoreRelationships.contains(r.getType().name())) {
          if (other.hasLabel(GlobalLabel.MetabolicModel)) {
            models.add((String) other.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
          } else {
            logger.warn("rprop: [{}] -> {}", r.getType().name(), Neo4jUtils.getLabelsAsString(other));
          }
        }
      }
    }
    
    if (!models.isEmpty()) {
      result.put("models", models);
    }
    
    return result;
  }
  
  public Set<String> getModels(Node rxnNode) {
    Set<String> result = new HashSet<> ();
    
    for (Relationship r : rxnNode.getRelationships(ReactionRelationshipType.included_in)) {
      Node modelNode = r.getOtherNode(rxnNode);
      if (modelNode.hasLabel(GlobalLabel.MetabolicModel)) {
        result.add((String) modelNode.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
      }
    }
    
    return result;
  }

}
