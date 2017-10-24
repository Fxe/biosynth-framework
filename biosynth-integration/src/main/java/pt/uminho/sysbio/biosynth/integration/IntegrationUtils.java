package pt.uminho.sysbio.biosynth.integration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jgrapht.UndirectedGraph;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtilsIntegration;
import pt.uminho.sysbio.biosynthframework.Reaction;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.GraphUtils;

public class IntegrationUtils {

  private final static Logger logger = LoggerFactory.getLogger(IntegrationUtils.class);

  private static void aww(Map<Long, Set<Long>> mapping, long key, long val) {
    if (!mapping.containsKey(key)) {
      mapping.put(key, new HashSet<Long> ());
    }
    mapping.get(key).add(val);
  }

  /**
   * Applies a metabolite unification map to reconcialiate metabolite ids
   */
  public static Map<Long, Set<Long>> reconciliateReactionMetabolites(Reaction reaction, Map<Long, Long> unificationMap) {
    Map<Long, Set<Long>> mapping = new HashMap<> ();

    Map<String, Double> left = reaction.getLeftStoichiometry();
    Map<String, Double> left_ = new HashMap<> ();
    for (String id : left.keySet()) {
      Double stoich = left.get(id);
      Long prev_id = Long.parseLong(id);
      Long unifi_id = unificationMap.get(prev_id);
      unifi_id = unifi_id == null ? prev_id : unifi_id;

      aww(mapping, unifi_id, prev_id);

      left_.put(unifi_id.toString(), stoich);
    }
    reaction.setLeftStoichiometry(left_);

    Map<String, Double> right = reaction.getRightStoichiometry();
    Map<String, Double> right_ = new HashMap<> ();
    for (String id : right.keySet()) {
      Double stoich = right.get(id);
      Long prev_id = Long.parseLong(id);
      Long unifi_id = unificationMap.get(prev_id);
      unifi_id = unifi_id == null ? prev_id : unifi_id;

      aww(mapping, unifi_id, prev_id);

      right_.put(unifi_id.toString(), stoich);
    }
    reaction.setRightStoichiometry(right_);

    return mapping;
  }

  public static List<IntegratedMember> collectMembersFromCluster(IntegratedCluster integratedCluster) {
    List<IntegratedMember> integratedMembers = new ArrayList<> ();

    for (IntegratedClusterMember icm : integratedCluster.getMembers()) {
      integratedMembers.add(icm.getMember());
    }

    return integratedMembers;
  }

  public static Set<String> collectMemberEntriesInClusterByMajorLabel(
      Label majorLabel, 
      IntegratedCluster integratedCluster, 
      GraphDatabaseService data) {

    Set<String> majorLabelSet = new HashSet<> ();

    for (IntegratedClusterMember clusterMember : integratedCluster.getMembers()) {
      IntegratedMember member = clusterMember.getMember();
      Node dataNode = data.getNodeById(member.getReferenceId());

      if (dataNode.hasLabel(majorLabel)) {
        majorLabelSet.add((String)dataNode.getProperty("entry"));
      }
    }

    return majorLabelSet;
  }

  public static Long getProtonClusterId(
      MetaboliteMajorLabel biodbLabel,
      String protonEntry,
      MetaboliteHeterogeneousDao<GraphMetaboliteEntity> metaboliteHeterogeneousDao,
      IntegrationMetadataDao integrationMetadataDao,
      Long iid) {

    Long protonClusterId = null;

    GraphMetaboliteEntity META_PROTON = metaboliteHeterogeneousDao.getMetaboliteByEntry(biodbLabel.toString(), protonEntry);
    List<IntegratedCluster> integratedClusters = integrationMetadataDao.getIntegratedClusterByMemberIds(iid, new Long[] {META_PROTON.getId()});

    if (integratedClusters.isEmpty()) return protonClusterId;
    if (integratedClusters.size() > 1) {
      logger.warn("Multiple Clusters found for member: " + protonEntry);
    }

    for (IntegratedCluster integratedCluster : integratedClusters) {
      protonClusterId = integratedCluster.getId();
    }
    return protonClusterId;
  }

  public static<R extends Reaction> Set<String> collectReactionMetabolites(R reaction) {
    Set<String> cpdSet = new HashSet<> ();
    cpdSet.addAll(reaction.getLeftStoichiometry().keySet());
    cpdSet.addAll(reaction.getRightStoichiometry().keySet());

    return cpdSet;
  }

