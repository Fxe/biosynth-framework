package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.algorithm.graph.BreadthFirstSearch;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.UndirectedGraph;

public class IntegrationCollectionUtilities {

	/**
	 * Resolve conflicts between clusters.
	 * Let C be a cluster defined by a set of elements E. Every cluster that 
	 * shares the same element must be merged together.
	 * 
	 * Example: C<sub>1</sub> = [a, c, f], C<sub>2</sub> = [h, k], C<sub>3</sub> = [a, n, m],
	 * then C<sub>1</sub> must be merged with C<sub>3</sub> since they have the <b>a</b>
	 *  elements in common.
	 * 
	 * @param clusterMap is the map that maps cluster id to the set of its elements.
	 * @param elementToClusterMap is the map that maps elements to clusters that they belong.
	 * @return a map that maps each element to a single cluster
	 */
	public static Map<Long, Long> resolveConflicts(Map<Long, Set<Long>> clusterMap, Map<Long, Set<Long>> elementToClusterMap) {
		Map<Long, Long> res = new HashMap<> ();
		Set<Long> eids = new HashSet<> ();
		UndirectedGraph<Long, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		for (Long cid : clusterMap.keySet()) {
			Long prev = null;
			for (Long eid : clusterMap.get(cid)) {
				eids.add(eid);
				if (prev != null) {
					DefaultBinaryEdge<Integer, Long> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
					graph.addEdge(edge);
				}
				prev = eid;
			}
		}
		
		Set<Long> eidsProcessed = new HashSet<> ();
		for (Long eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<Long> cluster = BreadthFirstSearch.run(graph, eid);
				eidsProcessed.addAll(cluster);
				Long cid = elementToClusterMap.get(cluster.iterator().next()).iterator().next();
				for (Long eid_ : cluster) {
					res.put(eid_, cid);
				}
				System.out.println(cluster);
			}
		}
		
//		Set<Long> skip = new HashSet<> ();
//		for (Long eid : elementToClusterMap.keySet()) {
//			Set<Long> cidCollection = elementToClusterMap.get(eid);
//			System.out.println(eid + " " + cidCollection);
//			if (cidCollection.size() > 1) {
//				
////				Long survivedCluster = cidCollection.iterator().next();
////				Set<Long> elements = clusterMap.get(survivedCluster);
//////				Set<Long> deleted = new HashSet<> ();
////				for (Long cid : cidCollection) {
////					if (cid != survivedCluster) {
////						Set<Long> elements_ = clusterMap.get(cid);
////						
////						elements.addAll(elements_);
////						//we are not mutating elementToClusterMap since the new map is the returned Eid X Cid map
//////						deleted.add(cid);
////					}
////				}
////				for (Long eid_ : elements) res.put(eid_, survivedCluster);
//			} else {
//				res.put(eid, cidCollection.iterator().next());
//			}
//		}
		
		return res;
	}
}
