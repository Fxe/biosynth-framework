package edu.uminho.biosynth.core.data.integration.chimera.service;


public interface ChimeraIntegrationService {
	public void createNewIntegrationSet(String name, String description);
	public void changeIntegrationSet(Long id);
	public void resetIntegrationSet();
	public void deleteIntegrationSet();
	public void createCluster(String query);
	public void mergeCluster(String query);
}
