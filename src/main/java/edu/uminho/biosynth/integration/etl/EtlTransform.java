package edu.uminho.biosynth.integration.etl;

public interface EtlTransform<SRC, DST> {
	public DST etlTransform(SRC entity);
}
