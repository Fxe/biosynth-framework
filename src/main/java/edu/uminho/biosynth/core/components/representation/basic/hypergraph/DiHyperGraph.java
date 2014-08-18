package edu.uminho.biosynth.core.components.representation.basic.hypergraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DiHyperGraph<V, E> {
	public static final boolean DEBUG = true;
	public static boolean _LONG_TOSTRING = false;
	
	private String name_;
	private Set<V> vertices_;
	private Map< E,  DiHyperEdge<V, E>> edges_;
	
	public DiHyperGraph() {
		this.name_ = "unamed_directed_hypergraph";
		this.vertices_ = new HashSet<V> ();
		this.edges_ = new HashMap< E, DiHyperEdge<V,E>> ();
	}
	
	public DiHyperGraph( final DiHyperGraph<V, E> hpg) {
		this.name_ = hpg.getName();
		this.edges_ = new HashMap<E, DiHyperEdge<V,E>> ();
		this.vertices_ = new HashSet<V> ();
		
		/*
		for ( DiHyperEdge<V, E> arc : hpg.getArcs()) {
			this.edges_.put(arc.getBody(), arc);
		}*/
		
		for ( E e : hpg.getEdges()) {
			DiHyperEdge<V, E> arc = new DiHyperEdge<V, E> ( hpg.getArc(e));
			this.addEdge( arc);
		}
	}
	
	public DiHyperGraph( final Collection<E> F, final DiHyperGraph<V, E> hpg) {
		this.name_ = hpg.getName();
		this.edges_ = new HashMap<E, DiHyperEdge<V,E>> ();
		this.vertices_ = new HashSet<V> ();
		
		for ( E e : F) {
			DiHyperEdge<V, E> arc = new DiHyperEdge<V, E> ( hpg.getArc(e));
			this.addEdge( arc);
		}
	}
	
	
	
	public Set<V> getVertices() {
		return vertices_;
	}

	public void setVertices(Set<V> vertices) {
		this.vertices_ = vertices;
	}

	public boolean addVertice( V vertice) {
		return this.vertices_.add(vertice);
	}
	
	public boolean addEdge( DiHyperEdge<V, E> edge) {
		this.vertices_.addAll( edge.inLinks());
		this.vertices_.addAll( edge.outLinks());
		
		return this.edges_.put( edge.getBody(), edge) == null;
	}
	
	public void removeEdge( E e) {
		this.edges_.remove(e);
	}
	
	public Set<E> getEdges() {
		return this.edges_.keySet();
	}
	
	public DiHyperEdge<V, E> getArc(E e) {
		return this.edges_.get(e);
	}
	
	
	public Set<V> X(E e) {
		return this.edges_.get(e).outLinks();
	}
	
	public Set<V> Y(E e) {
		DiHyperEdge<V, E> edge = this.edges_.get(e);
		
		if (DEBUG) if (edge == null) System.err.println("DEBUG: EDGE IS NULL !");
		
		return edge.inLinks();
	}
	
	public void setName( String name) {
		this.name_ = name;
	}
	
	public Collection< DiHyperEdge<V, E>> getArcs() {
		return this.edges_.values();
	}
	
	public String getName() {
		return this.name_;
	}
	
	public boolean isEmpty() {
		return this.vertices_.isEmpty();
	}
	
	public int size() {
		return this.vertices_.size();
	}
	
	public int order() {
		return this.edges_.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if ( _LONG_TOSTRING) {
			sb.append("Directed HyperGraph: ").append(this.name_).append('\n');
			sb.append(this.edges_.toString());
		} else {
			sb.append(this.edges_.keySet().toString());
		}
		return sb.toString();
	}
}
