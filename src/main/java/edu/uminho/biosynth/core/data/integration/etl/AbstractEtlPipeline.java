package edu.uminho.biosynth.core.data.integration.etl;

import java.io.Serializable;

public abstract class AbstractEtlPipeline<SRC, DST> implements IEtlPipeline<SRC, DST> {

	@Override
	public void performEtl(Serializable id,
			IEtlExtract<SRC> extract, IEtlTransform<SRC, DST> transform,
			IEtlLoad<DST> load) {
		SRC source = extract.extract(id);
		DST destination = transform.etlTransform(source);
		load.etlLoad(destination);
	}
}
