package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMeta;

public interface IntegrationMetaService {
	/**
	 * Generates Meta Information
	 * @param integratedCluster
	 * @return
	 */
	public List<IntegratedClusterMeta> generateMeta(IntegratedCluster integratedCluster);
	
	/**
	 * Updates integrated cluster meta information
	 * @param integratedCluster
	 */
	public void updatedMeta(IntegratedCluster integratedCluster);
	
	public List<IntegratedClusterMeta> getMeta(IntegratedCluster integratedCluster);
}
