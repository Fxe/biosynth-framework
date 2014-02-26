package edu.uminho.biosynth.core.components.representation.basic.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.util.math.components.OrderedPair;

public class MultiLayerGraph<V, E, L> {
	
//	private static Logger
	
	//A Layer contains a set of vertexes and edges
	Map<L, Set<V>> layeredVertex = new HashMap<> ();
	Map<L, Map<V, IBinaryEdge<E, V>>> layeredAdjMap = new HashMap<> ();
	
	Map<OrderedPair<V, L>, IBinaryEdge<E, OrderedPair<V, L>>> interLayerConnect = new HashMap<> (); 
	
	public void addVertex(V vertex, L layer) {
		if ( !layeredVertex.containsKey(layer)) layeredVertex.put(layer, new HashSet<V> ());
		
		if ( !layeredVertex.get(layer).add(vertex)) {
			System.err.println("already exists");
		}
	}
	
	public boolean contains(V vertex, L layer) {
		return this.layeredVertex.containsKey(layer) && this.layeredVertex.get(layer).contains(vertex);
	}
	
	public void addEdge(E edge, V src, L scrL, V dst, L dstL) {
		if (scrL.equals(dstL)) {
			L layer = scrL;
			if ( this.contains(src, layer) && this.contains(dst, layer)) {
				IBinaryEdge<E, V> e = new DefaultBinaryEdge<>(edge, src, dst);
				this.layeredAdjMap.get(layer).put(src, e);
			} else {
				System.err.println("not found");
			}
		} else {
			if ( this.contains(src, scrL) && this.contains(dst, dstL)) {
				System.out.println("Connection Layer " + scrL + " -> " + dstL + " with " + src + "," + dst);
				OrderedPair<V, L> srcV = new OrderedPair<>(src, scrL);
				OrderedPair<V, L> dstV = new OrderedPair<>(dst, dstL);
				IBinaryEdge<E, OrderedPair<V, L>> interConnect = new DefaultBinaryEdge<> (
						edge, srcV, dstV);
				interLayerConnect.put(srcV, interConnect);
			} else {
				System.err.println("not found");
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(layeredVertex);
		return sb.toString();
	}
}
