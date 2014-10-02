package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

public interface EtlDictionary<T, L> {
	public T translate(L lookup);
}
