package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.Reaction;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultGraphImpl;

public class DotGraphFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DotGraphFactory.class);
	
	private static String DEFAULT_PATHWAY = "DEFAULT";
	private String name = "unammed graph";
	private GraphVizShape compoundShape = GraphVizShape.PLAINTEXT;
	private GraphVizShape reactionShape = GraphVizShape.BOX;
	private String defaultColor = "black";
	private Map<String, Metabolite> metaboliteMap = new HashMap<> ();
	private Map<String, GenericReaction> reactionMap = new HashMap<> ();
	private Map<String, DotNode> metaboliteDotMap = new HashMap<> ();
	private Map<String, DotNode> reactionDotMap = new HashMap<> ();
	private Map<String, List<String>> pathways = new HashMap<> ();
	private Map<String, String> pathwayColor = new HashMap<> ();
	
	public DotGraphFactory withName(String name) {
		this.name = name;
		return this;
	}
	
	public DotGraphFactory withMetabolite(Metabolite metabolite) {
		if (metabolite == null || metabolite.getEntry() == null) {
			LOGGER.warn("Invalid metabolite");
			return this;
		}
		this.metaboliteMap.put(metabolite.getEntry(), metabolite);
		return this;
	}
	
	public DotGraphFactory withReaction(GenericReaction reaction) {
		if (reaction == null || reaction.getEntry() == null) {
			LOGGER.warn("Invalid reaction");
			return this;
		}
		this.reactionMap.put(reaction.getEntry(), reaction);
		if (!this.pathways.containsKey(DEFAULT_PATHWAY)) {
			this.pathways.put(DEFAULT_PATHWAY, new ArrayList<String> ());
		}
		this.pathways.get(DEFAULT_PATHWAY).add(reaction.getEntry());
//		for (String cpdEntry : reaction.) {
//			this.metaboliteMap.put(cpdEntry, value)
//		}
		
		return this;
	}
	
	public DotGraphFactory withReactionInPathway(GenericReaction reaction, String pathway) {
		if (reaction == null || reaction.getEntry() == null) {
			LOGGER.warn("Invalid reaction");
			return this;
		}
		this.reactionMap.put(reaction.getEntry(), reaction);
		if (!this.pathways.containsKey(pathway)) {
			this.pathways.put(pathway, new ArrayList<String> ());
		}
		this.pathways.get(pathway).add(reaction.getEntry());
//		for (String cpdEntry : reaction.) {
//			this.metaboliteMap.put(cpdEntry, value)
//		}
		
		return this;
	}
	
	public DotGraphFactory withPathwayColor(String pathway, String color) {
		this.pathwayColor.put(pathway, color);
		return this;
	}
	
	public BinaryGraph<DotNode, DotEdge> build() {
		
		BinaryGraph<DotNode, DotEdge> dotGraph = new DefaultGraphImpl<>();
		
		//Generate Compounds
		for (String cpdEntry : metaboliteMap.keySet()) {
			Metabolite cpd = metaboliteMap.get(cpdEntry);
			//Use a transformer interface to generate node !
			DotNode dotNode = new DotNode();
			dotNode.setShape(compoundShape);
//			String htmlLabel = String.format("<%s>", cpd.getEntry() + "\n" + cpd.getName());
			dotNode.setLabel(cpd.getEntry() + "\n" + cpd.getName());
//			dotNode.setLabel(cpd.getName()==null?cpd.getEntry():cpd.getName());
			dotGraph.addVertex(dotNode);
			this.metaboliteDotMap.put(cpdEntry, dotNode);
		}
		//Generate Reactions
		
		for (String rxnEntry : reactionMap.keySet()) {
			Reaction rxn = reactionMap.get(rxnEntry);
			DotNode dotNode = new DotNode();
			dotNode.setShape(reactionShape);
//			dotNode.setLabel(rxn.getEntry());
			dotNode.setLabel(rxn.getName() ==null? rxn.getEntry():rxn.getName());
			dotGraph.addVertex(dotNode);
			this.reactionDotMap.put(rxnEntry, dotNode);
		}
		//Generate Edges
		for (String pathway : pathways.keySet()) {
			for (String rxnEntry : pathways.get(pathway)) {
				System.out.println("Drawing " + rxnEntry);
				Reaction rxn = reactionMap.get(rxnEntry);
				for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
					switch (rxn.getOrientation()) {
						case RightToLeft: constructRxnToCpdEdge(cpdEntry, rxn, pathway, dotGraph); break;
						case LeftToRight: constructCpdToRxnEdge(cpdEntry, rxn, pathway, dotGraph); break;
						default: 
							LOGGER.warn("%s: assuming left to right", rxn.getOrientation());
							constructCpdToRxnEdge(cpdEntry, rxn, pathway, dotGraph);
							break;
					}
				}
				for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
					switch (rxn.getOrientation()) {
						case RightToLeft: constructCpdToRxnEdge(cpdEntry, rxn, pathway, dotGraph); break;
						case LeftToRight: constructRxnToCpdEdge(cpdEntry, rxn, pathway, dotGraph); break;
						default: 
							LOGGER.warn("%s: assuming left to right", rxn.getOrientation());
							constructRxnToCpdEdge(cpdEntry, rxn, pathway, dotGraph);
							break;
					}
				}
			}
		}
		
		return dotGraph;
	}
	
	private void constructCpdToRxnEdge(
			String cpdEntry, Reaction rxn, String pwy, 
			BinaryGraph<DotNode, DotEdge> graph) {
		
		DotNode src;
		DotNode dst;
		String rxnEntry = rxn.getEntry();
		DotNode cpdDotNode = this.metaboliteDotMap.get(cpdEntry);
		DotNode rxnDotNode = this.reactionDotMap.get(rxnEntry);
		src = cpdDotNode;
		dst = rxnDotNode;
		DotEdge dotEdge = new DotEdge();
		dotEdge.setColor(defaultColor);
		if (this.pathwayColor.containsKey(pwy)) {
			dotEdge.setColor(this.pathwayColor.get(pwy));
		}
		graph.addEdge(src, dst, dotEdge);
	}
	
	private void constructRxnToCpdEdge(
			String cpdEntry, Reaction rxn, String pwy, 
			BinaryGraph<DotNode, DotEdge> graph) {
		
		DotNode src;
		DotNode dst;
		String rxnEntry = rxn.getEntry();
		DotNode cpdDotNode = this.metaboliteDotMap.get(cpdEntry);
		DotNode rxnDotNode = this.reactionDotMap.get(rxnEntry);
		src = rxnDotNode;
		dst = cpdDotNode;
		DotEdge dotEdge = new DotEdge();
		dotEdge.setColor(defaultColor);
		if (this.pathwayColor.containsKey(pwy)) {
			dotEdge.setColor(this.pathwayColor.get(pwy));
		}
		graph.addEdge(src, dst, dotEdge);
	}
}
