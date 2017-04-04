package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.function.Function;

public interface EtlTransform<SRC, DST> extends Function<SRC, DST> {
	public DST etlTransform(SRC srcObject);
}
