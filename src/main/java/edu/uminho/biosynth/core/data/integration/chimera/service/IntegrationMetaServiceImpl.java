package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynth.integration.IntegratedMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.IntegrationSet;
import pt.uminho.sysbio.biosynth.integration.io.dao.IntegrationMetadataDao;
import edu.uminho.biosynth.core.data.integration.IntegrationMessageLevel;
import edu.uminho.biosynth.core.data.integration.chimera.IntegratedClusterMetaGenerator;
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationDataDao;

@Service
@Transactional(readOnly=true, value="chimerametadata")
public class IntegrationMetaServiceImpl implements IntegrationMetaService {

	@Autowired
	private IntegrationDataDao data;
	
	@Autowired
	private IntegrationMetadataDao meta;
	
	private List<IntegratedClusterMetaGenerator> generators = new ArrayList<> ();
	
	public IntegrationDataDao getData() {
		return data;
	}

	public void setData(IntegrationDataDao data) {
		this.data = data;
	}

	public IntegrationMetadataDao getMeta() {
		return meta;
	}

	public void setMeta(IntegrationMetadataDao meta) {
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

	@Override
	public Map<String, Integer> countWarnings(IntegrationSet integrationSet) {
		Map<String, Map<String, Integer>> freqMap =  this.meta.countMeta(integrationSet.getId());
		
		if (freqMap.containsKey(IntegrationMessageLevel.WARNING.toString())) 
				return freqMap.get(IntegrationMessageLevel.WARNING.toString());
		
		return new HashMap<String, Integer> ();
	}

}
