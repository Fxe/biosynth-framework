package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

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
        targetDb.findNodes(
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
      typeLabel = MetabolicModelLabel.MetaboliteSpecie;
    } else if (dataNode.hasLabel(MetabolicModelLabel.ModelReaction)) {
      typeLabel = MetabolicModelLabel.ModelReaction;
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
  
  public static Node getIntegrationNode(
      long itgId, GraphDatabaseService graphMetaService) 
          throws IllegalArgumentException {
    
    Node itgNode = graphMetaService.getNodeById(itgId);
    if (itgNode == null || 
        !itgNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) {
      throw new IllegalArgumentException(
          String.format("integration set [%d] not found", itgId));
    }
    return itgNode;
  }
  
  public static List<Object> clearIntegrationClusters(
      Node itgNode, 
      IntegrationRelationshipType itgToCtrType,
      IntegrationRelationshipType refToItgType, 
      GraphDatabaseService graphMetaService) {
    List<Object> result = new ArrayList<> ();

    //search model specie clusters
    for (Relationship itgToCtr : itgNode.getRelationships(itgToCtrType)) {
      Node msctr = itgToCtr.getOtherNode(itgNode);
      for (Relationship r : msctr.getRelationships(
          IntegrationRelationshipType.Integrates)) {
        r.delete();
        result.add(String.format("DELETE R[%d] [%d]->[%d]", r.getId(), 
            r.getStartNode().getId(), r.getEndNode().getId()));
      }
      itgToCtr.delete();
      result.add(String.format("DELETE R[%d] [%d]->[%d]", itgToCtr.getId(),
          itgNode.getId(), msctr.getId()));
      msctr.delete();
      result.add(String.format("DELETE N[%d]", msctr.getId()));
    }
    for (Relationship r : itgNode.getRelationships(refToItgType)) {
      r.delete();
      result.add(String.format("DELETE R[%d] [%d]->[%d]", r.getId(), 
          r.getStartNode().getId(), r.getEndNode().getId()));
    }

    return result;
  }
  
  public static List<Object> clearModelSpeciesClusters(
      long itgId, GraphDatabaseService graphMetaService) {
    Node itgNode = Neo4jUtilsIntegration.getIntegrationNode(itgId, graphMetaService);

    return Neo4jUtilsIntegration.clearIntegrationClusters(
        itgNode, 
        IntegrationRelationshipType.has_integrated_model_specie_cluster, 
        IntegrationRelationshipType.has_integrated_model_specie, 
        graphMetaService);
  }
  
  public static List<Object> clearModelMetabolitesClusters(
      long itgId, GraphDatabaseService graphMetaService) {
    Node itgNode = getIntegrationNode(itgId, graphMetaService);

    return clearIntegrationClusters(
        itgNode, 
        IntegrationRelationshipType.has_integrated_model_metabolite_cluster, 
        IntegrationRelationshipType.has_integrated_model_metabolite, 
        graphMetaService);
  }
  
  public static List<Object> clearModelReactionsClusters(
      long itgId, GraphDatabaseService graphMetaService) {
    Node itgNode = getIntegrationNode(itgId, graphMetaService);

    return clearIntegrationClusters(
        itgNode, 
        IntegrationRelationshipType.has_integrated_model_reaction_cluster, 
        IntegrationRelationshipType.has_integrated_model_reaction, 
        graphMetaService);
  }
  
  public static Map<RelationshipType, Integer> countClusters(
      long itgId, GraphDatabaseService graphMetaService) {
    Map<RelationshipType, Integer> result = new HashMap<> ();
    
    Node itgNode = graphMetaService.getNodeById(itgId);
    if (itgNode == null || 
        !itgNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) {
      logger.warn("integration set [{}] not found", itgId);
      return null;
    }
    
    for (Relationship r : itgNode.getRelationships()) {
      CollectionUtils.increaseCount(result, r.getType(), 1);
    }
    
    return result;
  }
  
  /**
   * Collects integration references (creates non existing)
   * @param ctr Set of reference ID's
   * @param refLabel Type of the references
   * @return
   */
  public static Map<Long, Node> getOrGenerateReferences(
      Set<Long> ctr, 
      Label refLabel, 
      GraphDatabaseService graphDataService, 
      GraphDatabaseService graphMetaService) {
    
    //create only if members are non integrated
    Map<Long, Node> idToNode = new HashMap<> ();
    for (long id : ctr) {
      Node refNode = Neo4jUtils.getUniqueResult(
          graphMetaService.findNodes(
              IntegrationNodeLabel.IntegratedMember, 
              Neo4jDefinitions.MEMBER_REFERENCE, id));
      if (refNode == null) {
        refNode = graphMetaService.createNode(
            refLabel,
            IntegrationNodeLabel.IntegratedMember);

        // set reference_id : entity id
        //     entry        : entity entry
        //     major_label  : entity unique label
        refNode.setProperty(Neo4jDefinitions.MEMBER_REFERENCE, id);
        refNode.setProperty(
            Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
            graphDataService.getNodeById(id).getProperty("entry"));
        refNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, 
            refLabel.toString());

        Neo4jUtils.setUpdatedTimestamp(refNode);
        Neo4jUtils.setCreatedTimestamp(refNode);
      }

      idToNode.put(id, refNode);
    }
    
    return idToNode;
  }
  
  /**
   * gut !
   * @param ctr
   * @param ctrEntry
   * @param itgId
   * @param graphDataService
   * @param graphMetaService
   * @return
   */
  public static Node createCluster(
      Set<Long> ctr,
      IntegrationNodeLabel ctrLabel,
      Label ctrRefType,
      IntegrationRelationshipType refToItgType,
      IntegrationRelationshipType ctrToItgType,
      String ctrEntry, 
      long itgId,
      GraphDatabaseService graphDataService,
      GraphDatabaseService graphMetaService) {
    if (ctr == null ||
        ctr.size() <= 1) {
      throw new IllegalArgumentException(String.format("invalid id set %s", ctr));
    }
    
    Node itgNode = getIntegrationNode(itgId, graphMetaService);
    
    //model metabolite references are species !
    Map<Long, Node> idToNode = getOrGenerateReferences(
        ctr, ctrRefType, 
        graphDataService, 
        graphMetaService);
    
    //check of every Node is not integrated in itgNode
    for (long id : idToNode.keySet()) {
      Node n = idToNode.get(id);
      Set<Long> ids = Neo4jUtils.collectNodeRelationshipNodeIds(n, refToItgType);
      if (ids.contains(itgId)) {
        logger.warn("[{}] already integrated in [{}]", id, itgId);
        return null;
      }
    }
    
    Node ctrNode = graphMetaService.createNode(
        ctrLabel, IntegrationNodeLabel.IntegratedCluster);
    
    ctrNode.setProperty("entry", ctrEntry);
    Neo4jUtils.setUpdatedTimestamp(ctrNode);
    Neo4jUtils.setCreatedTimestamp(ctrNode);
    
    //link itg -> ctr
    itgNode.createRelationshipTo(ctrNode, ctrToItgType);
    for (Node refNode : idToNode.values()) {
      //link ctr -> eid
      ctrNode.createRelationshipTo(refNode, 
          IntegrationRelationshipType.Integrates);
      //link itg -> eid
      itgNode.createRelationshipTo(refNode, refToItgType);
    }

    return ctrNode;
  }
  
  /**
   * Keeps references that are integrated by integration ID
   * @param refIds
   * @param itgId
   * @param ctrToItgRel
   * @param graphMetaService
   * @return
   */
  public static Set<Long> filterByIntegrationId(
      Set<Long> refIds, long itgId, 
      IntegrationRelationshipType ctrToItgRel, 
      GraphDatabaseService graphMetaService) {
    Set<Long> query = new HashSet<> ();
    for (long refId : refIds) {
      //    System.out.println(spiId);

      Node refNode = Neo4jUtils.getUniqueResult(
          graphMetaService.findNodes(
              IntegrationNodeLabel.IntegratedMember, 
              Neo4jDefinitions.MEMBER_REFERENCE, refId));
      if (refNode != null) {
        //      System.out.println(spiRrefNode);
        Set<Node> aa = Neo4jUtils.collectNodeRelationshipNodes(
            refNode, IntegrationRelationshipType.Integrates);
        for (Node ctrNode : aa) {
          Relationship r = ctrNode.getSingleRelationship(ctrToItgRel, Direction.BOTH);
          if (r != null) {
            Node itgNode = r.getOtherNode(ctrNode);
            //          System.out.println(ctrNode.getProperty("entry") + " -> " + itgNode.getProperty("entry"));
            if (itgNode.getId() == itgId) {
              query.add(ctrNode.getId());
            }
          }
        }
      }
    }
    
    return query;
  }
  
  public static void updateClusters(Set<Long> ctrMerge, Set<Long> eidAdd, 
      GraphDatabaseService graphDataService,
      GraphDatabaseService graphMetaService) {
    
    logger.debug("Fuse {} ++> {}", ctrMerge, eidAdd);
    Iterator<Long> it = ctrMerge.iterator();
    long pivotCtrId = it.next();
    Node pivotNode = graphMetaService.getNodeById(pivotCtrId);
    Set<Node> pivotEidNodes = Neo4jUtils.collectNodeRelationshipNodes(
        pivotNode, IntegrationRelationshipType.Integrates);
    Set<Node> eidNodesToAdd = new HashSet<> ();
    while (it.hasNext()) {
      long ctrId = it.next();
      Node ctrNode = graphMetaService.getNodeById(ctrId);
      for (Relationship r : ctrNode.getRelationships()) {
        System.out.println("C -> " + r.getType().name());
        if (r.getType().equals(IntegrationRelationshipType.Integrates)) {
          Node eidNode = r.getOtherNode(ctrNode);
          eidNodesToAdd.add(eidNode);
        }
        r.delete();
        logger.debug("DELETE R[{}]", r.getId());
      }
      ctrNode.delete();
      logger.debug("DELETE N[{}]", ctrNode.getId());
    }

    //collect eidAdd nodes
    for (long refId : eidAdd) {
      Node eidNode = Neo4jUtils.getUniqueResult(
          graphMetaService.findNodes(
              IntegrationNodeLabel.IntegratedMember, 
              Neo4jDefinitions.MEMBER_REFERENCE, refId));

      if (eidNode == null) {
        eidNode = 
            Neo4jUtilsIntegration.getOrCreateIntegratedMemberByReferenceId(
                refId, graphDataService, graphMetaService);
        logger.debug("create node for ref {}", refId);
      } else {
        logger.debug("found  node for ref {} -> {}", refId, eidNode);
      }
      eidNodesToAdd.add(eidNode);
    }

    eidNodesToAdd.removeAll(pivotEidNodes);

    logger.debug("{} => {} + {}", pivotNode, eidNodesToAdd, pivotEidNodes);
    for (Node eidNode : eidNodesToAdd) {
      if (eidNode == null) {
        logger.warn("found null reference node");
      } else {
        logger.info("LINK {} -> {}", pivotNode, eidNode);
        Relationship r = pivotNode.createRelationshipTo(eidNode, IntegrationRelationshipType.Integrates);
        Neo4jUtils.setCreatedTimestamp(r);
        Neo4jUtils.setUpdatedTimestamp(r);
      }
    }
  }
}
