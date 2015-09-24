package pt.uminho.sysbio.biosynth.integration.etl;

public interface EtlQualityScreen<E> {
	public void evaluate(E entity);
}