  public static IntegratedCluster assembleMetaboliteClusterWithIds(
      String entry, String description, Collection<Long> eids) {		

    return new IntegratedClusterFactory()
        .withEntry(entry)
        .withDescription(description)
        .withClusterType(IntegrationNodeLabel.MetaboliteCluster.toString())
        .withMemberType(IntegrationNodeLabel.MetaboliteMember.toString())
        .withMemberIdCollection(eids)
        .build();
  }

  public static IntegratedCluster assembleReactionClusterWithIds(
      String entry, String description, Collection<Long> eids) {		

    return new IntegratedClusterFactory()
        .withEntry(entry)
        .withDescription(description)
        .withClusterType(IntegrationNodeLabel.ReactionCluster.toString())
        .withMemberType(IntegrationNodeLabel.ReactionMember.toString())
        .withMemberIdCollection(eids)
        .build();
  }

  public static IntegratedCluster assembleMetaboliteClusterWithEntities(
      String entry, String description, Collection<GraphMetaboliteEntity> eids) {		

    return new IntegratedClusterFactory()
        .withEntry(entry)
        .withDescription(description)
        .withClusterType(IntegrationNodeLabel.MetaboliteCluster.toString())
        .withMemberType(IntegrationNodeLabel.MetaboliteMember.toString())
        .withMemberEntityCollection(eids)
        .build();
  }

  public static Set<Long> collectClusterMemberRerefenceEids(
      IntegratedCluster integratedCluster) {
    Set<Long> eids = new HashSet<> ();

    for (IntegratedClusterMember integratedClusterMember : integratedCluster.getMembers()) {
      Long eid = integratedClusterMember.getMember().getReferenceId();
      eids.add(eid);
    }
    return eids;
  }

  public static<E> double jaccard(Collection<E> a, Collection<E> b) {
    if (a.isEmpty() && b.isEmpty()) return 1.0;

    Set<E> A_union_B = new HashSet<> (a);
    A_union_B.addAll(b);
    Set<E> A_intersect_B = new HashSet<> (a);
    A_intersect_B.retainAll(b);

    return A_intersect_B.size() / (double)A_union_B.size();
  }

  public static<R extends Reaction> boolean alignReactions(List<R> rxnList) {
    logger.debug("Align reactions");

    if (rxnList.isEmpty()) return false;

    Reaction rxnPivot = rxnList.get(0);

    Set<String> leftPivot = new HashSet<> (rxnPivot.getLeftStoichiometry().keySet());
    Set<String> rightPivot = new HashSet<> (rxnPivot.getRightStoichiometry().keySet());
    logger.debug(rxnPivot.getEntry() + ":" + leftPivot + " / " + rightPivot);

    for (int i = 1; i < rxnList.size(); i++) {
      Reaction rxn = rxnList.get(i);
      Set<String> left_ = new HashSet<> (rxn.getLeftStoichiometry().keySet());

      Double l_l = IntegrationUtils.jaccard(left_, leftPivot);
      Double l_r = IntegrationUtils.jaccard(left_, rightPivot);
      if (l_r > l_l) swapStoichiometry(rxn);

      logger.debug(rxn.getEntry() + ":" + rxn.getLeftStoichiometry().keySet() + " / " + rxn.getRightStoichiometry().keySet());
    }
    return true;
  }

  public static<R extends Reaction> void swapStoichiometry(R rxn) {
    logger.debug("Swap eq rxn: " + rxn.getEntry());
    Map<String, Double> left = rxn.getLeftStoichiometry();
    Map<String, Double> right = rxn.getRightStoichiometry();
    rxn.setLeftStoichiometry(right);
    rxn.setRightStoichiometry(left);
  }

