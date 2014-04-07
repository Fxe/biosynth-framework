package edu.uminho.biosynth.core.data.integration.chimera.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraDataDao;
import edu.uminho.biosynth.core.data.integration.chimera.dao.ChimeraMetadataDao;
import edu.uminho.biosynth.core.data.integration.chimera.domain.CompositeMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedClusterMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedMember;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.generator.IKeyGenerator;

@Service
@Transactional(readOnly=true, value="chimerametadata")
public class ChimeraIntegrationServiceImpl implements ChimeraIntegrationService{

	private static Logger LOGGER = Logger.getLogger(ChimeraIntegrationServiceImpl.class);
	
	@Autowired
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
		Map<Long, Long> assignedMembers = this.meta.getAllAssignedIntegratedMembers(this.currentIntegrationSet.getId());
		List<Long> memberIdList = new ArrayList<> (assignedMembers.keySet());
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		memberIdList.retainAll(clusterElements);
		if (!memberIdList.isEmpty()) {
			LOGGER.warn(String.format("Create Cluster [%s] FAILED. Membership conflict %s", query, memberIdList));
			return null;
		}
		LOGGER.debug(String.format("[%s] generated %d elements", query, clusterElements.size()));
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
	
	public List<IntegratedCluster> createClusterCascade(ClusteringStrategy strategy, List<Long> elementsToCascade) {
		List<IntegratedCluster> res = new ArrayList<> ();

		List<Long> visitedIds = new ArrayList<> ();
		for (Long id: elementsToCascade) {
			if (!visitedIds.contains(id)) {
				strategy.setInitialNode(id);
				IntegratedCluster cluster = this.createCluster(strategy);
				for (IntegratedClusterMember elements:cluster.getMembers()) {
					visitedIds.add(elements.getMember().getId());
				}
				res.add(cluster);
//				this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query_);
				System.out.println("Generated Cluster: root -> " + id + " size -> " + cluster.getMembers().size());
			}
		}
		
		return res;
	}

	@Override
	public IntegratedCluster mergeCluster(String query) {
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, query);
//		this.meta.createCluster(, clusterElements, query, currentIntegrationSet);
		
	}
	@Override
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy) {
		Map<Long, Long> assignedMembers = this.meta.getAllAssignedIntegratedMembers(this.currentIntegrationSet.getId());
		List<Long> memberIdList = new ArrayList<> (assignedMembers.keySet());
		List<Long> clusterElements = strategy.execute();
		memberIdList.retainAll(clusterElements);
		Set<Long> joinClusterList = new HashSet<> ();
		if (!memberIdList.isEmpty()) {
			LOGGER.warn(String.format("Merge Cluster [%s] WITH. Membership conflict %s performing cluster join.", strategy.toString(), memberIdList));
			
			for (Long memberId: memberIdList) {
				joinClusterList.add(assignedMembers.get(memberId));
//				LOGGER.warn(String.format("Cluster %d marked as candidate", memberIdList));
			}
			for (Long clusterId: joinClusterList) {
//				IntegratedCluster cluster = this.meta.getIntegratedCluster();
				//ADD ALL IDS TO clusterElements
				//DELETE CLUSTER
			}
			
		}
		LOGGER.debug(String.format("%s obtained %d elements", strategy, clusterElements.size()));
		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, strategy.toString());
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
		for (String property: this.data.getAllProperties()) {
			res.put(property, this.data.listAllPropertyIds(property).size());
		}
		System.out.println("RETURNED THIS $$$$$$$$$$" + res);
		return res;
	}
	
	@Override
	public IntegratedCluster createCluster(ClusteringStrategy strategy) {
		Map<Long, Long> assignedMembers = this.meta.getAllAssignedIntegratedMembers(this.currentIntegrationSet.getId());
		List<Long> memberIdList = new ArrayList<> (assignedMembers.keySet());
		List<Long> clusterElements = strategy.execute();
		memberIdList.retainAll(clusterElements);
		if (!memberIdList.isEmpty()) {
			LOGGER.warn(String.format("Create Cluster [%s] FAILED. Membership conflict %s", strategy.toString(), memberIdList));
			return null;
		}
		LOGGER.debug(String.format("[%s] generated %d elements", strategy, clusterElements.size()));
		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, strategy.toString()); 
	}
	
	
	public List<IntegratedCluster> splitClusterByProperty(Long clusterId, String property, String field) {
		List<IntegratedCluster> res = new ArrayList<> ();
		IntegratedCluster cluster = this.meta.getIntegratedClusterById(clusterId);
		Map<Object, Set<Long>> propertyMemberMap = new HashMap<> ();
		for (IntegratedClusterMember members : cluster.getMembers()) {
			Long memberId = members.getMember().getId();
			CompositeMetaboliteEntity cpd = this.data.getCompositeMetabolite(memberId);
			if (cpd.getProperties().containsKey(property)) {
				String value = (String) cpd.getProperties().get(property).get(field);
				if (!propertyMemberMap.containsKey(value)) {
					propertyMemberMap.put(value, new HashSet<Long> ());
				}
				propertyMemberMap.get(value).add(memberId);
			}
		}
		
		System.out.println(propertyMemberMap);
		return res;
	}
}
