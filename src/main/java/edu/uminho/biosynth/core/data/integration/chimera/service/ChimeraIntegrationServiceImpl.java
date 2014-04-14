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
import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationCollectionUtilities;
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

	private IntegratedCluster mergeCluster(IntegratedCluster integratedCluster) {
		if (integratedCluster == null) return null;
		
		Set<Long> clusterMembers = new HashSet<> (integratedCluster.listAllIntegratedMemberIds());
		
		//Detect Collisions
		List<IntegratedCluster> collision = 
				this.meta.getIntegratedClusterByMemberIds(clusterMembers.toArray(new Long[0]));
		//Union All Collisions
		if (collision.isEmpty()) return integratedCluster;
		
		LOGGER.warn(String.format("MERGE with Membership conflict %s performing cluster join.", collision));
		
		if (collision.size() == 1) {
			IntegratedCluster cluster = collision.iterator().next();
			if (cluster.listAllIntegratedMemberIds().containsAll(clusterMembers)) {
				return cluster;
			}
		}
		
		for (IntegratedCluster c : collision) {
			clusterMembers.addAll(c.listAllIntegratedMemberIds());
			this.meta.deleteCluster(c);
		}
		
		IntegratedCluster union = this.generateCluster(integratedCluster.getName(), new ArrayList<> (clusterMembers), integratedCluster.getDescription());
		this.meta.saveIntegratedCluster(union);
		
		return union;
	}
	
	@Override
	public IntegratedCluster mergeCluster(String query) {
		IntegratedCluster integratedCluster = this.generateCluster(query);
		
		return this.mergeCluster(integratedCluster);
	}
	
	@Override
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy) {
		IntegratedCluster integratedCluster = this.generateCluster(strategy);
		
		return this.mergeCluster(integratedCluster);
		
		
//		Map<Long, Long> assignedMembers = this.meta.getAllAssignedIntegratedMembers(this.currentIntegrationSet.getId());
//		List<Long> memberIdList = new ArrayList<> (assignedMembers.keySet());
//		List<Long> clusterElements = strategy.execute();
//		memberIdList.retainAll(clusterElements);
//		Set<Long> joinClusterList = new HashSet<> ();
//		if (!memberIdList.isEmpty()) {
//			LOGGER.warn(String.format("Merge Cluster [%s] WITH. Membership conflict %s performing cluster join.", strategy.toString(), memberIdList));
//			
//			for (Long memberId: memberIdList) {
//				joinClusterList.add(assignedMembers.get(memberId));
////				LOGGER.warn(String.format("Cluster %d marked as candidate", memberIdList));
//			}
//			for (Long clusterId: joinClusterList) {
////				IntegratedCluster cluster = this.meta.getIntegratedCluster();
//				//ADD ALL IDS TO clusterElements
//				//DELETE CLUSTER
//			}
//			
//		}
//		LOGGER.debug(String.format("%s obtained %d elements", strategy, clusterElements.size()));
//		return this.createCluster(clusterIdGenerator.generateKey(), clusterElements, strategy.toString());
	}
	
	@Override
	public IntegratedCluster mergeCluster(String name, List<Long> elements,
			String description) {
		IntegratedCluster integratedCluster = this.generateCluster(name, elements, description);
		
		return this.mergeCluster(integratedCluster);
	}
	
	public List<IntegratedCluster> mergeClusterCascade(String query, List<Long> elementsToCascade) {
		List<Long> clusterIds = new ArrayList<> ();
		
		List<Long> visitedIds = new ArrayList<> ();
		for (Long id: elementsToCascade) {
			if (!visitedIds.contains(id)) {
				String query_ = String.format(query, id);
				IntegratedCluster integratedCluster = this.mergeCluster(query_);
				if (integratedCluster != null) {
					visitedIds.addAll(integratedCluster.listAllIntegratedMemberIds());
					clusterIds.add(integratedCluster.getId());
				}
			}
		}
		
		List<IntegratedCluster> res = new ArrayList<> ();
		for (Long clusterId : clusterIds) {
			IntegratedCluster integratedCluster = this.meta.getIntegratedClusterById(clusterId);
			if (integratedCluster != null) res.add(integratedCluster);
		}
		
		return res;
	}
	
	public List<IntegratedCluster> ai(ClusteringStrategy strategy, List<Long> elementsToCascade) {
		long start, end;
		Map<String, Set<Long>> tempClustersGenerated = new HashMap<> ();
		
System.out.println("Generating New Clusters");
		start = System.currentTimeMillis();
		Set<Long> visitedIds = new HashSet<> ();
		for (Long elementId : elementsToCascade) {
			if (!visitedIds.contains(elementId)) {
				strategy.setInitialNode(elementId);
				List<Long> clusterElements = strategy.execute();
				if (!clusterElements.isEmpty()) {
					visitedIds.addAll(clusterElements);
					String clusterEntry = this.clusterIdGenerator.generateKey();
					System.out.println(clusterEntry);
					tempClustersGenerated.put(clusterEntry, new HashSet<> (clusterElements));
				}
			}
		}
end = System.currentTimeMillis();
System.out.println("Ok ! [" + (end - start) + "]");
		
System.out.println("Resolving Conflicts");
System.out.println("Before " + tempClustersGenerated.keySet().size());
start = System.currentTimeMillis();
		Map<String, Set<Long>> resolvedClusters = IntegrationCollectionUtilities.resolveConflicts2(
				tempClustersGenerated);
		end = System.currentTimeMillis();
System.out.println("After " + resolvedClusters.keySet().size());
System.out.println("Ok ! [" + (end - start) + "]");

System.out.println("Loading Prev Clusters");
start = System.currentTimeMillis();
		for (IntegratedCluster integratedCluster : this.meta.getAllIntegratedClusters(this.currentIntegrationSet.getId())) {
			String clusterEntry = integratedCluster.getName();
			Set<Long> clustersElements = new HashSet<> (integratedCluster.listAllIntegratedMemberIds());
			resolvedClusters.put(clusterEntry, clustersElements);
		}
end = System.currentTimeMillis();
System.out.println("Ok ! [" + (end - start) + "]");

System.out.println("Resolving Db Conflicts");
System.out.println("Before " + resolvedClusters.keySet().size());
start = System.currentTimeMillis();
		Map<String, Set<Long>> finalClusters = IntegrationCollectionUtilities.resolveConflicts2(
				resolvedClusters);
end = System.currentTimeMillis();
System.out.println("After " + finalClusters.keySet().size());
System.out.println("Ok ! [" + (end - start) + "]");
		
System.out.println("Update Clusters");
start = System.currentTimeMillis();
		
end = System.currentTimeMillis();
System.out.println("Ok ! [" + (end - start) + "]");
		return null;
	}
	
	public List<IntegratedCluster> mergeClusterCascade(ClusteringStrategy strategy, List<Long> elementsToCascade) {
		Map<Long, Set<Long>> clusterIdToClusterElements = new HashMap<> ();
		//Newly generated Clusters
		Map<Long, Set<Long>> elementsToClusterIds_ = new HashMap<> ();
		//Previous generated Clusters shouldbe Long x Long
		Map<Long, Long> elementsToClusterIds = new HashMap<> ();
		
		Set<Long> visitedIds = new HashSet<> ();
		
		long i = 0; //this.meta.getLastClusterEntry(this.currentIntegrationSet.getId());
		for (Long elementId : elementsToCascade) {
			
			if (!visitedIds.contains(elementId)) {
				strategy.setInitialNode(elementId);
				List<Long> clusterElements = strategy.execute();
				
				if (!clusterElements.isEmpty()) {
				
						visitedIds.addAll(clusterElements);
						clusterIdToClusterElements.put(i++, new HashSet<> (clusterElements));
						for (Long eid : clusterElements) {
							elementsToClusterIds_.put(eid, new HashSet<Long> ());
						}
				
				}
//				LOGGER.trace(String.format("%d/%d", visitedIds.size(), elementsToCascade.size()));
			}
		}
		
		System.out.println("Done !");
		System.out.println("Solving Query Conflicts");
		
		Set<Long> deleted = new HashSet<> ();
		Map<Long, Long> elementsToClustersFixed = IntegrationCollectionUtilities.resolveConflicts(
				clusterIdToClusterElements, null, deleted);
		System.out.println("These died ... " + deleted);
		
		for (Long integratedClusterId : this.meta.getAllIntegratedClusterIds(this.currentIntegrationSet.getId())) {
			IntegratedCluster integratedCluster = this.meta.getIntegratedClusterById(integratedClusterId);
			for (Long eid : integratedCluster.listAllIntegratedMemberIds()) {
				elementsToClusterIds.put(eid, integratedClusterId);
			}
		}
		
		System.out.println(String.format("ElementsToClusterIds Size %d", elementsToClusterIds.size()));
		
		Map<Long, Set<Long>> conflictMapDbVsGenerated = new HashMap<> ();
		for (Long eid : elementsToClusterIds.keySet()) {
			conflictMapDbVsGenerated.put(eid, new HashSet<Long> ());
		}
		for (Long eid : elementsToClustersFixed.keySet()) {
			conflictMapDbVsGenerated.put(eid, new HashSet<Long> ());
		}
		for (Long eid : elementsToClusterIds.keySet()) {
			conflictMapDbVsGenerated.get(eid).add(elementsToClusterIds.get(eid));
		}
		for (Long eid : elementsToClustersFixed.keySet()) {
			conflictMapDbVsGenerated.get(eid).add(elementsToClustersFixed.get(eid));
		}
		
//		Map<Long, Long> finalClusters = IntegrationCollectionUtilities.resolveConflicts(
//				clusterIdToClusterElements, null, deleted);
//		System.out.println("These died ... " + deleted);

//		int progress = 0;
//		int total = elementsToCascade.size();
//		
//		List<Long> clusterIds = new ArrayList<> ();
//		
//		List<Long> visitedIds = new ArrayList<> ();
//		for (Long id: elementsToCascade) {
//			LOGGER.trace(String.format("%d/%d", ++progress, total));
//			if (!visitedIds.contains(id)) {
//				strategy.setInitialNode(id);
//				IntegratedCluster integratedCluster = this.mergeCluster(strategy);
//				if (integratedCluster != null) {
//					visitedIds.addAll(integratedCluster.listAllIntegratedMemberIds());
//					clusterIds.add(integratedCluster.getId());
//				}
//			}
//		}
//		
//		List<IntegratedCluster> res = new ArrayList<> ();
//		for (Long clusterId : clusterIds) {
//			IntegratedCluster integratedCluster = this.meta.getIntegratedClusterById(clusterId);
//			if (integratedCluster != null) res.add(integratedCluster);
//		}
		
		return null;
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
	
	@Override
	public List<Long> listAllIntegratedCompounds() {
		return this.meta.getAllIntegratedClusterMembersId();
	}
	
	@Override
	public List<Long> listAllUnintegratedCompounds() {
		Set<Long> res = new HashSet<> (this.data.getAllMetaboliteIds());
		Set<Long> integrated = new HashSet<> (this.listAllIntegratedCompounds());
		res.removeAll(integrated);
		return new ArrayList<> (res);
	}
	
	
	public IntegratedCluster generateCluster(String name, List<Long> elements, String description) {
		List<Long> clusterElements = elements;
		
		if (clusterElements.isEmpty()) return null;
		
		IntegratedCluster integratedCluster = new IntegratedCluster();
		integratedCluster.setIntegrationSet(this.currentIntegrationSet);
		integratedCluster.setName(name);
		integratedCluster.setDescription(description);
		
		for (Long memberId: clusterElements) {
			IntegratedMember member = new IntegratedMember();
			member.setId(memberId);
			this.meta.saveIntegratedMember(member);
			
			IntegratedClusterMember clusterMember = new IntegratedClusterMember();
			clusterMember.setCluster(integratedCluster);
			clusterMember.setMember(member);
			
			integratedCluster.getMembers().add(clusterMember);
		}
		
		return integratedCluster; 
	}
	
	public IntegratedCluster generateCluster(ClusteringStrategy clusteringStrategy) {
		List<Long> clusterElements = clusteringStrategy.execute();
		
		if (clusterElements.isEmpty()) return null;
		
		return this.generateCluster( this.clusterIdGenerator.generateKey(), clusterElements, clusteringStrategy.toString()); 
	}
	
	public IntegratedCluster generateCluster(String query) {
		List<Long> clusterElements = this.data.getClusterByQuery(query);
		
		if (clusterElements.isEmpty()) return null;

		return this.generateCluster( this.clusterIdGenerator.generateKey(), clusterElements, query); 
	}

}
