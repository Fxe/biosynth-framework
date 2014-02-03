package edu.uminho.biosynth.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;

public class GraphDotUtil {
	
	public static<V, E> String graphToDot(IBinaryGraph<V, E> graph, IVertexTransformer<V> vertexTransformer) {
		Integer initialIndex = 1;
		
		Map<Integer, V> vertexMap = new HashMap<> ();
		Map<V, Integer> vertexMapInv = new HashMap<> ();
//		Map<Integer, String> edges = new HashMap<> ();
//		Map<String, Integer> vertexEdgeInv = new HashMap<> ();
		
		for (V vertex : graph.getVertices()) {
			vertexMapInv.put(vertex, initialIndex);
			vertexMap.put(initialIndex++, vertex);
		}
//		for (IBinaryEdge<E, V> edge : graph.getEdges()) {
//			vertexEdge.put(edge, initialIndex);
//			vertexEdge.put(initialIndex++, edge);
//		}
		
		StringBuilder ret = new StringBuilder("digraph {");
		for (Integer vertexIndex : vertexMap.keySet()) {
			V vertex = vertexMap.get(vertexIndex);
			DotNode node = vertexTransformer==null?new DefaultVertexTransformer().toDotNode(vertex):vertexTransformer.toDotNode(vertex);
			ret.append(String.format("%d [shape=%s, fontsize=%d, color=%s, label=\"%s\"];\n", 
					vertexIndex, node.getShape(), node.getFontSize(), node.getColor(), node.getLabel()));
		}
		
		StringBuilder links = new StringBuilder();
		for (IBinaryEdge<E, V> edge : graph.getEdges()) {
			V left = edge.getLeft();
			V right = edge.getRight();
			Integer leftId = vertexMapInv.get(left);
			Integer rightId = vertexMapInv.get(right);
			
			links.append(String.format(
					"%s->%s [color=%s, label=\"%s\", style=\"setlinewidth(%s), %s\"];\n", 
					leftId, rightId, "black", "", 1, "solid"));
		}
		
		ret.append(links);
		ret.append("}");
		return ret.toString();
	}
	
	public static<V, E> String graphToDot(IBinaryGraph<V, E> graph) {
		return graphToDot(graph, null);
	}
	
	public static void generateGraph(String type, File input, File output) throws IOException {
		Runtime.getRuntime().exec(String.format(
				"D:/opt/graphviz-2.34/bin/%s.exe -T%s -oD:/%s.%s D:/%s.dot",
				"dot", type, input.getAbsolutePath(), type, output.getAbsolutePath()));
	}
}
