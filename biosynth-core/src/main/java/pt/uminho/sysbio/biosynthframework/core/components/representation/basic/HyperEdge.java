package pt.uminho.sysbio.biosynthframework.core.components.representation.basic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HyperEdge<V, E> {
	
	private Set<V> vertices_;
	private E edge_;
	
	public HyperEdge( Set<V> vertices, E body) {
		this.vertices_ = new HashSet<V> (vertices);
		this.edge_ = body;
	}
	
	public HyperEdge( V[] vertices, E body) {
		this.vertices_ = new HashSet<V> ( Arrays.asList(vertices));
		this.edge_ = body;
	}
	
	public E getBody() {
		return this.edge_;
	}
	
	@Override
	public String toString() {
		return this.vertices_.toString();
	}
}
