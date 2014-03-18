package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;

@Service
public class ChimeraIntegrationServiceImpl implements ChimeraIntegrationService{
	
	@Autowired
	private ChimeraDataDao data;
	@Autowired
	private ChimeraMetadataDao meta;
	
	private IntegrationSet currentIntegrationSet;
	
	public ChimeraDataDao getData() { return data;}
	public void setData(ChimeraDataDao data) { this.data = data;}

	public ChimeraMetadataDao getMeta() { return meta;}
	public void setMeta(ChimeraMetadataDao meta) { this.meta = meta;}
	
	@Override
	public void createNewIntegrationSet(String name, String description) {
		//check if name exists
		//whine if exists
		IntegrationSet integrationSet = new IntegrationSet();
		integrationSet.setName(name);
		integrationSet.setDescription(description);
		meta.saveIntegrationSet(integrationSet);
	}

	@Override
	public void changeIntegrationSet(Long id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		this.currentIntegrationSet = integrationSet;
	}

	@Override
	public void resetIntegrationSet() {
		for (Long clusterId : this.currentIntegrationSet.getIntegratedClustersMap().keySet()){
			IntegratedCluster cluster = this.currentIntegrationSet.getIntegratedClustersMap().get(clusterId);
			this.meta.deleteCluster(cluster);
		}
	}

	@Override
	public void deleteIntegrationSet() {
		this.meta.deleteIntegrationSet(this.currentIntegrationSet);
	}

	@Override
	public void createCluster(String query) {
		//error id merge !
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		this.meta.createCluster(clusterElements, query, currentIntegrationSet);
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
				this.meta.createCluster(clusterElements, query_, currentIntegrationSet);
				System.out.println("Generated Cluster: root -> " + id + " size -> " + clusterElements.size());
			}
		}
		//error id merge !
	}

	@Override
	public void mergeCluster(String query) {
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		this.meta.createCluster(clusterElements, query, currentIntegrationSet);
	}
}
