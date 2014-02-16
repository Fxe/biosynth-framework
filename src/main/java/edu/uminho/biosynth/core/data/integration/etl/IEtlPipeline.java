package edu.uminho.biosynth.core.data.integration.etl;

import java.io.Serializable;

public interface IEtlPipeline<SRC, DST> {
	public void performEtl(Serializable id, 
			IEtlExtract<SRC> extract, IEtlTransform<SRC, DST> transform, IEtlLoad<DST> load);
}
