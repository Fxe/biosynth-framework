package edu.uminho.biosynth.core.data.integration.references;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;

public class TransformKeggMetaboliteCrossReference extends AbstractTransformCrossReference<KeggCompoundMetaboliteCrossreferenceEntity>{

	@Override
	public Class<KeggCompoundMetaboliteCrossreferenceEntity> getTransformerEntityClass() {
		return KeggCompoundMetaboliteCrossreferenceEntity.class;
	}

	@Override
	public GenericCrossReference transform(
			KeggCompoundMetaboliteCrossreferenceEntity crossReference) {
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
