package edu.uminho.biosynth.util;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.ChebiMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.seed.SeedMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.components.ReferenceNode;

public class ReferenceNodeVertexTransformer implements IVertexTransformer<ReferenceNode>{

	private static Map<String, String> dbColor = new HashMap<> ();
	
	static {
		dbColor.put(BiggMetaboliteEntity.class.getName(), "blue");
		dbColor.put(BioCycMetaboliteEntity.class.getName(), "orange");
		dbColor.put(KeggCompoundMetaboliteEntity.class.getName(), "green");
		dbColor.put(MnxMetaboliteEntity.class.getName(), "gray");
		dbColor.put(SeedMetaboliteEntity.class.getName(), "red");
		dbColor.put(ChebiMetaboliteEntity.class.getName(), "purple");
	}
	
	@Override
	public DotNode toDotNode(ReferenceNode vertex) {
		DotNode node = new DotNode();
		node.setLabel(vertex.getEntryTypePair().getFirst());
//		System.out.println(vertex.getEntityType().getName());
		if (dbColor.containsKey(vertex.getEntityType().getName())) {
			node.setColor(dbColor.get(vertex.getEntityType().getName()));
		}
		return node;
	}

}
