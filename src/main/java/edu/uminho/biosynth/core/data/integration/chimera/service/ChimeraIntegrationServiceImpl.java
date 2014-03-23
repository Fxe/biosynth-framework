package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;

@Service
public class ChimeraIntegrationServiceImpl implements ChimeraIntegrationService{
	
	private static Logger LOGGER = Logger.getLogger(ChimeraIntegrationServiceImpl.class);
	
	@Autowired
	private ChimeraDataDao data;
	@Autowired
	private ChimeraMetadataDao meta;
	
	private IntegrationSet currentIntegrationSet;
	
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
		LOGGER.info(String.format("Current Integration Set changed to %s", this.currentIntegrationSet));
	}
	
	@Override
	public void changeIntegrationSet(String id) {
		IntegrationSet integrationSet = this.meta.getIntegrationSet(id);
		this.currentIntegrationSet = integrationSet;
	}
	
	@Override
	public void resetIntegrationSet() {
		if (this.currentIntegrationSet == null) {
			LOGGER.warn("No Integration Set selected - operation aborted");
			return;
		}
		LOGGER.info(String.format("Reset atempt to Integration Set %s", this.currentIntegrationSet));
		
		List<Long> clusters = new ArrayList<> (this.currentIntegrationSet.getIntegratedClustersMap().keySet());
		for (Long clusterId : clusters){
			IntegratedCluster cluster = this.currentIntegrationSet.getIntegratedClustersMap().remove(clusterId);
			LOGGER.info(String.format("Removing cluster %s", cluster));
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

		List<Long> clusterElements = elements;
		IntegratedCluster cluster = new IntegratedCluster();
		cluster.setIntegrationSet(this.currentIntegrationSet);
		cluster.setName(name);
		cluster.setDescription(description);
		
		for (Long memberId: clusterElements) {
			IntegratedMember member = new IntegratedMember();
			member.setId(memberId);
			this.meta.saveIntegratedMember(member);
			
			IntegratedClusterMember clusterMember = new IntegratedClusterMember();
			clusterMember.setCluster(cluster);
			clusterMember.setMember(member);
			
			cluster.getMembers().add(clusterMember);
		}
		
		this.meta.saveIntegratedCluster(cluster);
		
		return cluster;
	}

	@Override
	public IntegratedCluster createCluster(String query) {
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		//error id merge !
		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query);
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
				this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query_);
				System.out.println("Generated Cluster: root -> " + id + " size -> " + clusterElements.size());
			}
		}
		//error id merge !
	}

	@Override
	public void mergeCluster(String query) {
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query);
//		this.meta.createCluster(, clusterElements, query, currentIntegrationSet);
		
	}
	@Override
	public void mergeCluster(ClusteringStrategy strategy) {
		List<Long> clusterElements = strategy.execute();
		this.createCluster(clusterIdGenerator.generateKey(), clusterElements, strategy.toString());
	}
}
