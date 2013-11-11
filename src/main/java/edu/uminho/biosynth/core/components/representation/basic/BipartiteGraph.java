package edu.uminho.biosynth.core.components.representation.basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class BipartiteGraph< U, V, E> {
	private Set<U> vertices1;
	private Set<V> vertices2;
	private Map< U, Set<DefaultEdge<U, V>>> vertices1link;
	private Map< V, Set<DefaultEdge<V, U>>> vertices2link;
	
	public BipartiteGraph() {
		vertices1 = new HashSet<U>();
		vertices2 = new HashSet<V>();
		vertices1link = new HashMap<U, Set<DefaultEdge<U, V>>> ();
		vertices2link = new HashMap<V, Set<DefaultEdge<V, U>>> ();
	}
	
	public void linkUV( U src, V dst) {
		if ( !vertices1.contains(src)) {
			vertices1.add(src);
			//vertices1link.put(src, new HashSet<Edge<U, V>>());
		}
		if ( !vertices2.contains(dst)) {
			vertices2.add(dst);
			//vertices2link.put(dst, new HashSet<Edge<V, U>>());
		}
		
		DefaultEdge<U, V> edge = new DefaultEdge<U, V>(src, dst);
		if ( vertices1link.get(src) == null) {
			vertices1link.put(src, new HashSet<DefaultEdge<U, V>>());
		}
		vertices1link.get(src).add(edge);
	}
	public void linkVU( V src, U dst) {
		if ( !vertices1.contains(src)) {
			vertices1.add(dst);
			//vertices1link.put(dst, new HashSet<Edge<U, V>>());
		}
		if ( !vertices2.contains(dst)) {
			vertices2.add(src);
			//vertices2link.put(src, new HashSet<Edge<V, U>>());
		}
		DefaultEdge<V, U> edge = new DefaultEdge<V, U>(src, dst);
		if ( vertices1link.get(src) == null) {
			vertices2link.put(src, new HashSet<DefaultEdge<V, U>>());
		}
		vertices2link.get(src).add(edge);
	}
	
	public Set<U> getU() {
		Set<U> set1 = new HashSet<U> ( this.vertices1);
		return set1;
	}
	
	public Set<V> getV() {
		Set<V> set2 = new HashSet<V> ( this.vertices2);
		return set2;
	}
	
	public String[] toArrayV() {
		String[] array = new String[ this.vertices1.size() + this.vertices2.size()];
		int pos = 0;
		for ( U key : this.vertices1) {
			array[pos] = key.toString();
			pos++;
		}
		for ( V key : this.vertices2) {
			array[pos] = key.toString();
			pos++;
		}
		return array;
	}
	
	public int order() {
		int order = 0;
		for ( U key : this.vertices1link.keySet()) {
			order += this.vertices1link.get(key).size();
		}
		for ( V key : this.vertices2link.keySet()) {
			order += this.vertices2link.get(key).size();
		}
		return order;
	}
	
	public String[][] toArray() {
		int edges = this.order();
		
		String[][] array = new String[edges][2];
		int pos = 0;
		for ( U key : this.vertices1link.keySet()) {
			for (DefaultEdge<U, V> edge : vertices1link.get(key)) {
				array[pos][0] = edge.getSource().toString();
				array[pos][1] = edge.getDestination().toString();
				pos++;
			}
		}
		for ( V key : this.vertices2link.keySet()) {
			for ( DefaultEdge<V, U> edge : vertices2link.get(key)) {
				array[pos][0] = edge.getSource().toString();
				array[pos][1] = edge.getDestination().toString();
				pos++;
			}
		}
		
		return array;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("U:" + this.vertices1 + "\n");
		sb.append("V:" + this.vertices2 + "\n");
		sb.append("U Link:" + this.vertices1link + "\n");
		sb.append("V Link:" + this.vertices2link + "\n");
		sb.append("ORDER " + this.order());
		return sb.toString();
	}
}
