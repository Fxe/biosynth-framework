package edu.uminho.biosynth.core.data.integration.references;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.bigg.components.BiggMetaboliteCrossReferenceEntity;

public class TransformBiggMetaboliteCrossReference extends AbstractTransformCrossReference<BiggMetaboliteCrossReferenceEntity> {
	
	@Override
	public Class<BiggMetaboliteCrossReferenceEntity> getTransformerEntityClass() {
		return BiggMetaboliteCrossReferenceEntity.class;
	}

	@Override
	public GenericCrossReference transform(BiggMetaboliteCrossReferenceEntity crossReference) {
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
