package edu.uminho.biosynth.core.data.integration.references;

import edu.uminho.biosynth.core.components.GenericCrossReference;

public interface IReferenceTransformer<T extends GenericCrossReference> {
	public Class<T> getTransformerEntityClass();
	public GenericCrossReference transform(T crossReference);
}