  //	private Map<Long, Set<Long>> collectCompoundReactions(Set<Long> cpdIdSet, UnificationTable metaboliteUnificationTable, Long protonId) {
  //		Map<Long, Set<Long>> compoundToReactionMap = new HashMap<> ();
  //		
  //		for (Long cpdId : cpdIdSet) {
  //			Node cpdNode = db.getNodeById(cpdId);
  //			//ignore H on left -> this is cheating !
  //			long unif_cpdId = metaboliteUnificationTable.reconciliateId(cpdId);
  //			if (unif_cpdId != protonId) {
  //				Set<Long> meids = metaboliteUnificationTable.getIdMappingsTo(unif_cpdId);
  //				Set<Long> reids = collectNodes(meids, ReactionRelationshipType.Left, ReactionRelationshipType.Right);
  //				
  //				//non-integrated compounds matches the pivot reaction
  //				if (reids.isEmpty()) reids.add(this.initialNode.getId());
  //				
  //				LOGGER.debug(String.format("MetaboliteNode %s Mapped to CID[%d] matched %d reactions",
  //						cpdNode, unif_cpdId, reids.size()));
  //				compoundToReactionMap.put(cpdId, reids);
  //			} else {
  //				LOGGER.debug(String.format("MetaboliteNode %s Mapped to CID[%d] skipped (ignore protons)",
  //						cpdNode, unif_cpdId));
  //			}
  //		}
  //		
  //		return compoundToReactionMap;
  //	}
  
  public static List<Object> clearModelSpeciesClusters(long itgId, GraphDatabaseService graphMetaService) {
    Node itgNode = graphMetaService.getNodeById(itgId);
    if (itgNode == null || 
        !itgNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) {
      logger.warn("integration set [{}] not found", itgId);
      return null;
    }

    List<Object> result = new ArrayList<> ();

    //search model specie clusters
    for (Relationship itgToCtr : itgNode.getRelationships(IntegrationRelationshipType.has_integrated_model_specie_cluster)) {
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
    for (Relationship r : itgNode.getRelationships(
        IntegrationRelationshipType.has_integrated_model_specie)) {
      r.delete();
      result.add(String.format("DELETE R[%d] [%d]->[%d]", r.getId(), 
          r.getStartNode().getId(), r.getEndNode().getId()));
    }


    return result;
  }

  public static List<Object> clearModelReactionClusters(long itgId, GraphDatabaseService graphMetaService) {
    Node itgNode = graphMetaService.getNodeById(itgId);
    if (itgNode == null || 
        !itgNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) {
      logger.warn("integration set [{}] not found", itgId);
      return null;
    }

    List<Object> result = new ArrayList<> ();

    //search model reaction clusters
    for (Relationship itgToCtr : itgNode.getRelationships(
        IntegrationRelationshipType.has_integrated_model_reaction_cluster)) {
      Node mrctr = itgToCtr.getOtherNode(itgNode);
      for (Relationship r : mrctr.getRelationships(
          IntegrationRelationshipType.Integrates)) {
        r.delete();
        result.add(String.format("DELETE R[%d] [%d]->[%d]", r.getId(), 
            r.getStartNode().getId(), r.getEndNode().getId()));
      }
      itgToCtr.delete();
      result.add(String.format("DELETE R[%d] [%d]->[%d]", itgToCtr.getId(),
          itgNode.getId(), mrctr.getId()));
      mrctr.delete();
      result.add(String.format("DELETE N[%d]", mrctr.getId()));
    }
    for (Relationship r : itgNode.getRelationships(
        IntegrationRelationshipType.has_integrated_model_reaction)) {
      r.delete();
      result.add(String.format("DELETE R[%d] [%d]->[%d]", r.getId(), 
          r.getStartNode().getId(), r.getEndNode().getId()));
    }

    return result;
  }

  
  public static Long createModelSpecieCluster(
      Set<Long> spiIdSet, 
      String ctrEntry, 
      SubcellularCompartment cmp,
      long itgId, 
      GraphDatabaseService 
      graphMetaService, BiodbService service) {
    if (spiIdSet == null ||
        spiIdSet.size() <= 1) {
      logger.warn("invalid id set [{}]", spiIdSet);
    }

    Node itgNode = graphMetaService.getNodeById(itgId);

    if (itgNode == null || 
        !itgNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) {
      logger.warn("integration set [{}] not found", itgId);
      return null;
    }
    
    //create only if members are non integrated
    Map<Long, Node> idToNode = new HashMap<> ();
    for (long spiId : spiIdSet) {
      Node spiNode = Neo4jUtils.getUniqueResult(
          graphMetaService.findNodes(
              IntegrationNodeLabel.IntegratedMember, 
              Neo4jDefinitions.MEMBER_REFERENCE, spiId));
      if (spiNode == null) {
        spiNode = graphMetaService.createNode(
            IntegrationNodeLabel.ModelSpecieMember,
            IntegrationNodeLabel.IntegratedMember);

        // set reference_id : mrxnId
        //     entry        : 
        //     major_label  : ModelReaction
        spiNode.setProperty(Neo4jDefinitions.MEMBER_REFERENCE, spiId);
        spiNode.setProperty(
            Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
            service.getEntryById(spiId));
        spiNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, 
            MetabolicModelLabel.MetaboliteSpecie.toString());

        Neo4jUtils.setUpdatedTimestamp(spiNode);
        Neo4jUtils.setCreatedTimestamp(spiNode);
      } else {
        Set<Long> ids = Neo4jUtils.collectNodeRelationshipNodeIds(spiNode, 
            IntegrationRelationshipType.has_integrated_model_specie);
        if (ids.contains(itgId)) {
          logger.warn("[{}] already integrated in [{}]", spiId, itgId);
          return null;
        }
      }

      idToNode.put(spiId, spiNode);
    }
    
    Node spiCtrNode = 
        graphMetaService.createNode(IntegrationNodeLabel.IntegratedCluster,
            IntegrationNodeLabel.ModelSpecieCluster);
    
    spiCtrNode.setProperty("entry", ctrEntry);
    spiCtrNode.setProperty("subcellular_compartment", cmp.toString());
    Neo4jUtils.setUpdatedTimestamp(spiCtrNode);
    Neo4jUtils.setCreatedTimestamp(spiCtrNode);
    
    //link itg -> ctr
    itgNode.createRelationshipTo(spiCtrNode, 
        IntegrationRelationshipType.has_integrated_model_specie_cluster);
    for (Node spiNode : idToNode.values()) {
      //link ctr -> eid
      spiCtrNode.createRelationshipTo(spiNode, 
          IntegrationRelationshipType.Integrates);
      //link itg -> eid
      itgNode.createRelationshipTo(spiNode, 
          IntegrationRelationshipType.has_integrated_model_specie);
    }

    return spiCtrNode.getId();
  }

