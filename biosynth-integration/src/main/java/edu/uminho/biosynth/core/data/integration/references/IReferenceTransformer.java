package edu.uminho.biosynth.core.data.integration.references;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;

public interface IReferenceTransformer<T extends GenericCrossreference> {
	public Class<T> getTransformerEntityClass();
	public GenericCrossreference transform(T crossReference);
}
