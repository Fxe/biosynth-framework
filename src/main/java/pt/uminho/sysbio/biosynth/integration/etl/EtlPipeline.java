package pt.uminho.sysbio.biosynth.integration.etl;

public interface EtlPipeline<SRC, DST> {
	
	public void setExtractSubsystem(EtlExtract<SRC> extractSubsystem);
	public void setTransformSubsystem(EtlTransform<SRC, DST> transformSubsystem);
	public void setLoadSubsystem(EtlLoad<DST> loadSubsystem);
	public void etl();
}
