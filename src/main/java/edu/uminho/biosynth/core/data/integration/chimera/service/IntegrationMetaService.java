package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynth.integration.IntegratedMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;

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

	/**
	 * Count all warnings of an integration set
	 * @param integrationSet
	 * @return
	 */
	public Map<String, Integer> countWarnings(IntegrationSet integrationSet);
}
