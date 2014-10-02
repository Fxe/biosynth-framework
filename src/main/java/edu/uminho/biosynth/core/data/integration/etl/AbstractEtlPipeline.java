package edu.uminho.biosynth.core.data.integration.etl;

import pt.uminho.sysbio.biosynth.integration.etl.EtlExtract;
import pt.uminho.sysbio.biosynth.integration.etl.EtlPipeline;

public abstract class AbstractEtlPipeline<SRC, DST> implements EtlPipeline<SRC, DST> {

	EtlExtract<SRC> extract;
	
	@Override
	public void etl() {

	}
}
