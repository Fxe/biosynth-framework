package pt.uminho.sysbio.biosynth.integration.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.uminho.sysbio.biosynth.integration.io.dao.MetaboliteHeterogeneousDao;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultBinaryEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.UndirectedGraph;
import pt.uminho.sysbio.metropolis.network.graph.algorithm.BreadthFirstSearch;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegratedCluster;
import edu.uminho.biosynth.core.data.integration.chimera.domain.IntegrationSet;
import edu.uminho.biosynth.core.data.integration.chimera.service.ConflictDecision;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.SplitStrategy;

@Service
@Transactional(readOnly=true)
public class DefaultMetaboliteIntegrationServiceImpl<M extends Metabolite> 
extends BasicIntegrationService
implements MetaboliteIntegrationService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMetaboliteIntegrationServiceImpl.class);
	
	@Autowired
	private MetaboliteHeterogeneousDao<M> data;
	
	public MetaboliteHeterogeneousDao<M> getData() { return data;}
	public void setData(MetaboliteHeterogeneousDao<M> data) { this.data = data;}
	
	@Override
	public List<IntegratedCluster> pageClusters(Long iid, int firstResult,
			int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int countIntegratedClustersByIntegrationId(Long iid) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<IntegratedCluster> generateIntegratedClusters(Long iid,
			ClusteringStrategy clusteringStrategy, Set<Long> initial,
			Set<Long> domain, ConflictDecision conflictDecision) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IntegratedCluster createCluster(IntegrationSet integrationSet,
			String name, Set<Long> members, String description,
			ConflictDecision conflictDecision) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<IntegratedCluster> createCluster(IntegrationSet integrationSet,
			ClusteringStrategy clusteringStrategy, Set<Long> initial,
			Set<Long> domain, ConflictDecision conflictDecision, Long limit) {
		
		if (limit == null) limit = Long.MAX_VALUE;
		int i = 0;
		List<IntegratedCluster> integratedClusters = new ArrayList<> ();
		
		//Generate Clusters
		List<Set<Long>> generatedClusters = this.generateClusters(clusteringStrategy, initial, domain);
		LOGGER.info(String.format("Generated an initial of %d clusters.", generatedClusters.size()));
		List<Set<Long>> uniqueMembershipClusters = this.resolveMembershipConflict(generatedClusters);
		LOGGER.info(String.format("Resolved initial clusters merge. Clusters reduced to %d from %d", uniqueMembershipClusters.size(), generatedClusters.size()));
//		Map<Long, Set<Long>> prevClusters = new HashMap<> ();
//		for (IntegratedCluster integratedCluster : this.meta.getAllIntegratedClusters(integrationSet.getId())) {
//			Long cid = integratedCluster.getId();
//			Set<Long> clustersElements = new HashSet<> (integratedCluster.listAllIntegratedMemberIds());
//			prevClusters.put(cid, clustersElements);
//		}
//		LOGGER.info(String.format("Resolved initial clusters merge. Clusters reduced to %d from %d", uniqueMembershipClusters.size(), generatedClusters.size()));
		
		return integratedClusters;
	}
	@Override
	public List<IntegratedCluster> splitCluster(IntegrationSet integrationSet,
			IntegratedCluster integratedCluster, SplitStrategy splitStrategy,
			String name, String description) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IntegratedCluster updateCluster(IntegratedCluster integratedCluster,
			String name, Set<Long> members, String description) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IntegratedCluster mergeCluster(IntegrationSet integrationSet,
			Set<Long> cidList, String name, Set<Long> members,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void deleteCluster(IntegrationSet integrationSet, Long cid) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<Long> listAllIntegratedCompounds(IntegrationSet integrationSet) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Long> listAllUnintegratedCompounds(IntegrationSet integrationSet) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IntegratedCluster createCluster(String query) {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public IntegratedCluster createCluster(IntegrationSet integrationSet,
			ClusteringStrategy strategy) {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public IntegratedCluster mergeCluster(String query) {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public IntegratedCluster mergeCluster(ClusteringStrategy strategy) {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public IntegratedCluster mergeCluster(String name, Set<Long> elements,
			String description) {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public Map<Long, IntegratedCluster> splitCluster(Long cid, Set<Long> keep,
			String entry, String description) {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public void updateCluster(Long cid, String entry, String description,
			Set<Long> elements) {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public IntegrationSet getCurrentIntegrationSet() {
		throw new RuntimeException("DEPRECATED");
	}
	@Override
	public Map<String, Integer> getDataStatistics() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<Set<Long>> generateClusters(
			ClusteringStrategy clusteringStrategy,
			Set<Long> initial, Set<Long> domain) {
		
		List<Set<Long>> generatedClusters = new ArrayList<> ();
		
		Set<Long> visitedIds = new HashSet<> ();
		for (Long i : initial) {
			if (!visitedIds.contains(i)) {
				clusteringStrategy.setInitialNode(i);
				Set<Long> clusterElements = clusteringStrategy.execute();
				LOGGER.debug(String.format("%s generated %d members from %d", clusteringStrategy.getClass().getSimpleName(), clusterElements.size(), i));
				clusterElements.retainAll(domain);
				if (!clusterElements.isEmpty()) {
					visitedIds.addAll(clusterElements);
					generatedClusters.add(clusterElements);
				}
			}
		}
		
		return generatedClusters;
	}
	
	private List<Set<Long>> resolveMembershipConflict(List<Set<Long>> clusterList) {
		List<Set<Long>> uniqueMembershipClusters = new ArrayList<> ();
		
		UndirectedGraph<Long, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		Set<Long> eids = new HashSet<> ();
		for (Set<Long> cluster : clusterList) {
			Long prev = null;
			if (cluster.size() > 1) {
				for (Long eid : cluster) {
					eids.add(eid);
					if (prev != null) {
						DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
						graph.addEdge(edge);
					}
					prev = eid;
				}
			} else {
				for (Long eid : cluster) {
					eids.add(eid);
					graph.addVertex(eid);
				}
			}
		}
		
		Set<Long> eidsProcessed = new HashSet<> ();
		for (Long eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<Long> cluster = BreadthFirstSearch.run(graph, eid);
				eidsProcessed.addAll(cluster);
				uniqueMembershipClusters.add(cluster);
			}
		}
		
		return uniqueMembershipClusters;
	}
}
