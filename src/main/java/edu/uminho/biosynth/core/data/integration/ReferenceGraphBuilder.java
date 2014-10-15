package edu.uminho.biosynth.core.data.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.UniqueEdgeGraphImpl;
import pt.uminho.sysbio.biosynthframework.util.math.components.OrderedPair;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;
import edu.uminho.biosynth.core.data.integration.loader.IReferenceLoader;

public class ReferenceGraphBuilder {
	
	private List<IReferenceLoader> loadersList = new ArrayList<> ();
	public List<IReferenceLoader> getLoadersList() { return loadersList;}
	public void setLoadersList(List<IReferenceLoader> loadersList) { this.loadersList = loadersList;}
	
	public BinaryGraph<ReferenceNode, ReferenceLink> omg() {
		
		BinaryGraph<ReferenceNode, ReferenceLink> referenceGraph = 
				new UniqueEdgeGraphImpl<>();
				
		for (IReferenceLoader loader : loadersList) {
			for (String cpdId : loader.getMetabolitesId()) {
//				System.out.println(cpdId);
				BinaryGraph<ReferenceNode, ReferenceLink> xrefsGraph = loader.getMetaboliteReferences(cpdId);
				referenceGraph.addAll(xrefsGraph);
			}
		}
		
		return referenceGraph;
	}
	
	public BinaryGraph<ReferenceNode, ReferenceLink> extractReferenceGraph(String[] entries) {
		// save the services that returned the node so in future we can re-fetch the entities
		Map<ReferenceNode, Set<String>> servicesRelatedToNodeMap = new HashMap<> ();
		
		BinaryGraph<ReferenceNode, ReferenceLink> referenceGraph = new UniqueEdgeGraphImpl<>();
		Set<String> setOfEntries = new HashSet<> ();
		for (int i = 0; i < entries.length; i++) {
			setOfEntries.add(entries[i]);
		}
		for (IReferenceLoader loader : loadersList) {
			for (String cpdId : loader.getMetabolitesId()) {
				if (setOfEntries.contains(cpdId)) {
					System.out.println("===========================================");
					System.out.println(cpdId);
					BinaryGraph<ReferenceNode, ReferenceLink> xrefsGraph = loader.getMetaboliteReferences(cpdId);
					referenceGraph.addAll(xrefsGraph);
					
					for (ReferenceNode vertex : xrefsGraph.getVertices()) {
						if (!servicesRelatedToNodeMap.containsKey(vertex)) {
							servicesRelatedToNodeMap.put(vertex, new HashSet<String> ());
						}
						servicesRelatedToNodeMap.get(vertex).addAll(vertex.getRelatedServiceIds());
					}
					System.out.println(servicesRelatedToNodeMap);
					System.out.println("===========================================");
				}
			}
		}
		for (ReferenceNode vertex : referenceGraph.getVertices()) {
			Set<String> servicesRelatedToNode = servicesRelatedToNodeMap.get(vertex);
			vertex.getIdServiceMap().clear();
			for (String serviceId : servicesRelatedToNode) {
				vertex.getIdServiceMap().add(new OrderedPair<Integer, String>(0, serviceId));
			}
		}
		System.out.println(referenceGraph);
		return referenceGraph;
	}
}
