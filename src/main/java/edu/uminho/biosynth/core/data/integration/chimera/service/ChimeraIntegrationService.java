package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

public interface ChimeraIntegrationService {
	
	public IntegrationSet getIntegrationSetByEntry(String entry);
	public IntegrationSet getIntegrationSetById(Long id);
	public IntegrationSet createNewIntegrationSet(String name, String description);
	public void resetIntegrationSet(IntegrationSet integrationSet);
	public void deleteIntegrationSet(IntegrationSet integrationSet);
	public List<IntegrationSet> getAllIntegrationSets();
	
	public IntegrationSet changeIntegrationSet(Long id);
	public IntegrationSet changeIntegrationSet(String id);
	
	
	public List<IntegratedCluster> pageClusters(Long iid, int firstResult, int maxResults);
	public int countIntegratedClustersByIntegrationId(Long iid);
	
	public List<IntegratedCluster> generateIntegratedClusters(Long iid, ClusteringStrategy clusteringStrategy, 
			Set<Long> initial, Set<Long> domain, ConflictDecision conflictDecision);
	
	public IntegratedCluster createCluster(String query);
	public IntegratedCluster createCluster(ClusteringStrategy strategy);
	public IntegratedCluster createCluster(String name, Set<Long> elements, String description);
	
	public IntegratedCluster mergeCluster(String query);
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy);
	public IntegratedCluster mergeCluster(String name, Set<Long> elements, String description);
	
	public List<Long> listAllIntegratedCompounds();
	public List<Long> listAllUnintegratedCompounds();
	
	public IntegrationSet getCurrentIntegrationSet();
	public Map<String, Integer> getDataStatistics();
}
