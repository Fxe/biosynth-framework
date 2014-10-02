package edu.uminho.biosynth.core.data.integration.etl;

import java.io.Serializable;

import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.etl.EtlExtract;
import pt.uminho.sysbio.biosynth.integration.etl.EtlLoad;

public abstract class AbstractEtlPipeline<SRC, DST> implements IEtlPipeline<SRC, DST> {

	@Override
	public void performEtl(Serializable id,
			EtlExtract<SRC> extract, EtlTransform<SRC, DST> transform,
			EtlLoad<DST> load) {
		SRC source = extract.extract(id);
		DST destination = transform.etlTransform(source);
		load.etlLoad(destination);
	}
}
