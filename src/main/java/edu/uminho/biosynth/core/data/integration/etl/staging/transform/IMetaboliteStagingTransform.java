package edu.uminho.biosynth.core.data.integration.etl.staging.transform;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;
import edu.uminho.biosynth.integration.etl.EtlTransform;

public interface IMetaboliteStagingTransform<SRC extends GenericMetabolite> 
		extends EtlTransform<SRC, MetaboliteStga> {
	
	public MetaboliteStga etlTransform(SRC cpd);
}
