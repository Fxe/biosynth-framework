package pt.uminho.sysbio.biosynthframework.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

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
	
	public static<E> void replicateVertex(DiHyperGraph<String, E> hyperGraph, String vertex) {
		int index = 0;
		for (DiHyperEdge<String, E> edge : hyperGraph.getArcs()) {
			if (edge.inLinks().contains(vertex)) {
				String vertexReplica = vertex + "_" + index++;
				edge.inLinks().remove(vertex);
				edge.inLinks().add(vertexReplica);
				hyperGraph.addVertice(vertexReplica);
			}
			if (edge.outLinks().contains(vertex)) {
				String vertexReplica = vertex + "_" + index++;
				edge.outLinks().remove(vertex);
				edge.outLinks().add(vertexReplica);				
				hyperGraph.addVertice(vertexReplica);
			}
		}
	}
	
	public static<V, E> BinaryGraph<V, E> toBipartiteGraph(DiHyperGraph<V, E> hyperGraph, BinaryGraph<V, E> graph) {
		graph.clear();
		
		return graph;
	}
}
