package edu.uminho.biosynth.visualization.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.components.representation.basic.graph.UniqueEdgeGraphImpl;
import edu.uminho.biosynth.visualization.graphviz.DotEdge;
import edu.uminho.biosynth.visualization.graphviz.DotNode;

public class DotDigraphBuilder {
	
	private List<IBinaryEdge<DotEdge, DotNode>> graphEdges = new ArrayList<> ();
	
	public DotDigraphBuilder(List<GenericReaction> genericReactionsList, List<Double> flux) {
		Map<String, DotNode> compoundMap = new HashMap<> ();
		List<DotNode> reactions = new ArrayList<> ();
//		List<DotEdge> edges = new ArrayList<> ();
		
		for (int i = 0; i < genericReactionsList.size(); i++) {
			GenericReaction genericReaction = genericReactionsList.get(i);
			Double fluxVal = flux.get(i);
			DotNode reactionDotNode = new DotNode();
			reactionDotNode.setLabel(genericReaction.getEntry());
			reactionDotNode.setShape("reactangle");
			reactions.add(reactionDotNode);
			
			for (String cpdEntry : genericReaction.getProductStoichiometry().keySet()) {
				
				DotEdge dotEdge = new DotEdge();
				DotNode dotNode;
				if (!compoundMap.containsKey(cpdEntry)) {
					dotNode = new DotNode();
					dotNode.setLabel(cpdEntry);
					compoundMap.put(cpdEntry, dotNode);
				} else {
					dotNode = compoundMap.get(cpdEntry);
				}
				//POSITIVE FLUX reaction outputs products
				if (fluxVal > 0.0) {
					IBinaryEdge<DotEdge, DotNode> edge = new DefaultBinaryEdge<>(dotEdge, reactionDotNode, dotNode);
					graphEdges.add(edge);
				//NEGATIVE FLUX reaction consumes products
				} else if (fluxVal < 0.0) {
					IBinaryEdge<DotEdge, DotNode> edge = new DefaultBinaryEdge<>(dotEdge, dotNode, reactionDotNode);
					graphEdges.add(edge);
				//ZERO NULL REACTION
				} else {
					IBinaryEdge<DotEdge, DotNode> edge = new DefaultBinaryEdge<>(dotEdge, reactionDotNode, dotNode);
					graphEdges.add(edge);
				}
			}
			for (String cpdEntry : genericReaction.getReactantStoichiometry().keySet()) {
				DotEdge dotEdge = new DotEdge();
				DotNode dotNode;
				if (!compoundMap.containsKey(cpdEntry)) {
					dotNode = new DotNode();
					dotNode.setLabel(cpdEntry);
					compoundMap.put(cpdEntry, dotNode);
				} else {
					dotNode = compoundMap.get(cpdEntry);
				}
				//POSITIVE FLUX reaction outputs ...
				if (fluxVal > 0.0) {
					IBinaryEdge<DotEdge, DotNode> edge = new DefaultBinaryEdge<>(dotEdge, dotNode, reactionDotNode);
					graphEdges.add(edge);
				//NEGATIVE FLUX reaction consumes ...
				} else if (fluxVal < 0.0) {
					IBinaryEdge<DotEdge, DotNode> edge = new DefaultBinaryEdge<>(dotEdge, reactionDotNode, dotNode);
					graphEdges.add(edge);
				//ZERO NULL REACTION
				} else {
					IBinaryEdge<DotEdge, DotNode> edge = new DefaultBinaryEdge<>(dotEdge, dotNode, reactionDotNode);
					graphEdges.add(edge);
				}
			}
		}
	}
	
	public IBinaryGraph<DotNode, DotEdge> build() {
		
		IBinaryGraph<DotNode, DotEdge> graph = new UniqueEdgeGraphImpl<> ();
		for (IBinaryEdge<DotEdge, DotNode> edge : graphEdges) graph.addEdge(edge);
		
		return graph;
	}
}
