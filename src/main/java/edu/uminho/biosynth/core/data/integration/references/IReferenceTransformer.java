package edu.uminho.biosynth.core.data.integration.references;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;

public interface IReferenceTransformer<T extends GenericCrossReference> {
	public Class<T> getTransformerEntityClass();
	public GenericCrossReference transform(T crossReference);
}
