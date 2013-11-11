package edu.uminho.biosynth.core.components.representation.basic.graph;

public class DefaultBinaryEdge<E, V> implements IBinaryEdge<E, V>{
	private final V left;
	private final V right;
	private final E edge;
	private double weight;
	
	public DefaultBinaryEdge(E edge, V left, V right) {
		this.left = left;
		this.right = right;
		this.weight = 1.0d;
		this.edge = edge;
		verifyArgs();
	}
	
	public DefaultBinaryEdge(E edge, V left, V right, double weight) {
		this.left = left;
		this.right = right;
		this.weight = weight;
		this.edge = edge;
		verifyArgs();
	}
	
	public DefaultBinaryEdge( DefaultBinaryEdge<E, V> edge) {
		this.left = edge.getLeft();
		this.right = edge.getRight();
		this.weight = edge.getWeight();
		this.edge = edge.getEdge();
		verifyArgs();
	}
	
	private void verifyArgs() {
		if (this.edge == null) System.err.println("EDGE NULL");
		if (this.left == null) System.err.println("left NULL");
		if (this.right == null) System.err.println("right NULL");
	}
	
	public E getEdge() {
		return this.edge;
	}
	
	public DefaultBinaryEdge<E, V> clone() {
		return new DefaultBinaryEdge<E, V>( this);
	}
	
	@Override
	public double getWeight() {
		return this.weight;
	}
	
	@Override
	public void setWeight( double weight) {
		this.weight = weight;
	}
	
	@Override
	public V getLeft() {
		return this.left;
	}
	
	@Override
	public V getRight() {
		return this.right;
	}
	
	@Override
	public int hashCode() {
		/*
		int hash = 11;
		hash = 23 * hash + (left == null ? 0 : left.hashCode());
		hash = 23 * hash + (right == null ? 0 : right.hashCode());
		hash = 23 * hash + (edge == null ? 0 : edge.hashCode());
		*/
		return edge.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj == null) return false;
		if ( obj == this) return true;
		if ( !obj.getClass().equals(this.getClass())) return false;
		
		@SuppressWarnings("unchecked")
		DefaultBinaryEdge<E, V> aux = (DefaultBinaryEdge<E, V>) obj;
		
		return aux.getEdge().equals(this.edge);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.edge).append(':');
		sb.append( left).append( " - ").append( right);
		return sb.toString();
	}
}
