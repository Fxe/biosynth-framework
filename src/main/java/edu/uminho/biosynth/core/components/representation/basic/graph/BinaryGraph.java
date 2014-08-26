package edu.uminho.biosynth.core.components.representation.basic.graph;

import java.util.Collection;
import java.util.Set;

/**
 * 
 * @author Filipe Liu
 *
 * @param <V>
 * @param <E>
 */
public interface BinaryGraph<V, E> extends Graph {
	
	/**
	 * Add vertex to graph
	 * 
	 * @param v vertex to be added to this graph
	 * @return <code>true</code> if this graph did not already contain the specified vertex
	 */
	public boolean addVertex(V v);
	
	/**
	 * 
	 * 
	 * @param v vertex to be removed from this graph, if present 
	 * @return <code>true</code> if this set contained the specified element
	 */
	public boolean removeVertex(V v);
	
	/**
	 * 
	 * @return a copy of all the vertices from this graph
	 */
	public Set<V> getVertices();
	
	/**
	 * 
	 * @param v vertex whose adjacent elements is to be tested
	 * @return a copy of the adjacent vertices from this graph
	 */
	public Set<V> getAdjacentVertices(V v);
	
	/**
	 * @param src source of the edge to be added to this graph
	 * @param dst destination of the edge to be added to this graph
	 * @param e edge element to be added to this graph
	 * @return <code>true</code> if this graph did not already contain the specified vertex
	 */
	public boolean addEdge(V src, V dst, E e);
	
	/**
	 * 
	 * @param src source of the edge to be added to this graph
	 * @param dst destination of the edge to be added to this graph
	 * @param e edge element to be added to this graph
	 * @param w weight of the edge
	 * @return <code>true</code> if this graph did not already contain the specified vertex
	 */
	public boolean addEdge(V src, V dst, E e, double w);
	
	public boolean addEdge(IBinaryEdge<E, V> edge);
	public E getEdge(V src, V dst);
	public IBinaryEdge<E, V> getEdge(E edge);
	public Collection<IBinaryEdge<E, V>> getEdges();
	
	public void addAll(BinaryGraph<V, E> graph);
	
	public void setWeight(V src, V dst, double w);
	public double getWeight(V src, V dst);
}
