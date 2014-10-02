package edu.uminho.biosynth.core.data.integration.etl;

import java.io.Serializable;

import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynth.integration.etl.EtlExtract;
import pt.uminho.sysbio.biosynth.integration.etl.EtlLoad;

public interface IEtlPipeline<SRC, DST> {
	public void performEtl(Serializable id, 
			EtlExtract<SRC> extract, EtlTransform<SRC, DST> transform, EtlLoad<DST> load);
}
