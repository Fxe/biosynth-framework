package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.algorithm.graph.BreadthFirstSearch;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.UndirectedGraph;

public class IntegrationCollectionUtilities {

	public static<K, V> Map<V, Set<K>> invertMapKeyToSet(Map<K, Set<V>> map) {
		Map<V, Set<K>> res = new HashMap<> ();
		for (K key : map.keySet()) {
			for (V value : map.get(key)) {
				res.put(value, new HashSet<K> ());
			}
		}
		for (K key : map.keySet()) {
			for (V value : map.get(key)) {
				res.get(value).add(key);
			}
		}
		
		return res;
	}
	
	/**
	 * Resolve conflicts between clusters.
	 * Let C be a cluster defined by a set of elements E. Every cluster that 
	 * shares the same element must be merged together.
	 * 
	 * Example: C<sub>1</sub> = [a, c, f], C<sub>2</sub> = [h, k], C<sub>3</sub> = [a, n, m],
	 * then C<sub>1</sub> must be merged with C<sub>3</sub> since they have the <b>a</b>
	 *  element in common.
	 * 
	 * @param clusterMap is the map that maps cluster id to the set of its elements.
	 * @param elementToClusterMap is the map that maps elements to clusters that they belong.
	 * @return a map that maps each element to a single cluster
	 */
	public static<EID, CID> Map<EID, CID> resolveConflicts(Map<CID, Set<EID>> clusterMap,
			Collection<CID> survived, Collection<CID> deleted) {
		
		Map<EID, Set<CID>> eidToCid = invertMapKeyToSet(clusterMap);
		
		Map<EID, CID> res = new HashMap<> ();
		Set<EID> eids = new HashSet<> ();
		UndirectedGraph<EID, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		for (CID cid : clusterMap.keySet()) {
			EID prev = null;
			for (EID eid : clusterMap.get(cid)) {
				eids.add(eid);
				if (prev != null) {
					DefaultBinaryEdge<Integer, EID> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
					graph.addEdge(edge);
				}
				prev = eid;
			}
		}
		
		Set<CID> cidsSurvived = new HashSet<> ();
		Set<EID> eidsProcessed = new HashSet<> ();
		for (EID eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<EID> cluster = BreadthFirstSearch.run(graph, eid);
				eidsProcessed.addAll(cluster);
				CID cid = eidToCid.get(cluster.iterator().next()).iterator().next();
				cidsSurvived.add(cid);
				for (EID eid_ : cluster) {
					res.put(eid_, cid);
				}
//				System.out.println(cluster);
			}
		}
		
		if (survived != null) {
			survived.clear();
			survived.addAll(cidsSurvived);
		}
		
		if (deleted != null) {
			deleted.clear();
			deleted.addAll(clusterMap.keySet());
			deleted.removeAll(cidsSurvived);
		}
		
		return res;
	}
	
	public static<EID, CID> Map<CID, Set<EID>> resolveConflicts2(Map<CID, Set<EID>> clusterMap) {
		
		Map<EID, Set<CID>> eidToCid = invertMapKeyToSet(clusterMap);
		
		Map<CID, Set<EID>> res = new HashMap<> ();
		Set<EID> eids = new HashSet<> ();
		UndirectedGraph<EID, Integer> graph = new UndirectedGraph<>();
		Integer counter = 0;
		for (CID cid : clusterMap.keySet()) {
			EID prev = null;
			Set<EID> eids_ = clusterMap.get(cid);
			if (eids_.size() < 2) {
				for (EID eid : eids_) {
					graph.addVertex(eid);
					eids.add(eid);
				}
			} else {
				for (EID eid : eids_) {
					eids.add(eid);
					if (prev != null) {
						DefaultBinaryEdge<Integer, EID> edge = new DefaultBinaryEdge<>(counter++, prev, eid);
						graph.addEdge(edge);
					}
					prev = eid;
				}
			}
		}

		Set<EID> eidsProcessed = new HashSet<> ();
		for (EID eid : eids) {
			if (!eidsProcessed.contains(eid)) {
				Set<EID> cluster = BreadthFirstSearch.run(graph, eid);
				eidsProcessed.addAll(cluster);
				CID cid = eidToCid.get(cluster.iterator().next()).iterator().next();
				res.put(cid, cluster);
//				System.out.println(cluster);
			}
		}
		
		return res;
	}
}
