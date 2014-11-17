package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterLookupTable<K, V> {
	private Map<K, Set<V>> clusterMap = new HashMap<> ();
	private Map<V, Set<K>> clusterInvMap = new HashMap<> ();
	
	public void putCluster(K key, Set<V> values) {
		this.clusterMap.put(key, values);
		for (V value : values) {
			if (!clusterInvMap.containsKey(value)) {
				clusterInvMap.put(value, new HashSet<K> ());
			}
			clusterInvMap.get(value).add(key);
		}
	}
	
	public Set<V> getCluster(K key) {
		return this.clusterMap.get(key);
	}
	
	public Set<K> findClusterForValue(V value) {
		return clusterInvMap.get(value);
	}
	
	public int size() {
		return this.clusterMap.size();
	}
	
	/**
	 * Collects all values that a v belongs
	 * c1 -> {a, b, c} c2 -> {a, e, f}
	 * f(a) = {a, b, c, e, f}
	 * this should not happen ! integration should merge
	 * @param value
	 * @return
	 */
	public Set<V> findAAAA(V value) {
		Set<V> aaa = new HashSet<> ();
		Set<K> clusters = this.findClusterForValue(value);
		for (K key : clusters) {
			aaa.addAll(this.clusterMap.get(key));
		}
		return aaa;
	}
}
