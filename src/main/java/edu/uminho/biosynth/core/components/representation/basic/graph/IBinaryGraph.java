package edu.uminho.biosynth.core.components.representation.basic.graph;

import java.util.Collection;
import java.util.Set;

import edu.uminho.biosynth.core.components.representation.basic.IGraph;

public interface IBinaryGraph<V, E> extends IGraph {
	
	public boolean addVertex(V vertex);
	public boolean removeVertex(V vertex);
	public Set<V> getVertices();
	public Set<V> getAdjacentVertices(V vertex);
	
	public boolean addEdge(IBinaryEdge<E, V> edge);
	public E getEdge(V src, V dst);
	public IBinaryEdge<E, V> getEdge(E edge);
	public Collection<IBinaryEdge<E, V>> getEdges();
	
	public void setWeight(V src, V dst, double w);
	public double getWeight(V src, V dst);
}
