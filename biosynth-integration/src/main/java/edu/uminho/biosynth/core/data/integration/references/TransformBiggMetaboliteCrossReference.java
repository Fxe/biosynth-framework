package edu.uminho.biosynth.core.data.integration.references;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteCrossreferenceEntity;

public class TransformBiggMetaboliteCrossReference extends AbstractTransformCrossReference<BiggMetaboliteCrossreferenceEntity> {
	
	@Override
	public Class<BiggMetaboliteCrossreferenceEntity> getTransformerEntityClass() {
		return BiggMetaboliteCrossreferenceEntity.class;
	}

	@Override
	public GenericCrossreference transform(BiggMetaboliteCrossreferenceEntity crossReference) {
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
