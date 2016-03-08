package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.Reaction;

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
}
