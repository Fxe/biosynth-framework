package edu.uminho.biosynth.core.data.integration.references;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;

public class TransformBiocycMetaboliteCrossReference extends AbstractTransformCrossReference<BioCycMetaboliteCrossreferenceEntity>{
//	static {
//		refTransformMap.put("LIGAND-CPD", "BIOCYC");
//		refTransformMap.put("BIGG", "BIGG");
//		refTransformMap.put("CHEBI", "CHEBI");
//		refTransformMap.put("", "");
//	}
	@Override
	public Class<BioCycMetaboliteCrossreferenceEntity> getTransformerEntityClass() {
		return BioCycMetaboliteCrossreferenceEntity.class;
	}

	@Override
	public GenericCrossReference transform(
			BioCycMetaboliteCrossreferenceEntity crossReference) {
		
		String originalRef = crossReference.getRef().toUpperCase();
		String originalValue = crossReference.getValue().toUpperCase();
		
		if ( refTransformMap.containsKey(originalRef)) {
			originalRef = refTransformMap.get(originalRef);
		}
		if ( valueTransformMap.containsKey(originalRef) 
				&& valueTransformMap.get(originalRef).containsKey(originalValue)) {
			originalValue = valueTransformMap.get(originalRef).get(originalValue);
		}
		
		GenericCrossReference xref = new GenericCrossReference(crossReference.getType(), originalRef, originalValue);
		
		return xref;
	}

}
