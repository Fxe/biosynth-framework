package edu.uminho.biosynth.core.data.integration.chimera.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
		
		for (Long eid : elementToClusterMap.keySet()) {
			Set<Long> cidCollection = elementToClusterMap.get(eid);
			if (cidCollection.size() > 1) {
				Long survivedCluster = cidCollection.iterator().next();
				Set<Long> elements = clusterMap.get(survivedCluster);
//				Set<Long> deleted = new HashSet<> ();
				for (Long cid : cidCollection) {
					if (cid != survivedCluster) {
						Set<Long> elements_ = clusterMap.get(cid);
						
						elements.addAll(elements_);
						//we are not mutating elementToClusterMap since the new map is the returned Eid X Cid map
//						deleted.add(cid); 
					}
				}
				for (Long eid_ : elements) res.put(eid_, survivedCluster);
			} else {
				res.put(eid, cidCollection.iterator().next());
			}
		}
		
		return res;
	}
}
