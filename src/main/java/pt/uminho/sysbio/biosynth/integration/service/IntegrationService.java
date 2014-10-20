package pt.uminho.sysbio.biosynth.integration.service;

import java.util.List;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

public interface IntegrationService {
	
	public List<String> getAllIntegrationSetsEntries();
	public List<Long> getAllIntegrationSetsIds();
	public IntegrationSet getIntegrationSetByEntry(String entry);
	public IntegrationSet getIntegrationSetById(Long id);
	public IntegrationSet createIntegrationSet(String name, String description);
	public void resetIntegrationSet(IntegrationSet integrationSet);
	public void deleteIntegrationSet(IntegrationSet integrationSet);
	public List<IntegrationSet> getAllIntegrationSets();
}
