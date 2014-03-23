package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;

@Service
@Transactional(readOnly=true, value="chimerametadata")
public class ChimeraIntegrationServiceImpl implements ChimeraIntegrationService{
	
//	@Autowired
	private ChimeraDataDao data;
	@Autowired
	private ChimeraMetadataDao meta;
	
	private IntegrationSet currentIntegrationSet;
	
	@Override
	public IntegrationSet getCurrentIntegrationSet() { return currentIntegrationSet;}
	public void setCurrentIntegrationSet(IntegrationSet currentIntegrationSet) { this.currentIntegrationSet = currentIntegrationSet;}

	private IKeyGenerator<String> clusterIdGenerator;
	
	public ChimeraDataDao getData() { return data;}
	public void setData(ChimeraDataDao data) { this.data = data;}

	public ChimeraMetadataDao getMeta() { return meta;}
	public void setMeta(ChimeraMetadataDao meta) { this.meta = meta;}
	
	public IKeyGenerator<String> getClusterIdGenerator() { return clusterIdGenerator;}
	public void setClusterIdGenerator(IKeyGenerator<String> clusterIdGenerator) { this.clusterIdGenerator = clusterIdGenerator;}
	
	@Override
	public IntegrationSet createNewIntegrationSet(String name, String description) {
		//check if name exists
		//whine if exists
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName(name);
		integrationSet.setDescription(description);
		meta.saveIntegrationSet(integrationSet);
		
		return integrationSet;
	}

	@Override
	public void changeIntegrationSet(Long id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		this.currentIntegrationSet = integrationSet;
	}
	
	@Override
	public void changeIntegrationSet(String id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		this.currentIntegrationSet = integrationSet;
	}
	
	@Override
	public void resetIntegrationSet() {
		List<Long> clusters = new ArrayList<> (this.currentIntegrationSet.getIntegratedClustersMap().keySet());
		for (Long clusterId : clusters){
			IntegratedCluster cluster = this.currentIntegrationSet.getIntegratedClustersMap().remove(clusterId);
			this.meta.deleteCluster(cluster);
		}
		
//		this.meta.saveIntegrationSet(currentIntegrationSet);
	}

	@Override
	public void deleteIntegrationSet() {
		this.meta.deleteIntegrationSet(this.currentIntegrationSet);
	}
	
	public IntegratedCluster createCluster(String name, List<Long> elements, String description) {
		if (elements.isEmpty()) return null;
//		IntegratedCluster cluster = new IntegratedCluster();
//		cluster.setIntegrationSet(currentIntegrationSet);
//		
//		for (Long elementId : elements) {
//			IntegratedClusterMember member = new IntegratedClusterMember();
//			member.setIntegratedCluster(cluster);
//			member.setId(elementId);
//		}
		
		return this.meta.createCluster(name, elements, description, this.currentIntegrationSet);
	}

	@Override
	public void createCluster(String query) {
		//error id merge !
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		this.meta.createCluster(clusterIdGenerator.generateKey(), clusterElements, query, currentIntegrationSet);
	}
	
	public void createClusterCascade(String query) {
		List<Long> compoundIds = this.data.getAllMetaboliteIds();
		List<Long> visitedIds = new ArrayList<> ();
		for (Long id: compoundIds) {
			if (!visitedIds.contains(id)) {
				String query_ = String.format(query, id);
				System.out.println(query_);
				List<Long> clusterElements = this.data.getClusterByQuery(query_);
				visitedIds.addAll(clusterElements);
				this.meta.createCluster(clusterIdGenerator.generateKey(), clusterElements, query_, currentIntegrationSet);
				System.out.println("Generated Cluster: root -> " + id + " size -> " + clusterElements.size());
			}
		}
		//error id merge !
	}

	@Override
	public void mergeCluster(String query) {
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		this.meta.createCluster(clusterIdGenerator.generateKey(), clusterElements, query, currentIntegrationSet);
		
	}
	@Override
	public void mergeCluster(ClusteringStrategy strategy) {
		List<Long> clusterElements = strategy.execute();
		this.meta.createCluster(clusterIdGenerator.generateKey(), clusterElements, strategy.toString(), currentIntegrationSet);
	}
	
	@Override
	public List<IntegrationSet> getAllIntegrationSets() {
		List<IntegrationSet> res = new ArrayList<> ();
		for (Serializable id: this.meta.getAllIntegrationSetsId()) {
			res.add(this.meta.getIntegrationSet(id));
		}
		return res;
	}
	
	@Override
	public Map<String, Integer> getDataStatistics() {
		Map<String, Integer> res = new HashMap<> ();
//		for (String property: this.data.getAllProperties()) {
//			res.put(property, this.data.listAllPropertyIds(property).size());
//		}
		return res;
	}
}
