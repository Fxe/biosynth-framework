package edu.uminho.biosynth.core.data.integration.chimera.service;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;

public interface ChimeraDatabaseBuilderService {
	public void changeIntegrationSet(Long id);
	public void resetTarget();
	public void generateIntegratedDatabase();
	public IntegratedMetaboliteEntity buildCompoundByClusterName(String clusterName);
	public IntegratedMetaboliteEntity buildCompoundByClusterMemberId(Long memberId);
	public IntegratedMetaboliteEntity buildCompoundByClusterId(Long id);
}
