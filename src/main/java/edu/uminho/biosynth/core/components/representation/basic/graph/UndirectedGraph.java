package edu.uminho.biosynth.core.components.representation.basic.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UndirectedGraph<V, E> implements BinaryGraph<V, E> {

	protected final Set<V> vertexes = new HashSet<> ();
	protected final Map<V, Set<IBinaryEdge<E, V>>> vertexEdgesMap = new HashMap<> ();
	protected final Map<E, Set<IBinaryEdge<E, V>>> edgesMap = new HashMap<> ();
	
	@Override
	public int size() {
		return this.edgesMap.size();
	}

	@Override
	public int order() {
		return this.vertexes.size();
	}

	@Override
	public void clear() {
		this.vertexes.clear();
		this.edgesMap.clear();
		this.vertexEdgesMap.clear();
	}

	@Override
	public void reverseGraph() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addVertex(V vertex) {
		
		if ( !this.vertexes.contains(vertex)) {
			this.vertexes.add(vertex);
			this.vertexEdgesMap.put(vertex, new HashSet<IBinaryEdge<E, V>> ());
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean removeVertex(V vertex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<V> getVertices() {
		return new HashSet<> (this.vertexes);
	}

	@Override
	public Set<V> getAdjacentVertices(V vertex) {
		Set<V> adj = new HashSet<> ();
		try {
			for (IBinaryEdge<E, V> e : this.vertexEdgesMap.get(vertex)) {
				if (e.getLeft().equals(vertex)) {
					adj.add(e.getRight());
				} else {
					adj.add(e.getLeft());
				}
			}
		} catch (NullPointerException e) {
			System.out.println(vertex);
			System.out.println(this.vertexes.contains(vertex));
			System.out.println(this.vertexEdgesMap.containsKey(vertex));
			throw e;
		}
		
		return adj;
	}

	@Override
	public boolean addEdge(IBinaryEdge<E, V> edge) {
		V left = edge.getLeft();
		V right = edge.getRight();
		
		this.addVertex(left);
		this.addVertex(right);
		
		this.vertexEdgesMap.get(left).add(edge);
		this.vertexEdgesMap.get(right).add(edge);
		
		return false;
	}

	@Override
	public E getEdge(V src, V dst) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBinaryEdge<E, V> getEdge(E edge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IBinaryEdge<E, V>> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAll(BinaryGraph<V, E> graph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWeight(V src, V dst, double w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getWeight(V src, V dst) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (V v : this.vertexEdgesMap.keySet()) {
			sb.append(v).append(" -> ");
			for (IBinaryEdge<E, V> e : this.vertexEdgesMap.get(v)) {
				if (e.getLeft().equals(v)) {
					sb.append(e.getRight()).append(" -> ");
				} else {
					sb.append(e.getLeft()).append(" -> ");
				}
			}
			sb.append("\\\n");
		}
		return sb.toString();
	}

	@Override
	public boolean addEdge(V src, V dst, E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addEdge(V src, V dst, E e, double w) {
		// TODO Auto-generated method stub
		return false;
	}
}
