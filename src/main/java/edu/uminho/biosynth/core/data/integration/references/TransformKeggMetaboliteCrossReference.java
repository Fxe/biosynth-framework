package edu.uminho.biosynth.core.data.integration.references;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggMetaboliteCrossReferenceEntity;

public class TransformKeggMetaboliteCrossReference extends AbstractTransformCrossReference<KeggMetaboliteCrossReferenceEntity>{

	@Override
	public Class<KeggMetaboliteCrossReferenceEntity> getTransformerEntityClass() {
		return KeggMetaboliteCrossReferenceEntity.class;
	}

	@Override
	public GenericCrossReference transform(
			KeggMetaboliteCrossReferenceEntity crossReference) {
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
