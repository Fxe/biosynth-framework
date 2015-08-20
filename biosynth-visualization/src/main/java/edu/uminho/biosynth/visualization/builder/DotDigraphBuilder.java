package edu.uminho.biosynth.visualization.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultBinaryEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.IBinaryEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.UniqueEdgeGraphImpl;
import pt.uminho.sysbio.biosynthframework.visualization.graphviz.DotEdge;
import pt.uminho.sysbio.biosynthframework.visualization.graphviz.DotNode;
import pt.uminho.sysbio.biosynthframework.visualization.graphviz.GraphVizShape;

public class DotDigraphBuilder {
	
	private GraphVizShape compoundShape = GraphVizShape.CIRCLE;
	
	private List<GenericReaction> reactionsList = new ArrayList<> ();
	
	private List<Double> reactionsFluxList = new ArrayList<> ();

	private List<IBinaryEdge<DotEdge, DotNode>> graphEdges = new ArrayList<> ();
	
	private Set<String> compoundIgnoreList = new HashSet<> ();
	
	private Map<String, String> nodeText = new HashMap<> ();
	
	public GraphVizShape getCompoundShape() { return compoundShape;}
	public DotDigraphBuilder setCompoundShape(GraphVizShape compoundShape) {
		this.compoundShape = compoundShape;
		return this;
	}
	
	public List<GenericReaction> getReactionsList() { return reactionsList;}
	public DotDigraphBuilder setReactionsList(List<GenericReaction> reactionsList) {
		this.reactionsList = reactionsList;
		return this;
	}

	public List<Double> getReactionsFluxList() { return reactionsFluxList;}
	public void setReactionsFluxList(List<Double> reactionsFluxList) {
		this.reactionsFluxList = reactionsFluxList;
	}

	public Set<String> getCompoundIgnoreList() { return compoundIgnoreList;}
	public DotDigraphBuilder setCompoundIgnoreList(Set<String> compoundIgnoreList) {
		this.compoundIgnoreList = compoundIgnoreList;
		return this;
	}
	
	public Map<String, String> getNodeText() {
		return nodeText;
	}
	public DotDigraphBuilder setNodeText(Map<String, String> nodeText) {
		this.nodeText = nodeText;
		return this;
	}
	
	public DotDigraphBuilder() {

	}
	
	public DotDigraphBuilder(List<GenericReaction> reactionsList, List<Double> reactionsFluxList, Set<String> compoundIgnoreList) {
		this.reactionsList = reactionsList;
		this.reactionsFluxList = reactionsFluxList;
		this.compoundIgnoreList = compoundIgnoreList;
	}
	
	public BinaryGraph<DotNode, DotEdge> build() {
		
		Map<String, DotNode> compoundMap = new HashMap<> ();
		List<DotNode> reactions = new ArrayList<> ();
//		List<DotEdge> edges = new ArrayList<> ();
		
		for (int i = 0; i < reactionsList.size(); i++) {
			GenericReaction genericReaction = reactionsList.get(i);
			Double fluxVal = reactionsFluxList.get(i);
			DotNode reactionDotNode = new DotNode();
			reactionDotNode.setLabel(genericReaction.getEntry());
			reactionDotNode.setShape(GraphVizShape.BOX);
			reactions.add(reactionDotNode);
			
			for (String cpdEntry : genericReaction.getProductStoichiometry().keySet()) {
				if (!compoundIgnoreList.contains(cpdEntry)) {
					DotEdge dotEdge = new DotEdge();
					DotNode dotNode;
					if (!compoundMap.containsKey(cpdEntry)) {
						dotNode = new DotNode();
						dotNode.setShape(compoundShape);
						String label = this.getNodeText().get(cpdEntry);
						dotNode.setLabel(label==null?cpdEntry:label);
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
			}
			for (String cpdEntry : genericReaction.getReactantStoichiometry().keySet()) {
				if (!compoundIgnoreList.contains(cpdEntry)) {
					DotEdge dotEdge = new DotEdge();
					DotNode dotNode;
					if (!compoundMap.containsKey(cpdEntry)) {
						dotNode = new DotNode();
						dotNode.setShape(compoundShape);
						dotNode.setLabel(cpdEntry);
						String label = this.getNodeText().get(cpdEntry);
						dotNode.setLabel(label==null?cpdEntry:label);
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
		
		BinaryGraph<DotNode, DotEdge> graph = new UniqueEdgeGraphImpl<> ();
		for (IBinaryEdge<DotEdge, DotNode> edge : graphEdges) graph.addEdge(edge);
		
		return graph;
	}
}
