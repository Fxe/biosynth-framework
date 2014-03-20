package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

public interface ChimeraMetadataDao {
	public List<Serializable> getAllIntegrationSetsId();
	
	public void saveIntegrationSet(IntegrationSet integrationSet);
	public void deleteIntegrationSet(IntegrationSet integrationSet);
	
	public IntegrationSet getIntegrationSet(Serializable id);
	public IntegrationSet getIntegrationSet(String id);
	
	public IntegratedCluster createCluster(String name, List<Long> ids, String description, IntegrationSet integrationSet);
	public void mergeCluster(List<Long> ids, Serializable integrationId);
	
	public void deleteCluster(IntegratedCluster cluster);
}
