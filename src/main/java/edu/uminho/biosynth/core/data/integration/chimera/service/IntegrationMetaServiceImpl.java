package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.ArrayList;
import java.util.List;

import edu.uminho.biosynth.core.data.integration.chimera.IntegratedClusterMetaGenerator;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMeta;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMetaboliteEntity;

public class IntegrationMetaServiceImpl implements IntegrationMetaService {

	private ChimeraDataDao data;
	private ChimeraMetadataDao meta;
	
	private List<IntegratedClusterMetaGenerator> generators = new ArrayList<> ();
	
	public ChimeraDataDao getData() {
		return data;
	}

	public void setData(ChimeraDataDao data) {
		this.data = data;
	}

	public ChimeraMetadataDao getMeta() {
		return meta;
	}

	public void setMeta(ChimeraMetadataDao meta) {
		this.meta = meta;
	}
	
	

	public List<IntegratedClusterMetaGenerator> getGenerators() {
		return generators;
	}

	public void setGenerators(List<IntegratedClusterMetaGenerator> generators) {
		this.generators = generators;
	}

	@Override
	public List<IntegratedClusterMeta> generateMeta(
			IntegratedMetaboliteEntity integratedMetaboliteEntity) {
		
		List<IntegratedClusterMeta> integratedClusterMetas = new ArrayList<> ();
		for (IntegratedClusterMetaGenerator generator : generators) {
			integratedClusterMetas.addAll(generator.generateMeta(integratedMetaboliteEntity));
		}
//		System.out.println(integratedCluster.listAllIntegratedMemberIds());		
		
//		data.get
		
		return integratedClusterMetas;
	}
	
	

	@Override
	public void updatedMeta(IntegratedCluster integratedCluster, List<IntegratedClusterMeta> integratedClusterMetas) {
		
	}

	@Override
	public List<IntegratedClusterMeta> getMeta(
			IntegratedCluster integratedCluster) {
		// TODO Auto-generated method stub
		return null;
	}

}
