package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

public interface ChimeraMetadataDao {
	
	//IntegrationSet
	public List<Serializable> getAllIntegrationSetsId();
	public void saveIntegrationSet(IntegrationSet integrationSet);
	public void deleteIntegrationSet(IntegrationSet integrationSet);
	
	public IntegrationSet getIntegrationSet(Serializable id);
	public IntegrationSet getIntegrationSet(String id);
	
	public List<Long> getAllIntegratedClusterIds(Long integrationSetId);
	public void saveIntegratedCluster(IntegratedCluster cluster);
	public void mergeCluster(List<Long> ids, Serializable integrationId);
	public IntegratedCluster getIntegratedClusterByName(String name, Long integrationSetId);
	public IntegratedCluster getIntegratedClusterById(Long id);
	public void deleteCluster(IntegratedCluster cluster);
	
	public IntegratedMember getIntegratedMember(Long id);
	public void saveIntegratedMember(IntegratedMember member);
	public List<Long> getAllIntegratedMembersId();
	public Map<Long, Long> getAllAssignedIntegratedMembers(Long integrationSetId);
}
