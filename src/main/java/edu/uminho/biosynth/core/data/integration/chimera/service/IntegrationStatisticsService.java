package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;

public interface IntegrationStatisticsService {
	
	public int countTotalMetaboliteMembers();
	public int countIntegratedMetaboliteMembers(IntegrationSet integrationSet);
	public Map<String, Integer> countTotalMetaboliteMembersByMajor();
	public Map<String, Integer> countIntegratedMetaboliteMembersByMajor(IntegrationSet integrationSet);
	

	public Map<Integer, Integer> getIntegratedClusterPropertyFrequency(
			IntegrationSet integrationSet, String property);
	
	Map<String, Integer> getIntegratedClusterDatabaseFreq(IntegratedCluster integratedCluster);
	
	//CrossReferences ignored per db
	
	//
}
