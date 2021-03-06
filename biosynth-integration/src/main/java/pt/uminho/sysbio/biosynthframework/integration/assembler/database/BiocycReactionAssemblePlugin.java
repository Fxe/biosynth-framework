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
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class BiocycReactionAssemblePlugin extends AbstractNeo4jAssemblePlugin {

  private static final Logger logger = LoggerFactory.getLogger(BiocycReactionAssemblePlugin.class);
  
  protected String pgdb;
  
  public BiocycReactionAssemblePlugin(GraphDatabaseService graphDatabaseService, String pgdb) {
    super(graphDatabaseService);
    this.pgdb = pgdb;
    
    ignoreAttributes.add("frameId");
  }

  @Override
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    Map<String, Object> result = new HashMap<>();
    Set<BiodbReactionNode> rxns = getReactionNodes(refs, pgdb);
    

    
    result.put("orphan", null);
    result.put("gibbs", null);
    result.put("physiologicallyRelevant", null);
    Set<String> uniprot = new HashSet<>();
    Set<String> pathways = new HashSet<>();
    Set<String> inhibitors = new HashSet<>();
    Set<String> activators = new HashSet<>();

//    result.put("reactionDirection", null);
    
    for (BiodbReactionNode rxnNode : rxns) {
      collectAttributes(result, rxnNode);
//      logger.info("aprop: {}", rxnNode.getAllProperties().keySet());
      
      for (Relationship r : rxnNode.getRelationships()) {
        Node other = r.getOtherNode(rxnNode);
        if (!ignoreRelationships.contains(r.getType().name())) {
          if (other.hasLabel(GlobalLabel.UniProt)) {
            uniprot.add((String) other.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
          } else if (other.hasLabel(GlobalLabel.MetabolicPathway) && other.hasLabel(ReactionMajorLabel.MetaCyc)){
            pathways.add((String) other.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
          } else if (r.getType().name().equals(MetaboliteRelationshipType.is_inhibitor_of.toString())) {
            inhibitors.add((String) other.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
          } else if (r.getType().name().equals(MetaboliteRelationshipType.is_activator_of.toString())) {
            activators.add((String) other.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
          } else {
            logger.warn("rprop: [{}] -> {}", r.getType().name(), Neo4jUtils.getLabelsAsString(other));
          }
        }
      }
    }
    
    result.put("uniprot", uniprot);
    result.put("pathways", pathways);
    result.put("inhibitors", inhibitors);
    result.put("activators", activators);
    
    return result;
  }

}
