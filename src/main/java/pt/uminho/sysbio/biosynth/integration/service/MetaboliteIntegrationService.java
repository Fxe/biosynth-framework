package pt.uminho.sysbio.biosynth.integration.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.service.ConflictDecision;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.SplitStrategy;

public interface MetaboliteIntegrationService extends IntegrationService {
	
	public List<IntegratedCluster> pageClusters(Long iid, int firstResult, int maxResults);
	public int countIntegratedClustersByIntegrationId(Long iid);
	
	public void lalal(String type, long iid, int page, int limit);
	
	/**
	 * 
	 * Applies a clustering strategy to a set of initial 
	 * targets against a set of domain elements.
	 * 
	 * @param iid id of the integration set
	 * @param clusteringStrategy strategy to generate clusters
	 * @param initial members to scaffold
	 * @param domain valid members of the clusters
	 * @param conflictDecision on conflict action
	 * @return
	 */
	public List<IntegratedCluster> generateIntegratedClusters(Long iid, ClusteringStrategy clusteringStrategy, 
			Set<Long> initial, Set<Long> domain, ConflictDecision conflictDecision);
	
	/**
	 * Creates a single cluster
	 * 
	 * @param integrationSet integration set of the cluster
	 * @param name the entry of the cluster
	 * @param members the elements of the cluster
	 * @param description
	 * @param conflictDecision on conflict action
	 * @return
	 */
	public IntegratedCluster createCluster(
			IntegrationSet integrationSet, 
			String name, Set<Long> members, 
			String description, ConflictDecision conflictDecision);
	
	/**
	 * same as generateIntegratedClusters except there is a limit !
	 * @param integrationSet
	 * @param clusteringStrategy
	 * @param initial
	 * @param domain
	 * @param conflictDecision
	 * @param limit
	 * @return
	 */
	public List<IntegratedCluster> createCluster(
			IntegrationSet integrationSet,
			ClusteringStrategy clusteringStrategy,
			Set<Long> initial, Set<Long> domain,
			ConflictDecision conflictDecision, Long limit);
	
	/**
	 * Splits a cluster
	 * @param integrationSet
	 * @param integratedCluster
	 * @param splitStrategy
	 * @param name
	 * @param description
	 * @return
	 */
	public List<IntegratedCluster> splitCluster(
			IntegrationSet integrationSet,
			IntegratedCluster integratedCluster,
			SplitStrategy splitStrategy,
			String name, String description
			);
	
	public IntegratedCluster updateCluster(
			IntegratedCluster integratedCluster, 
			String name, 
			Set<Long> members, 
			String description);
	
	public IntegratedCluster mergeCluster(
			IntegrationSet integrationSet, 
			Set<Long> cidList, 
			String name, 
			Set<Long> members, 
			String description);
	
	public void deleteCluster(IntegrationSet integrationSet, Long cid);
	
	public List<Long> listAllIntegratedCompounds(IntegrationSet integrationSet);
	public List<Long> listAllUnintegratedCompounds(IntegrationSet integrationSet);
	
	@Deprecated
	public IntegratedCluster createCluster(String query);
	@Deprecated
	public IntegratedCluster createCluster(IntegrationSet integrationSet, ClusteringStrategy strategy);
	
	@Deprecated
	public IntegratedCluster mergeCluster(String query);
	@Deprecated
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy);
	@Deprecated
	public IntegratedCluster mergeCluster(String name, Set<Long> elements, String description);
	
	@Deprecated
	public Map<Long, IntegratedCluster> splitCluster(Long cid, Set<Long> keep, String entry, String description);
	
	@Deprecated
	public void updateCluster(Long cid, String entry, String description, Set<Long> elements);
	

	@Deprecated
	public IntegrationSet getCurrentIntegrationSet();
	public Map<String, Integer> getDataStatistics();

	public List<IntegratedCluster> getAllMetaboliteIntegratedClusterEntries(Long iid);
	public List<IntegratedCluster> getAllReactionIntegratedClusterEntries(Long iid);
	public Map<Long, Long> getMetaboliteUnificationMap(long iid);
	
	public IntegratedMember getIntegratedMemberByReferenceEid(long referenceEid);
}
