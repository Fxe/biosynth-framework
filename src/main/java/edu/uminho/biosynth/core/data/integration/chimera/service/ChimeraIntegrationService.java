package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;
import java.util.Map;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

public interface ChimeraIntegrationService {
	public IntegrationSet createNewIntegrationSet(String name, String description);
	public List<IntegrationSet> getAllIntegrationSets();
	public void changeIntegrationSet(Long id);
	public void changeIntegrationSet(String id);
	public void resetIntegrationSet();
	public void deleteIntegrationSet();
	
	public IntegratedCluster createCluster(String query);
	public IntegratedCluster createCluster(ClusteringStrategy strategy);
	public IntegratedCluster createCluster(String name, List<Long> elements, String description);
	
	public IntegratedCluster mergeCluster(String query);
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy);
	public IntegratedCluster mergeCluster(String name, List<Long> elements, String description);
	
	public List<Long> listAllIntegratedCompounds();
	public List<Long> listAllUnintegratedCompounds();
	
	public IntegrationSet getCurrentIntegrationSet();
	public Map<String, Integer> getDataStatistics();
}
