package edu.uminho.biosynth.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import edu.uminho.biosynth.core.components.representation.basic.hypergraph.DiHyperEdge;
import edu.uminho.biosynth.core.components.representation.basic.hypergraph.DiHyperGraph;

public class HypergraphFactory {
	
//	private static Logger
	
	public static DiHyperGraph<String, String> buildFrom(File file) throws IOException {
		
		DiHyperGraph<String, String> hypergraph = new DiHyperGraph<String, String>();
		
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		
		Set<String> knownVertices = new HashSet<> ();
		String line;
		//read vertices
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if (line.equals("#")) break;
//			System.out.println(line);
			hypergraph.addVertice(line);
			knownVertices.add(line);
		}
		
		int counter = 0;
		//read edges
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			String edgeName = "edge" + counter++;
			if (line.contains(":")) {
				String[] pair = line.split(":");
				edgeName = pair[0].trim();
				line = pair[1].trim();
			}
			
			String[] pair = line.split("-");
			String[] in = pair[0].trim().split(" ");
			String[] out = pair[1].trim().split(" ");
			
			DiHyperEdge<String, String> edge = new DiHyperEdge<String, String>(in, out, edgeName);
			hypergraph.addEdge(edge);
		}
		
		bufferedReader.close();
		
//		hypergraph.addVertice(vertice)
		
		return hypergraph;
	}
}
