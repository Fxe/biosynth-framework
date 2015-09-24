package edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation;

import java.io.Serializable;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.MultiLayerGraph;

public class IntegrationSystem<V, E, L> extends MultiLayerGraph<V, E, L> {
//	MultiLayerGraph<Serializable, String, Class<?>> graph = new MultiLayerGraph<>();
//	
//	public IntegrationSystem(MultiLayerGraph<Serializable, String, Class<?>> graph) {
//		this.graph = graph;
//	}
	
	public List<?> split(Serializable vertex, String layer, String comparator) {
		//Get Elements Adjacent to vertex
		//For each element adjacent to vertex build adjacency map to L
		//Apply comparator to separate elements of L
		//Each L is a new C return [C]
		return null;
	}
	
	public String merge(V c1, V c2) {
		//Simple Return new Cluster
		return null;
	}
}
