package pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph;

/**
 * 
 * @author Filipe Liu
 * 
 */
public interface Graph {
	
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
	
	/**
	 * inverts the direction of all edges of this graph
	 */
	public void reverseGraph();
}
