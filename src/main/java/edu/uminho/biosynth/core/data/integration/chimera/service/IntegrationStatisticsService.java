package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.Map;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

public interface IntegrationStatisticsService {
	
	public int countTotalMetaboliteMembers();
	public int countIntegratedMetaboliteMembers(IntegrationSet integrationSet);
	public Map<String, Integer> countTotalMetaboliteMembersByMajor();
	public Map<String, Integer> countIntegratedMetaboliteMembersByMajor(IntegrationSet integrationSet);
	
	public Map<Integer, Integer> getIntegratedClusterPropertyFrequency(
			IntegrationSet integrationSet, String property);
	
	//CrossReferences ignored per db
	
	//
}
