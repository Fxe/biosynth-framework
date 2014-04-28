package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.SplitStrategy;

public interface ChimeraIntegrationService {
	
	public IntegrationSet getIntegrationSetByEntry(String entry);
	public IntegrationSet getIntegrationSetById(Long id);
	public IntegrationSet createNewIntegrationSet(String name, String description);
	public void resetIntegrationSet(IntegrationSet integrationSet);
	public void deleteIntegrationSet(IntegrationSet integrationSet);
	public List<IntegrationSet> getAllIntegrationSets();
	
	public List<IntegratedCluster> pageClusters(Long iid, int firstResult, int maxResults);
	public int countIntegratedClustersByIntegrationId(Long iid);
	
	public List<IntegratedCluster> generateIntegratedClusters(Long iid, ClusteringStrategy clusteringStrategy, 
			Set<Long> initial, Set<Long> domain, ConflictDecision conflictDecision);
	
	public IntegratedCluster createCluster(
			IntegrationSet integrationSet, 
			String name, Set<Long> members, 
			String description, ConflictDecision conflictDecision);
	
	public List<IntegratedCluster> createCluster(
			IntegrationSet integrationSet,
			ClusteringStrategy clusteringStrategy,
			Set<Long> initial, Set<Long> domain,
			ConflictDecision conflictDecision, Long limit);
	
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
}
