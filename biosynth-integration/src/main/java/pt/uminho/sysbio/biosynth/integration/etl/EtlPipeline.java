package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;

public interface EtlPipeline<SRC, DST> {
	
	public void setEtlDataCleasingSubsystem(EtlDataCleansing<DST> dataCleasingSubsystem);
	public void setExtractSubsystem(EtlExtract<SRC> extractSubsystem);
	public void setTransformSubsystem(EtlTransform<SRC, DST> transformSubsystem);
	public void setLoadSubsystem(EtlLoad<DST> loadSubsystem);
	public void etl(Serializable id);
	public void etl();
}
