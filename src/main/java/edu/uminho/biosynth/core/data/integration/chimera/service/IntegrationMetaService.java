package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;

import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMeta;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;

public interface IntegrationMetaService {
	/**
	 * Generates Meta Information
	 * @param integratedCluster
	 * @return
	 */
	public List<IntegratedClusterMeta> generateMeta(IntegratedMetaboliteEntity integratedMetaboliteEntity);
	
	/**
	 * Updates integrated cluster meta information
	 * @param integratedCluster
	 * @param integratedClusterMetas
	 */
	public void updatedMeta(IntegratedCluster integratedCluster, List<IntegratedClusterMeta> integratedClusterMetas);
	
	public List<IntegratedClusterMeta> getMeta(IntegratedCluster integratedCluster);

	
}
