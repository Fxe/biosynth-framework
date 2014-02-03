package edu.uminho.biosynth.core.data.integration.references;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossReferenceEntity;

public class TransformBiocycMetaboliteCrossReference extends AbstractTransformCrossReference<BioCycMetaboliteCrossReferenceEntity>{
//	static {
//		refTransformMap.put("LIGAND-CPD", "BIOCYC");
//		refTransformMap.put("BIGG", "BIGG");
//		refTransformMap.put("CHEBI", "CHEBI");
//		refTransformMap.put("", "");
//	}
	@Override
	public Class<BioCycMetaboliteCrossReferenceEntity> getTransformerEntityClass() {
		return BioCycMetaboliteCrossReferenceEntity.class;
	}

	@Override
	public GenericCrossReference transform(
			BioCycMetaboliteCrossReferenceEntity crossReference) {
		
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
