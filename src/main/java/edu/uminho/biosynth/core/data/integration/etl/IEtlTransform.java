package edu.uminho.biosynth.core.data.integration.etl;

public interface IEtlTransform<SRC, DST> {
	public DST etlTransform(SRC entity);
}
