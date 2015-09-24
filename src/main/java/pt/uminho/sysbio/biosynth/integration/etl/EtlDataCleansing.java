package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

public interface EtlDataCleansing<SRC> {
	public Map<String, Triple<String, String, EtlCleasingType>> etlCleanse(SRC object);
}
