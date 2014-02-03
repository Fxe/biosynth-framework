package edu.uminho.biosynth.core.data.integration.components;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;

public class ReferenceGraph {
	//map of vertexes
	private Map<Integer, ReferenceNode> nodeMap = new HashMap<> ();
	
	//the graph
	private IBinaryGraph<Integer, ReferenceLinkType> graph;
	
	public ReferenceGraph() { };
	public ReferenceGraph(IBinaryGraph<Integer, ReferenceLinkType> binaryGraph) {
		this.graph = binaryGraph;
	}
	
	public IBinaryGraph<Integer, ReferenceLinkType> getGraph() { return graph;}
	public void setGraph(IBinaryGraph<Integer, ReferenceLinkType> graph) { this.graph = graph;}
	
	public void merge(IBinaryGraph<Integer, ReferenceLinkType> graph) {
		
	}
}
