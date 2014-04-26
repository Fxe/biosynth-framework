package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

public interface ChimeraIntegrationService {
	public IntegrationSet createNewIntegrationSet(String name, String description);
	public List<IntegrationSet> getAllIntegrationSets();
	public IntegrationSet changeIntegrationSet(Long id);
	public IntegrationSet changeIntegrationSet(String id);
	public void resetIntegrationSet();
	public void deleteIntegrationSet();
	
	public List<IntegratedCluster> pageClusters(Long iid, int firstResult, int maxResults);
	public int countIntegratedClustersByIntegrationId(Long iid);
	
	public IntegratedCluster createCluster(String query);
	public IntegratedCluster createCluster(ClusteringStrategy strategy);
	public IntegratedCluster createCluster(String name, List<Long> elements, String description);
	
	public IntegratedCluster mergeCluster(String query);
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy);
	public IntegratedCluster mergeCluster(String name, List<Long> elements, String description);
	
	public Map<Long, IntegratedCluster> splitCluster(Long cid, Set<Long> keep, String entry, String description);
	
	public void updateCluster(Long cid, String entry, String description, Set<Long> elements);
	
	public List<Long> listAllIntegratedCompounds();
	public List<Long> listAllUnintegratedCompounds();
	
	public IntegrationSet getCurrentIntegrationSet();
	public Map<String, Integer> getDataStatistics();
}
