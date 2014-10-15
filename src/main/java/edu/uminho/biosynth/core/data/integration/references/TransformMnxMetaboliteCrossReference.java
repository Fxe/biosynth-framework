package edu.uminho.biosynth.core.data.integration.references;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;

public class TransformMnxMetaboliteCrossReference extends AbstractTransformCrossReference<MnxMetaboliteCrossreferenceEntity> {
	
	@Override
	public Class<MnxMetaboliteCrossreferenceEntity> getTransformerEntityClass() {
		return MnxMetaboliteCrossreferenceEntity.class;
	}

	@Override
	public GenericCrossReference transform(MnxMetaboliteCrossreferenceEntity crossReference) {
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
