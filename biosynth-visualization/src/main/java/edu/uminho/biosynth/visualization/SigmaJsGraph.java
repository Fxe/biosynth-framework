package edu.uminho.biosynth.visualization;

import java.util.ArrayList;
import java.util.List;

public class SigmaJsGraph {
	private List<SigmaJsNode> nodes = new ArrayList<> ();
	private List<SigmaJsEdge> edges = new ArrayList<> ();
	public List<SigmaJsNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<SigmaJsNode> nodes) {
		this.nodes = nodes;
	}
	public List<SigmaJsEdge> getEdges() {
		return edges;
	}
	public void setEdges(List<SigmaJsEdge> edges) {
		this.edges = edges;
	}
	
	
}
