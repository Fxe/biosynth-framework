package pt.uminho.sysbio.biosynthframework.core.components.representation.basic;

import java.util.Map;
import java.util.Set;

public class HyperGraph<V, E> {
	private String name_;
	private Set<V> vertices_;
	private Map< E,  HyperEdge<V, E>> edges_;
	
	public HyperGraph() {
		this.name_ = "unamed_hypergraph";
	}
	
	public void addVertice( V vertice) {
		this.vertices_.add(vertice);
	}
	
	public void setName( String name) {
		this.name_ = name;
	}
	
	public int size() {
		return this.vertices_.size();
	}
	
	public int hyperedges() {
		return this.edges_.size();
	}
	
	public String getName() {
		return this.name_;
	}
}
