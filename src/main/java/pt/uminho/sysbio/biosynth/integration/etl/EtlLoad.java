package pt.uminho.sysbio.biosynth.integration.etl;

public interface EtlLoad<DST> {
	public void etlLoad(DST destinationObject);
}
