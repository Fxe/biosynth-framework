package edu.uminho.biosynth.core.data.integration.components;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.representation.basic.graph.BinaryGraph;

public class ReferenceGraph {
	//map of vertexes
	private Map<Integer, ReferenceNode> nodeMap = new HashMap<> ();
	
	//the graph
	private BinaryGraph<Integer, ReferenceLinkType> graph;
	
	public ReferenceGraph() { };
	public ReferenceGraph(BinaryGraph<Integer, ReferenceLinkType> binaryGraph) {
		this.graph = binaryGraph;
	}
	
	public BinaryGraph<Integer, ReferenceLinkType> getGraph() { return graph;}
	public void setGraph(BinaryGraph<Integer, ReferenceLinkType> graph) { this.graph = graph;}
	
	public void merge(BinaryGraph<Integer, ReferenceLinkType> graph) {
		
	}
}
