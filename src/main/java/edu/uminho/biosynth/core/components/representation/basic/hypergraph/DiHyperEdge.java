package edu.uminho.biosynth.core.components.representation.basic.hypergraph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DiHyperEdge<V, E> {
	protected Set<V> head_;
	protected Set<V> tail_;
	protected E edge_;
	private DiHyperEdge<V, E> reverseEdge = null;;
	
	public DiHyperEdge(Set<V> in, Set<V> out, E body) {
		this.head_ = new HashSet<V> (in);
		this.tail_ = new HashSet<V> (out);
		this.edge_ = body;
	}
	
	public DiHyperEdge(V[] in, V[] out, E body) {
		this.head_ = new HashSet<V> ( Arrays.asList(in));
		this.tail_ = new HashSet<V> ( Arrays.asList(out));
		this.edge_ = body;
	}
	
	public DiHyperEdge( DiHyperEdge<V, E> arc) {
		this.head_ = new HashSet<V> ( arc.outLinks());
		this.tail_ = new HashSet<V> ( arc.inLinks());
		this.edge_ = arc.getBody();
	}
	
	public DiHyperEdge<V, E> getReverse() {
		return this.reverseEdge;
	}
	
	public void buildReverseEdge(E edge) {
		DiHyperEdge<V, E> reverse = new DiHyperEdge<V, E>(tail_, head_, edge);
		this.reverseEdge = reverse;
	}
	
	public Set<V> inLinks() {
		return this.tail_;
	}
	
	public Set<V> outLinks() {
		return this.head_;
	}
	
	public boolean isDisjoint() {
		Set<V> aux = new HashSet<V> ( this.head_);
		return !aux.removeAll(tail_);
	}
	
	public E getBody() {
		return this.edge_;
	}
	
	public boolean contains(V v) {
		return (this.head_.contains(v) || this.tail_.contains(v));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( this.edge_).append('*')
		  .append(this.tail_).append("->").append(this.head_);
		return sb.toString();
	}
}
