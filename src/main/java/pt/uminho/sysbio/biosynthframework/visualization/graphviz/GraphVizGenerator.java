package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.IBinaryEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;


public class GraphVizGenerator {
	
	private static Logger LOGGER = LoggerFactory.getLogger(GraphVizGenerator.class);
	
	public static String GRAPH_VIZ_BIN_PATH = ""; 
	
	public static String[] executeGraphviz(String stdin, String...args) throws IOException {
		
		LOGGER.debug(StringUtils.join(args, ' '));
		LOGGER.trace(stdin);
		
		StringBuilder stdout = new StringBuilder();
		StringBuilder stderr = new StringBuilder();
		
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		final Process process = processBuilder.start();
		
//		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(process.getOutputStream());
//		outputStreamWriter.write(stdin);
//		outputStreamWriter.write("\u001a");
//		outputStreamWriter.flush();
		
		
		
//		InputStreamReader stdinStreamReader = new InputStreamReader(process.getInputStream());
//		InputStreamReader stderrStreamReader = new InputStreamReader(process.getErrorStream());
		
		IOUtils.write(stdin, process.getOutputStream());
		process.getOutputStream().close();
		
//		IOUtils.w
		stdout.append(IOUtils.toString(process.getInputStream()));
		stderr.append(IOUtils.toString(process.getErrorStream()));
//		
//		BufferedReader bufferedReader;
//		bufferedReader = new BufferedReader(stdinStreamReader);
//		
//		String readline;
//		while ((readline = bufferedReader.readLine()) != null) {
//			stdout.append(readline);
//		}
//		
//		outputStreamWriter.close();
//		stdinStreamReader.close();
//		bufferedReader.close();
//		
//		bufferedReader = new BufferedReader(stderrStreamReader);
//		while ((readline = bufferedReader.readLine()) != null) {
//			stderr.append(readline);
//		}
//		stderrStreamReader.close();
//		bufferedReader.close();
		
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
		
		LOGGER.trace(output[0]);
		LOGGER.trace(output[1]);
		
		return output;
	}
	
	public static String generateSvg(String dotStr) throws IOException {
		String[] output = executeGraphviz(dotStr, GRAPH_VIZ_BIN_PATH + "dot", "-Tsvg");

		return output[0];
	}
	
	public static<V, E> String hyperGraphToDot(DiHyperGraph<V, E> hyperGraph) {
		StringBuilder dotStr = new StringBuilder("digraph {");
		
		Map<Integer, V> vertexMap = new HashMap<> ();
		Map<V, Integer> vertexMapInv = new HashMap<> ();
		Map<Integer, E> vertexEdgeMap = new HashMap<> ();
		Map<E, Integer> vertexEdgeMapInv = new HashMap<> ();
		
		int initialIndex = 1;
		for (V vertex : hyperGraph.getVertices()) {
			vertexMapInv.put(vertex, initialIndex);
			vertexMap.put(initialIndex++, vertex);
		}
		for (E edge : hyperGraph.getEdges()) {
			vertexEdgeMapInv.put(edge, initialIndex);
			vertexEdgeMap.put(initialIndex++, edge);
		}
		
		for (Integer vertexIndex : vertexMap.keySet()) {
			V vertex = vertexMap.get(vertexIndex);
//			DotNode node = vertexTransformer.toDotNode(vertex);
			DotNode node = new DotNode();
			node.setLabel(vertex.toString());
			dotStr.append(String.format("%d [shape=%s, fontsize=%d, color=%s, label=\"%s\"];\n", 
					vertexIndex, node.getShape(), node.getFontSize(), node.getColor(), node.getLabel()));
		}
		for (Integer vertexIndex : vertexEdgeMap.keySet()) {
			E edge = vertexEdgeMap.get(vertexIndex);
//			DotNode node = vertexTransformer.toDotNode(vertex);
			DotNode node = new DotNode();
			node.setShape(GraphVizShape.BOX);
			node.setLabel(edge.toString());
			dotStr.append(String.format("%d [shape=%s, fontsize=%d, color=%s, label=\"%s\"];\n", 
					vertexIndex, node.getShape(), node.getFontSize(), node.getColor(), node.getLabel()));
		}
		
		StringBuilder links = new StringBuilder();
		for (DiHyperEdge<V, E> edge : hyperGraph.getArcs()) {
			Set<V> left = edge.outLinks();
			Set<V> right = edge.inLinks();
			for (V l : left) {
				E r = edge.getBody();
				Integer leftId = vertexMapInv.get(l);
				Integer rightId = vertexEdgeMapInv.get(r);
			
				links.append(String.format(
						"%s->%s [color=%s, label=\"%s\", style=\"setlinewidth(%s), %s\"];\n", 
						leftId, rightId, "black", "", 1, "solid"));
			}
			for (V r : right) {
				E l = edge.getBody();
				Integer leftId = vertexEdgeMapInv.get(l);
				Integer rightId = vertexMapInv.get(r);
				links.append(String.format(
						"%s->%s [color=%s, label=\"%s\", style=\"setlinewidth(%s), %s\"];\n", 
						leftId, rightId, "black", "", 1, "solid"));
			}
		}
		
		dotStr.append(links);
		dotStr.append('}');
		return dotStr.toString();
	}
	
	public static String dotGraphToDot(BinaryGraph<DotNode, DotEdge> graph) {
		Integer initialIndex = 1;
		
		Map<Integer, DotNode> vertexMap = new HashMap<> ();
		Map<DotNode, Integer> vertexMapInv = new HashMap<> ();
		for (DotNode vertex : graph.getVertices()) {
			vertexMapInv.put(vertex, initialIndex);
			vertexMap.put(initialIndex++, vertex);
		}
		StringBuilder ret = new StringBuilder("digraph {");
		for (Integer vertexIndex : vertexMap.keySet()) {
			DotNode vertex = vertexMap.get(vertexIndex);
			DotNode node = vertex;
			ret.append(String.format("%d [shape=%s, fontsize=%d, color=%s, label=\"%s\"];\n", 
					vertexIndex, node.getShape(), node.getFontSize(), node.getColor(), node.getLabel()));
		}
		StringBuilder links = new StringBuilder();
		for (IBinaryEdge<DotEdge, DotNode> edge : graph.getEdges()) {
			DotNode left = edge.getLeft();
			DotNode right = edge.getRight();
			Integer leftId = vertexMapInv.get(left);
			Integer rightId = vertexMapInv.get(right);
			String color = edge.getEdge().getColor();
			links.append(String.format(
					"%s->%s [color=%s, label=\"%s\", style=\"setlinewidth(%s), %s\"];\n", 
					leftId, rightId, color, "", 1, "solid"));
		}
		
		ret.append(links);
		ret.append("}");
		return ret.toString();
	}
	
	public static<V, E> String graphToDot(BinaryGraph<V, E> graph, VertexTransformer<V, DotNode> vertexTransformer) {
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
