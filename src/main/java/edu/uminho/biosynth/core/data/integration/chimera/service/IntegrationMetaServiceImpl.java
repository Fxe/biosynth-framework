package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		Map<String, IntegratedClusterMeta> metaMap = new HashMap<> ();
		for (IntegratedClusterMeta integratedClusterMeta : integratedClusterMetas) {
			metaMap.put(integratedClusterMeta.getMetaType(), integratedClusterMeta);
		}
		
		Set<String> unmodified = new HashSet<> ();
		Set<String> delete = new HashSet<> ();
		for (String metaType : integratedCluster.getMeta().keySet()) {
			if (metaMap.containsKey(metaType)) {
				unmodified.add(metaType);
			} else {
				delete.add(metaType);
			}
		}
		
		for (String metaType : unmodified) metaMap.remove(metaType);
		for (String metaType : delete) integratedCluster.getMeta().remove(metaType);
		
		for (String metaType : metaMap.keySet()) {
			IntegratedClusterMeta integratedClusterMeta = metaMap.get(metaType); 
			integratedClusterMeta.setIntegratedCluster(integratedCluster);
			integratedCluster.getMeta().put(metaType, integratedClusterMeta);
		}
		
		this.meta.saveIntegratedCluster(integratedCluster);
	}

	@Override
	public List<IntegratedClusterMeta> getMeta(
			IntegratedCluster integratedCluster) {
		// TODO Auto-generated method stub
		return null;
	}

}
