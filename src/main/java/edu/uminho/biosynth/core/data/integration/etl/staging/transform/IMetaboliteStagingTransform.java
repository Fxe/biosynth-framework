package edu.uminho.biosynth.core.data.integration.etl.staging.transform;

import pt.uminho.sysbio.biosynth.integration.etl.EtlTransform;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public interface IMetaboliteStagingTransform<SRC extends GenericMetabolite> 
		extends EtlTransform<SRC, MetaboliteStga> {
	
	public MetaboliteStga etlTransform(SRC cpd);
}