  public static Long createModelReactionCluster(
      Set<Long> mrxn, String ctrEntry, long itgId, GraphDatabaseService graphMetaService, BiodbService service) {
    if (mrxn == null ||
        mrxn.size() <= 1) {
      logger.warn("invalid id set [{}]", mrxn);
    }

    Node itgNode = graphMetaService.getNodeById(itgId);

    if (itgNode == null || 
        !itgNode.hasLabel(IntegrationNodeLabel.IntegrationSet)) {
      logger.warn("integration set [{}] not found", itgId);
      return null;
    }

    //create only if members are non integrated
    Map<Long, Node> idToNode = new HashMap<> ();
    for (long mrxnId : mrxn) {
      Node mrxnNode = Neo4jUtils.getUniqueResult(
          graphMetaService.findNodes(
              IntegrationNodeLabel.IntegratedMember, 
              Neo4jDefinitions.MEMBER_REFERENCE, mrxnId));
      if (mrxnNode == null) {
        mrxnNode = graphMetaService.createNode(
            IntegrationNodeLabel.ModelReactionMember,
            IntegrationNodeLabel.IntegratedMember);

        // set reference_id : mrxnId
        //     entry        : 
        //     major_label  : ModelReaction
        mrxnNode.setProperty(Neo4jDefinitions.MEMBER_REFERENCE, mrxnId);
        mrxnNode.setProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
            service.getEntryById(mrxnId));
        mrxnNode.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, 
            MetabolicModelLabel.ModelReaction.toString());

        Neo4jUtils.setUpdatedTimestamp(mrxnNode);
        Neo4jUtils.setCreatedTimestamp(mrxnNode);
      } else {
        Set<Long> ids = Neo4jUtils.collectNodeRelationshipNodeIds(mrxnNode, 
            IntegrationRelationshipType.has_integrated_model_reaction);
        if (ids.contains(itgId)) {
          logger.warn("[{}] already integrated in [{}]", mrxnId, itgId);
          return null;
        }
      }

