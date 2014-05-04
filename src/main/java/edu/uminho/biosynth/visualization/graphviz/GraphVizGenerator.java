package edu.uminho.biosynth.visualization.graphviz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uminho.biosynth.core.components.UnreversibleReaction;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;

public class GraphVizGenerator {
	
	
	
	public static String[] executeGraphviz(String stdin, String...args) throws IOException {
		StringBuilder stdout = new StringBuilder();
		StringBuilder stderr = new StringBuilder();
		
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		final Process process = processBuilder.start();
		
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(process.getOutputStream());
		outputStreamWriter.write(stdin);
		outputStreamWriter.write("\u001a");
		outputStreamWriter.flush();
		
		InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		String readline;
		while ((readline = bufferedReader.readLine()) != null) {
			stdout.append(readline);
		}
		
		outputStreamWriter.close();
		inputStreamReader.close();
		bufferedReader.close();
		
//		Thread stdinWriter = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(process.getOutputStream());
////				outputStreamWriter.
//			}
//		}) {
//		};
//
//		Thread stdoutReader = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
//				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//				String readline;
//				try {
//					while ((readline = bufferedReader.readLine()) != null) {
//						System.out.println(readline);
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}) {
//		};
//
//		Thread stderrReader = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				InputStreamReader inputStreamReader = new InputStreamReader(process.getErrorStream());
//				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//				String readline;
//				try {
//					while ((readline = bufferedReader.readLine()) != null) {
//						System.out.println(readline);
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}) {
//		};
//		
//		stdoutReader.start();
//		stderrReader.start();
		
		String[] output = new String[2];
		output[0] = stdout.toString();
		output[1] = stderr.toString();
		
		return output;
	}
	
	public static String generateSvg(List<UnreversibleReaction> reactions ) {
		
		return "lol";
	}
	
	public static String generateSvg(String dotStr) throws IOException {
		String[] output = executeGraphviz(dotStr, "dot", "-Tsvg");

		return output[0];
	}
	
	public static<V, E> String graphToDot(IBinaryGraph<V, E> graph, VertexTransformer<V, DotNode> vertexTransformer) {
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
			DotNode node = vertexTransformer.toDotNode(vertex);
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
}
