package edu.uminho.biosynth.core.components.representation.basic.graph;

public interface IBinaryEdge<E, V> {
	
	public E getEdge();
	public double getWeight();
	public void setWeight( double weight);
	public V getLeft();
	public V getRight();
	
	public IBinaryEdge<E, V> clone();
}
