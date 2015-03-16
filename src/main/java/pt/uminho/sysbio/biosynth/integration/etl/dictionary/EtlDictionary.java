package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

public interface EtlDictionary<T, L, R> {
	public T translate(L lookup, R reference);
}
