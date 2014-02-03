package edu.uminho.biosynth.core.data.integration.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultGraphImpl;
import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;

public class ReferenceGraphBuilder {
	
	private List<IReferenceLoader> loadersList = new ArrayList<> ();
	public List<IReferenceLoader> getLoadersList() { return loadersList;}
	public void setLoadersList(List<IReferenceLoader> loadersList) { this.loadersList = loadersList;}
	
	public IBinaryGraph<ReferenceNode, ReferenceLink> omg() {
		
		IBinaryGraph<ReferenceNode, ReferenceLink> referenceGraph = 
				new DefaultGraphImpl<>();
				
		for (IReferenceLoader loader : loadersList) {
			for (String cpdId : loader.getMetabolitesId()) {
//				System.out.println(cpdId);
				IBinaryGraph<ReferenceNode, ReferenceLink> xrefsGraph = loader.getMetaboliteReferences(cpdId);
				referenceGraph.addAll(xrefsGraph);
			}
		}
		
		return referenceGraph;
	}
	
	public IBinaryGraph<ReferenceNode, ReferenceLink> extractReferenceGraph(String[] entries) {
		IBinaryGraph<ReferenceNode, ReferenceLink> referenceGraph = new DefaultGraphImpl<>();
		Set<String> setOfEntries = new HashSet<> ();
		for (int i = 0; i < entries.length; i++) {
			setOfEntries.add(entries[i].toUpperCase());
		}
		for (IReferenceLoader loader : loadersList) {
			for (String cpdId : loader.getMetabolitesId()) {
				if (setOfEntries.contains(cpdId.toUpperCase())) {
//					System.out.println(cpdId);
					IBinaryGraph<ReferenceNode, ReferenceLink> xrefsGraph = loader.getMetaboliteReferences(cpdId);
					referenceGraph.addAll(xrefsGraph);
				}
			}
		}
		
		return referenceGraph;
	}
}
