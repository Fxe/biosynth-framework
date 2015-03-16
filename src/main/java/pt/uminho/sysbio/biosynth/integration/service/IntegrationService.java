package pt.uminho.sysbio.biosynth.integration.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedMember;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;

public interface IntegrationService {
	
	public List<String> getAllIntegrationSetsEntries();
	public List<Long> getAllIntegrationSetsIds();
	public IntegrationSet getIntegrationSetByEntry(String entry);
	public IntegrationSet getIntegrationSetById(Long id);
	public IntegrationSet createIntegrationSet(String name, String description);
	public void resetIntegrationSet(IntegrationSet integrationSet);
	public void deleteIntegrationSet(IntegrationSet integrationSet);
	public List<IntegrationSet> getAllIntegrationSets();
	
	public IntegratedCluster getIntegratedClusterById(long id);
	public IntegratedCluster getIntegratedClusterByEntry(String entry, long iid);
	
	public IntegratedMember getIntegratedMemberById(long id);
	public Map<IntegrationSet, Set<IntegratedCluster>> findIntegratedClusterByMemberReferenceId(long refId);
}
