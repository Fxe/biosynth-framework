package edu.uminho.biosynth.core.data.integration.loader;

import java.util.List;

import edu.uminho.biosynth.core.components.representation.basic.graph.BinaryGraph;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;

public interface IReferenceLoader {
	
	public List<String> getMetabolitesId();
	public BinaryGraph<ReferenceNode, ReferenceLink> getMetaboliteReferences(String entry);
}
