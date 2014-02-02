package edu.uminho.biosynth.core.components.representation.basic.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultGraphImpl<V, E> implements IBinaryGraph<V, E> {
	private final Set<V> vertexes = new HashSet<V> ();
	private final Map<V, Set<IBinaryEdge<E, V>>> vertexEdgesMap = new HashMap< V, Set<IBinaryEdge<E, V>>> ();
	
	private final Map<E, IBinaryEdge<E, V>> edgeMap = new HashMap<E, IBinaryEdge<E, V>>();
	
	public DefaultGraphImpl() { }
	
	public DefaultGraphImpl( DefaultGraphImpl<V, E> graph) {
		this.clearEdges();
		for (V v : graph.getVertices()) {
			Set<IBinaryEdge<E, V>> adjEdges = graph.getEdges(v);
			for ( IBinaryEdge<E, V> edge : adjEdges) {
				this.vertexEdgesMap.get(v).add(edge.clone());
			}
		}
		this.edgeMap.putAll( graph.getEdgeMap());
	}
	
	public void clear() {
		this.vertexes.clear();
		this.vertexEdgesMap.clear();
		this.edgeMap.clear();
	}
	
	public int getDegree(V v) {
		int degree = 0;
		for (E edge : edgeMap.keySet()) {
			IBinaryEdge<E, V> ed = edgeMap.get(edge);
			if ( ed.getLeft().equals(v) || ed.getRight().equals(v)) {
				degree++;
			}
		}
		
		return degree;
	}
	
	public int getOutDegree(V v) {
		
		return this.vertexEdgesMap.get(v).size();
	}
	
	public void setWeight(V src, V dst, double w) {
		Set<IBinaryEdge<E, V>> adjEdges = this.vertexEdgesMap.get(src);
		for (IBinaryEdge<E, V> edge : adjEdges) {
			if ( edge.getRight().equals(dst)) {
				edge.setWeight(w);
				break;
			}
		}
	}
	
	public E getEdge(V in, V out) {
		for ( IBinaryEdge<E, V> edge : this.vertexEdgesMap.get(in)) {
			if ( edge.getRight().equals(out)) return edge.getEdge();
		}
		return null;
	}
	
	public IBinaryEdge<E, V> getEdge(E edge) {
		return this.edgeMap.get(edge);
	}
	
	public void merge( DefaultGraphImpl<V, E> graph) {
		for (V v : graph.getVertices()) {
			if ( this.vertexes.add(v)) {
				Set<IBinaryEdge<E, V>> edgeSet = new HashSet<IBinaryEdge<E, V>> ();
				this.vertexEdgesMap.put(v, edgeSet);
			}
		}
		for (V v : graph.getVertices()) {
			Set<IBinaryEdge<E, V>> edgeSet = graph.getEdges(v);
			for ( IBinaryEdge<E, V> edge : edgeSet) {
				this.vertexEdgesMap.get(v).add(edge.clone());
			}
		}
	}
	
	public Set<V> getVertices() {
		return this.vertexes;
	}
	
	public boolean removeEdge() {
		return false;
	}
	
	public void clearEdges() {
		this.vertexEdgesMap.clear();
		this.edgeMap.clear();
		for ( V v : this.vertexes) {
			Set<IBinaryEdge<E, V>> edgeSet = new HashSet<IBinaryEdge<E, V>> ();
			this.vertexEdgesMap.put(v, edgeSet);
		}
	}
	
	public Map<V, Set<IBinaryEdge<E, V>>> getVertexEdgeMap() {
		return this.vertexEdgesMap;
	}
	
	public Collection<IBinaryEdge<E, V>> getEdges() {
		return this.edgeMap.values();
	}
	
	public Set<V> getAdjacentVertices(V v) {
		Set<IBinaryEdge<E, V>> edges = this.vertexEdgesMap.get(v);
		Set<V> adj = new HashSet<V> ();
		for ( IBinaryEdge<E, V> edge : edges) {
			adj.add( edge.getRight());
		}
		return adj;
	}
	
	public Set<IBinaryEdge<E, V>> getEdges(V vertex) {
		return this.vertexEdgesMap.get(vertex);
	}
	
	public double getWeight(V src, V dst) {
		Set<IBinaryEdge<E, V>> dstList = this.vertexEdgesMap.get(src);
		double w = Double.MIN_VALUE;
		for ( IBinaryEdge<E, V> edge : dstList) {
			if ( edge.getRight().equals(dst)) {
				w = edge.getWeight();
				break;
			}
		}
		return w;
	}
	
	public boolean addVertex(V vertex) {
		boolean added = this.vertexes.add(vertex);
		if (added) {
			this.vertexEdgesMap.put(vertex, new HashSet<IBinaryEdge<E, V>> ());
		}
		return added;
	}
	
	public boolean addEdge(IBinaryEdge<E, V> edge) {
		if ( this.edgeMap.containsKey(edge.getEdge())) return false;
		this.addVertex(edge.getLeft());
		this.addVertex(edge.getRight());
		
		this.edgeMap.put(edge.getEdge(), edge);
		
		if (!this.vertexEdgesMap.containsKey(edge.getLeft())) {
			this.vertexEdgesMap.put(edge.getLeft(), new HashSet<IBinaryEdge<E, V>> ());
		}
		this.vertexEdgesMap.get(edge.getLeft()).add(edge);

		return true;
	}
	

	public int size() {
		return this.edgeMap.size();
	}
	
	public int order() {
		return this.vertexes.size();
	}
	
	public void reverseGraph() {
		Map<V, Set<IBinaryEdge<E, V>>> edges_rev = new HashMap<V, Set<IBinaryEdge<E, V>>> ();
		for ( V v : this.vertexes) {
			Set<IBinaryEdge<E, V>> edgeSet = new HashSet<IBinaryEdge<E, V>> ();
			edges_rev.put(v, edgeSet);
		}
		
		for ( Set<IBinaryEdge<E, V>> edgeSet : this.vertexEdgesMap.values()) {
			for (IBinaryEdge<E, V> edge : edgeSet) {
				IBinaryEdge<E, V> edge_rev = new DefaultBinaryEdge<E, V> (edge.getEdge(), edge.getRight(), edge.getLeft(), edge.getWeight());
				edges_rev.get(edge_rev.getLeft()).add(edge_rev);
			}
		}
		
		this.vertexEdgesMap.clear();
		this.vertexEdgesMap.putAll(edges_rev);
	}
	
	public double getPathCost(List<V> path) {
		double cost = 0;
		for ( int i = 1; i < path.size(); i++) {
			cost += this.getWeight(path.get(i - 1), path.get(i));
		}
		return cost;
	}
	
	public Map<E, IBinaryEdge<E, V>> getEdgeMap() {
		return this.edgeMap;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (V vertex : vertexes) {
			sb.append( vertex.toString()).append(" -> ");
			if (vertexEdgesMap.containsKey(vertex)) {
				for ( IBinaryEdge<E, V> edge : vertexEdgesMap.get(vertex)) {
					sb.append( edge.getRight()).append(',').append( edge.getWeight()).append(" -> ");
				}
			}
			sb.append(" /\n");
		}
		System.out.println(this.edgeMap);
		sb.append("SIZE:" + this.size()).append('\n');
		sb.append("ORDER:" + this.order());
		return sb.toString();
	}

	@Override
	public boolean removeVertex(V vertex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addAll(IBinaryGraph<V, E> graph) {
		this.vertexes.addAll(graph.getVertices());
		for (IBinaryEdge<E, V> edge : graph.getEdges()) {
			this.addEdge(edge);
		}
	}
}
