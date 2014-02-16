package edu.uminho.biosynth.core.data.integration.etl.staging.transform;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.etl.IEtlTransform;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public interface IMetaboliteStageTransform<SRC extends GenericMetabolite> 
		extends IEtlTransform<SRC, MetaboliteStga> {
	
	public MetaboliteStga etlTransform(SRC cpd);
}
