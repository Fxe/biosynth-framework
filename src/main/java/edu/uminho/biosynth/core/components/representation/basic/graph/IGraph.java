package edu.uminho.biosynth.core.components.representation.basic.graph;


public interface IGraph {
	
	/**
	 * The size of a graph is the number of edges.
	 * 
	 * @return the number of edges.
	 */
	public int size();
	
	/**
	 * The order of a graph is the number of vertexes.
	 * 
	 * @return the number of vertexes.
	 */
	public int order();
	
	/**
	 * Removes all of the elements from this graph (Vertexes and Edges). The graph will be empty after this call returns.
	 */
	public void clear();
	
	public void reverseGraph();
}