      idToNode.put(mrxnId, mrxnNode);
    }

    Node mrxnCtrNode = 
        graphMetaService.createNode(IntegrationNodeLabel.IntegratedCluster,
            IntegrationNodeLabel.ModelReactionCluster);
    mrxnCtrNode.setProperty("entry", ctrEntry);
    Neo4jUtils.setUpdatedTimestamp(mrxnCtrNode);
    Neo4jUtils.setCreatedTimestamp(mrxnCtrNode);

    //link itg -> ctr
    itgNode.createRelationshipTo(mrxnCtrNode, 
        IntegrationRelationshipType.has_integrated_model_reaction_cluster);
    for (Node mrxnNode : idToNode.values()) {
      //link ctr -> eid
      mrxnCtrNode.createRelationshipTo(mrxnNode, 
          IntegrationRelationshipType.Integrates);
      //link itg -> eid
      itgNode.createRelationshipTo(mrxnNode, 
          IntegrationRelationshipType.has_integrated_model_reaction);
    }

    return mrxnCtrNode.getId();
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

  public static Set<Long> getSpecieClusters(Set<Long> eidIdSet, long itgId, GraphDatabaseService meta) {
    Set<Long> ctrIds = new HashSet<> ();
    for (long eidId : eidIdSet) {
      Node eidNode = Neo4jUtils.getUniqueResult(
          meta.findNodes(
              IntegrationNodeLabel.IntegratedMember, 
              Neo4jDefinitions.MEMBER_REFERENCE, eidId));
//      System.out.println(eidId + " " + eidNode);
      if (eidNode != null) {
        Set<Node> ctrNodes = Neo4jUtils.collectNodeRelationshipNodes(
            eidNode, IntegrationRelationshipType.Integrates);
        for (Node ctrNode : ctrNodes) {
          if (ctrNode.hasLabel(IntegrationNodeLabel.ModelSpecieCluster) &&
              ctrNode.getSingleRelationship(
                  IntegrationRelationshipType.has_integrated_model_specie_cluster, 
                  Direction.BOTH).getOtherNode(ctrNode).getId() == itgId) {
            ctrIds.add(ctrNode.getId());
          }
        }
      }
//      for (Relationship r : eidNode.getRelationships()) {
//        System.out.println(r.getType().name());
//      }
    }
    
    return ctrIds;
  }
  
  public static void createOrMergeMetaboliteClusters(
      long itgId, 
      Set<Set<Long>> mcpdIntegration,
      String prefix,
      GraphDatabaseService graphDataService,
      GraphDatabaseService graphMetaService,
      BiodbService service) {
//    int counter = 0;
    for (Set<Long> mcpdCtr : mcpdIntegration) {
      Set<Long> ctrIds = new HashSet<> ();
      for (long mcpdId : mcpdCtr) {
        Node mcpdRefNode = Neo4jUtils.getUniqueResult(
            graphMetaService.findNodes(
                IntegrationNodeLabel.IntegratedMember, 
                Neo4jDefinitions.MEMBER_REFERENCE, mcpdId));
        if (mcpdRefNode != null) {
        Set<Node> aa = Neo4jUtils.collectNodeRelationshipNodes(mcpdRefNode, IntegrationRelationshipType.Integrates);
        for (Node ctrNode : aa) {
          Relationship r = ctrNode.getSingleRelationship(
              IntegrationRelationshipType.has_integrated_metabolite, 
              Direction.BOTH);
          if (r != null) {
            Node itgNode = r.getOtherNode(ctrNode);
//            System.out.println(ctrNode.getProperty("entry") + " -> " + itgNode.getProperty("entry"));
            if (itgNode.getId() == itgId) {
              ctrIds.add(ctrNode.getId());
            }
          }
          
        }
      }
      }
    }
  }
  
  public static void createOrMergeSpecieClusters(
      long itgId,
      Map<Set<Long>, SubcellularCompartment> specieIntegration,
      String prefix,
      GraphDatabaseService graphDataService,
      GraphDatabaseService graphMetaService,
      BiodbService service) {
    int counter = 0;
    for (Set<Long> spiIdCluster : specieIntegration.keySet()) {
      Set<Long> ctrIds = new HashSet<> ();
      for (long spiId : spiIdCluster) {
//        System.out.println(spiId);
        Node spiRrefNode = Neo4jUtils.getUniqueResult(
            graphMetaService.findNodes(
                IntegrationNodeLabel.IntegratedMember, 
                Neo4jDefinitions.MEMBER_REFERENCE, spiId));
        if (spiRrefNode != null) {
//          System.out.println(spiRrefNode);
          Set<Node> aa = Neo4jUtils.collectNodeRelationshipNodes(spiRrefNode, IntegrationRelationshipType.Integrates);
          for (Node ctrNode : aa) {
            Relationship r = ctrNode.getSingleRelationship(
                IntegrationRelationshipType.has_integrated_model_specie_cluster, 
                Direction.BOTH);
            if (r != null) {
              Node itgNode = r.getOtherNode(ctrNode);
//              System.out.println(ctrNode.getProperty("entry") + " -> " + itgNode.getProperty("entry"));
              if (itgNode.getId() == itgId) {
                ctrIds.add(ctrNode.getId());
              }
            }
            
          }
        }
      }
      //if no previous ctrs tem create new otherwise update
      if (ctrIds.isEmpty()) {
        String ctrEntry = prefix + counter++;
        logger.info("create {} -> {}", spiIdCluster, ctrEntry);
        SubcellularCompartment scmp = specieIntegration.get(spiIdCluster);
        createModelSpecieCluster(
            spiIdCluster, ctrEntry, scmp,
            itgId, 
            graphMetaService, service);
      } else {
        logger.info("update {} with {}", ctrIds, spiIdCluster);
        updateClusters(ctrIds, spiIdCluster, graphDataService, graphMetaService);
      }
    }
  }
  
  /**
   * Adds unification link SPECIE -> [SPECIES]
   * from specie clusters
   * @param g
   * @param spiUniMap
   */
  public static void setupSpecieClusterLinks(UndirectedGraph<Long, ?> g, Map<Long, Long> spiUniMap) {
    Map<Long, Set<Long>> ctrToSpiSet = CollectionUtils.reverseMap(spiUniMap);
    for (Set<Long> spiSet : ctrToSpiSet.values()) {
      Iterator<Long> it = spiSet.iterator();
      long pivot = it.next();
      GraphUtils.addVertexIfNotExists(g, pivot);
      while (it.hasNext()) {
        long next = it.next();
        GraphUtils.addVertexIfNotExists(g, next);
        g.addEdge(pivot, next);
      }
    }
  }
  
  public static void setupSpecieClusterLinks(UndirectedGraph<Long, ?> g, GraphDatabaseService meta, long itgId) {
    Map<Long, Long> spiUniMap = new HashMap<> ();
    Node itgNode = meta.getNodeById(itgId);
    for (Node spiCtrNode : Neo4jUtils.collectNodeRelationshipNodes(
        itgNode, IntegrationRelationshipType.has_integrated_model_specie_cluster)) {
      for (Node refNode : Neo4jUtils.collectNodeRelationshipNodes(
          spiCtrNode, IntegrationRelationshipType.Integrates)) {
        long refId = (long) refNode.getProperty(Neo4jDefinitions.MEMBER_REFERENCE);
       spiUniMap.put(refId, spiCtrNode.getId());
      }
    }
    
    setupSpecieClusterLinks(g, spiUniMap);
  }
  
  /**
   * Adds unification link ENTITY -> [REFERENCES]
   * @param g
   * @param biodbService
   * @param ids
   */
  public static void setupIdToReferenceId(UndirectedGraph<Long, ?> g, BiodbService biodbService, Set<Long> ids) {
    for (long id : ids) {
      GraphUtils.addVertexIfNotExists(g, id);
      Set<Long> refSet = biodbService.getReferencesBy(id);
      if (refSet != null) {
        for (long refId : refSet) {
          GraphUtils.addVertexIfNotExists(g, refId);
          g.addEdge(id, refId);
        }
      }
    }
  }
  
  /**
   * Adds unification link ENTITY -> [REFERENCES]
   * @param g
   * @param biodbService
   * @param ids
   */
  public static void setupIdToIdFromFile(UndirectedGraph<Long, ?> g, String path) {
    try {
      List<String> lines = IOUtils.readLines(new FileInputStream(path));
      for (String l : lines) {
        if (!l.startsWith("#")) {
          String[] cols = l.split("\t");
          if (cols.length > 1) {
            long pivot = Long.parseLong(cols[0]);
            GraphUtils.addVertexIfNotExists(g, pivot);
            for (int i = 1; i < cols.length; i++) {
              long e = Long.parseLong(cols[i]);
              GraphUtils.addVertexIfNotExists(g, e);
              g.addEdge(pivot, e);
            }
          } else {
            logger.warn("invalid line {}", l);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
//  public static Node getNodeByDatabaseAndEntry(Label database, String entry, GraphDatabaseService service) {
//    return Neo4jUtils.getNodeByEntry(database, entry, service);
//  }
}
