package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities used to perform several Neo4j operations 
 * specific to integration context.
 * 
 * @author Filipe
 */
public class Neo4jUtilsIntegration {

  private final static Logger logger = LoggerFactory.getLogger(Neo4jUtilsIntegration.class);

  public static void deleteCluster(Node clusterNode) {

  }
  
  /**
   * 
   * @param refId Unique entity node identifier 
   * @param dataDb GraphDatabaseService that contains information of reference
   * @param targetDb GraphDatabaseService that creates the member
   */
  public static Node getOrCreateIntegratedMemberByReferenceId(
      long refId, GraphDatabaseService dataDb, GraphDatabaseService targetDb) {
    Node refNode = Neo4jUtils.getUniqueResult(
        targetDb.findNodesByLabelAndProperty(
            IntegrationNodeLabel.IntegratedMember, 
            Neo4jDefinitions.MEMBER_REFERENCE, refId));
    
    if (refNode != null) {
      return refNode;
    }
    //create node
    Node dataNode = dataDb.getNodeById(refId);
    Label typeLabel = null;
    if (dataNode.hasLabel(GlobalLabel.Metabolite)) {
      typeLabel = IntegrationNodeLabel.MetaboliteMember;
    } else if (dataNode.hasLabel(GlobalLabel.Reaction)) {
      typeLabel = IntegrationNodeLabel.ReactionMember;
    } else if (dataNode.hasLabel(MetabolicModelLabel.MetaboliteSpecie)) {
      typeLabel = IntegrationNodeLabel.ModelSpecieMember;
    } else if (dataNode.hasLabel(MetabolicModelLabel.ModelReaction)) {
      typeLabel = IntegrationNodeLabel.ModelReactionMember;
    } else {
      throw new RuntimeException("Invalid reference id. Must be either MetaboliteSpecie/Metabolite/Reaction");
    }
    
    String entry = (String) dataNode.getProperty("entry");

    refNode = targetDb.createNode(IntegrationNodeLabel.IntegratedMember, typeLabel);
    Neo4jUtils.setCreatedTimestamp(refNode);
    Neo4jUtils.setUpdatedTimestamp(refNode);
    refNode.setProperty(Neo4jDefinitions.MEMBER_REFERENCE, refId);
    refNode.setProperty("entry", entry);
    
    if (dataNode.hasProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY)) {
      String database = (String) dataNode.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
      refNode.setProperty("database", database);
    }
    
    logger.debug("created {} [{}]{}", typeLabel, refNode.getId(), entry);
    
    return refNode;
  }

  public static void removeMembersFromCluster(Node clusterNode, Long[] members) {
    Set<Long> memberSet = new HashSet<> (Arrays.asList(members));

    for (Relationship relationship : clusterNode.getRelationships()) {
      Node other = relationship.getOtherNode(clusterNode);
      Long eid = other.getId();
      if (memberSet.contains(eid)) {
        logger.debug(String.format("Delete: [%d]-[%d]-[%d]", clusterNode.getId(), relationship.getId(), eid));
      }
    }
    //		Map<Long, Set<Relationship>> memberRelationshipMap = new HashMap<> ();
    //		for (Long member : members) memberRelationshipMap.put(member, new HashSet<Relationship> ());
    //		
    //		for (Relationship relationship :clusterNode.getRelationships()) {
    //			Node other = relationship.getOtherNode(clusterNode);
    //			Long eid = other.getId();
    //			if (memberRelationshipMap.containsKey(eid)) {
    //				memberRelationshipMap.get(eid).add(relationship);
    //			}
    //		}

    //		for (Long eid : memberRelationshipMap.keySet()) {
    //			
    //		}
  }
}
