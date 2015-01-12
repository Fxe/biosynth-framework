package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationNodeLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jIntegrationMetadataDaoImpl;
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationCollectionUtilities;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

public class Neo4jIntegrationEngine {

	private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jIntegrationEngine.class);	
	
//	IKeyGenerator<String> entryGenerator;
	
	Neo4jIntegrationMetadataDaoImpl metadataDao;
	
	 List<Set<Long>> fresh;
	// should be pair
	 Map<Set<Long>, Set<Long>> merge;
	
	public Neo4jIntegrationEngine() {
		
	}
	
	public Map<Long, Set<Long>> getPreviousClusters(IntegrationSet integrationSet) {
		Map<Long, Set<Long>> clusterMemberMap = new HashMap<> ();
		
		Set<Long> cidSet =  metadataDao.getAllIntegratedClusterIdsByType(
				integrationSet.getId(), 
				IntegrationNodeLabel.MetaboliteCluster.toString());
		
		for (Long cid : cidSet) {
			IntegratedCluster integratedCluster = metadataDao.getIntegratedClusterById(cid);
			Long id = integratedCluster.getId();
			Set<Long> eids = IntegrationUtils.collectClusterMemberIds(integratedCluster);
			
			clusterMemberMap.put(id, eids);
		}
		
		LOGGER.debug("Previous Clusters: " + clusterMemberMap.size());
		
		return clusterMemberMap;
	}
	
	public List<Set<Long>> generateUniqueMembershipClusters(ClusteringStrategy clusteringStrategy, Long[] eids) {
		Map<Long, Set<Long>> initialClusters = new HashMap<> ();
		
		
		for (Long eid : eids) {
			clusteringStrategy.setInitialNode(eid);
			Set<Long> res = clusteringStrategy.execute();
			if (res == null || res.isEmpty()) throw new RuntimeException("null or empty set - " + eid);
			
			if (res.size() > 1) {
				initialClusters.put(eid, res);
			}
		}
		
		Set<Long> survived = new HashSet<>();
		Set<Long> deleted = new HashSet<>();
		
		LOGGER.debug("Resolving Strategy Conflicts");
		Map<Long, Long> mapping = IntegrationCollectionUtilities.resolveConflicts(initialClusters, survived, deleted);
		
		List<Set<Long>> uniqueMembershipClusters = new ArrayList<> ();
		for (Long id : survived) uniqueMembershipClusters.add(initialClusters.get(id));
		
		LOGGER.trace("Mapping   : " + mapping);
		LOGGER.trace("Deleted   : " + deleted);
		LOGGER.trace("Survided  : " + survived);
		LOGGER.trace("U. Cluster: " + uniqueMembershipClusters);
		
		LOGGER.debug("Deleted   : " + deleted.size());
		LOGGER.debug("Survided  : " + survived.size());
		LOGGER.debug("U. Cluster: " + uniqueMembershipClusters.size());
		
		return uniqueMembershipClusters;
	}
	
	public void integrate(IntegrationSet integrationSet, ClusteringStrategy clusteringStrategy, Long[] eids) {
		this.integrate(integrationSet, clusteringStrategy, Arrays.asList(eids));
	}
	
	public void integrate(IntegrationSet integrationSet, ClusteringStrategy clusteringStrategy, Collection<Long> eids) {
		Long[] eidArray =  eids.toArray(new Long[0]);
		
		LOGGER.debug("Generate Initial Integration");
		List<Set<Long>> uniqueMembershipClusters = this.generateUniqueMembershipClusters(clusteringStrategy, eidArray);
		
		LOGGER.debug("Loading Previous Clusters");
		Map<Long, Set<Long>> prev = this.getPreviousClusters(integrationSet);
		
		LOGGER.debug("Resolving Previous Conflicts");
		this.resolveConflicts(uniqueMembershipClusters, prev);
	}
	
	public void resolveConflicts(List<Set<Long>> uniqueMembershipClusters, Map<Long, Set<Long>> prev) {
		List<Set<Long>> fresh = new ArrayList<> ();
		Map<Set<Long>, Set<Long>> merge = new HashMap<> ();
		Set<Long> unaffected = new HashSet<> ();
		IntegrationCollectionUtilities.resolveMerging(uniqueMembershipClusters, prev, fresh, merge, unaffected);
		
		LOGGER.trace("Fresh     : " + fresh);
		LOGGER.trace("Merge     : " + merge);
		LOGGER.trace("Unaffected: " + unaffected);
		
		this.fresh = fresh;
		this.merge = merge;
	}
}
