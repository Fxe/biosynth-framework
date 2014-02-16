package edu.uminho.biosynth.core.data.integration.etl;

public interface IEtlLoad<DST> {
	public void etlLoad(DST destinationObject);
}
