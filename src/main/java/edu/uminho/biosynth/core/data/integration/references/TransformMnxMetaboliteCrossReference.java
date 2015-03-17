package edu.uminho.biosynth.core.data.integration.references;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteCrossreferenceEntity;

public class TransformMnxMetaboliteCrossReference extends AbstractTransformCrossReference<MnxMetaboliteCrossreferenceEntity> {
	
	@Override
	public Class<MnxMetaboliteCrossreferenceEntity> getTransformerEntityClass() {
		return MnxMetaboliteCrossreferenceEntity.class;
	}

	@Override
	public GenericCrossreference transform(MnxMetaboliteCrossreferenceEntity crossReference) {
		String originalRef = crossReference.getRef().toUpperCase();
		String originalValue = crossReference.getValue().toUpperCase();
		
		if ( refTransformMap.containsKey(originalRef)) {
			originalRef = refTransformMap.get(originalRef);
		}
		if ( valueTransformMap.containsKey(originalRef) 
				&& valueTransformMap.get(originalRef).containsKey(originalValue)) {
			originalValue = valueTransformMap.get(originalRef).get(originalValue);
		}
		
		GenericCrossreference xref = new GenericCrossreference(crossReference.getType(), originalRef, originalValue);
		
		return xref;
	}
}
