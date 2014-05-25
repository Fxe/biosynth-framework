package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

public interface ChimeraMetadataDao {
	
	//IntegrationSet
	public List<Serializable> getAllIntegrationSetsId();
	public void saveIntegrationSet(IntegrationSet integrationSet);
	public void deleteIntegrationSet(IntegrationSet integrationSet);
	
	public IntegrationSet getIntegrationSet(Serializable id);
	public IntegrationSet getIntegrationSet(String id);
	
	public IntegratedCluster getIntegratedClusterByEntry(String entry, Long integrationSetId);
	public IntegratedCluster getIntegratedClusterById(Long id);
	public List<IntegratedCluster> getAllIntegratedClusters(Long integrationSetId);
	public List<IntegratedCluster> getIntegratedClustersByPage(Long integrationSetId, int firstResult, int maxResults);
	public List<IntegratedCluster> getIntegratedClusterByMemberIds(Long...memberIds);
	public List<Long> getAllIntegratedClusterIds(Long integrationSetId);
	public Set<Long> getAllIntegratedClusterIds(IntegrationSet integrationSetId);
	
	public IntegratedCluster saveIntegratedCluster(IntegratedCluster cluster);
	public void removeMembersFromIntegratedCluster(IntegratedCluster integratedCluster, Set<Long> toRemove);
	
	public void mergeCluster(List<Long> ids, Serializable integrationId);
	public void updateCluster(IntegratedCluster cluster);
	public void deleteCluster(IntegratedCluster cluster);
	public String getLastClusterEntry(Long integrationSetId);
	
	public void deleteClusterMember(IntegratedClusterMember member);
	public List<Long> getAllIntegratedClusterMembersId();
	
	/**
	 * 
	 * @param iid integration set id
	 * @param eid integrated member id
	 * @return map with integrated cluster cid and entry
	 */
	public Map<Long, String> getIntegratedClusterWithElement(Long iid, Long eid);
	
	public IntegratedMember getIntegratedMember(Long id);
	public IntegratedMember getOrCreateIntegratedMember(Long id);
	public void saveIntegratedMember(IntegratedMember member);
	public List<Long> getAllIntegratedMembersId();
	public List<Long> getAllIntegratedMembersId(IntegrationSet integrationSet, boolean distinct);
	public Map<Long, Long> getAllAssignedIntegratedMembers(Long integrationSetId);
	
	public int countIntegratedMembers(IntegrationSet integrationSet, boolean distinct);
	
	public Map<String, Map<String, Integer>> countMeta(Long iid);
	
}
