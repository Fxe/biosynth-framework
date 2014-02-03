package edu.uminho.biosynth.core.data.integration.loader;

import java.util.List;

import edu.uminho.biosynth.core.components.representation.basic.graph.IBinaryGraph;
import edu.uminho.biosynth.core.data.integration.components.ReferenceLink;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;

public interface IReferenceLoader {
	
	public List<String> getMetabolitesId();
	public IBinaryGraph<ReferenceNode, ReferenceLink> getMetaboliteReferences(String entry);
}
