package edu.uminho.biosynth.core.components.representation.basic.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.uminho.biosynth.util.math.components.OrderedPair;

public class UniqueEdgeGraphImpl<V, E> extends DefaultGraphImpl<V, E> {
	
	private Map<OrderedPair<V, V>, E> edgePairMap = new HashMap<>();
	
	@Override
	public void clear() {
		super.clear();
		this.edgePairMap.clear();
	};
	
	@Override
	public boolean addEdge(IBinaryEdge<E, V> edge) {
		OrderedPair<V, V> pair = new OrderedPair<>(edge.getLeft(), edge.getRight());
		if (edgePairMap.containsKey(pair)) return false;
		
		edgePairMap.put(pair, edge.getEdge());
		
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
}
