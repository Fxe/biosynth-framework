package edu.uminho.biosynth.core.components.representation.basic.hypergraph;

import java.util.Set;

public interface HyperEdge<V, E> {
	public Set<V> getA();
	public Set<V> getB();
}
