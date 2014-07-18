package edu.uminho.biosynth.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.components.representation.basic.hypergraph.DiHyperEdge;
import edu.uminho.biosynth.core.components.representation.basic.hypergraph.DiHyperGraph;

public class HypergraphUtils {
	
//	private static<K, V extends Number> void increment(Map<K, V> map, K key, V value) {
//		if (!map.containsKey(key)) {
//			map.put(key, value);
//		} else {
//			V prev = map.get(key);
//			map.put(key, prev + value);
//		}
//	}
	
	public static<V,E> Map<Integer, Integer> getInputDistribution(
			DiHyperGraph<V, E> diHyperGraph, Map<Integer, Integer> map) {
		map.clear();
		for (DiHyperEdge<V, E> edge : diHyperGraph.getArcs()) {
			int count = edge.inLinks().size();
			if (!map.containsKey(count)) {
				map.put(count, 1);
			} else {
				int prev = map.get(count);
				map.put(count, prev + 1);
			}
		}
		return map;
	}
	
	public static<V,E> Map<Integer, Integer> getOutputDistribution(
			DiHyperGraph<V, E> diHyperGraph, Map<Integer, Integer> map) {
		map.clear();
		for (DiHyperEdge<V, E> edge : diHyperGraph.getArcs()) {
			int count = edge.outLinks().size();
			if (!map.containsKey(count)) {
				map.put(count, 1);
			} else {
				int prev = map.get(count);
				map.put(count, prev + 1);
			}
		}
		
		
		
		return map;
	}
	
	public static<V,E> Map<Integer, Set<E>> getInputEdgeDistribution(
			DiHyperGraph<V, E> diHyperGraph, Map<Integer, Set<E>> map) {
		map.clear();
		for (DiHyperEdge<V, E> edge : diHyperGraph.getArcs()) {
			int count = edge.inLinks().size();
			if (!map.containsKey(count)) {
				map.put(count, new HashSet<E> ());
			}
			map.get(count).add(edge.getBody());
		}
		return map;
	}
	
	public static<V,E> Map<Integer, Set<E>> getOutputEdgeDistribution(
			DiHyperGraph<V, E> diHyperGraph, Map<Integer, Set<E>> map) {
		map.clear();
		for (DiHyperEdge<V, E> edge : diHyperGraph.getArcs()) {
			int count = edge.outLinks().size();
			if (!map.containsKey(count)) {
				map.put(count, new HashSet<E> ());
			}
			map.get(count).add(edge.getBody());
		}
		return map;
	}
}
