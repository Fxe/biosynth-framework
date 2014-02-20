package edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public class MetaboliteClusterEvaluator implements IClusterEvaluator<MetaboliteStga>{

	//NAME
	//CHARGE
	//FORMULA
	double paramName;
	double paramCharge;
	double paramFormula;
	double paramInchi;
	double paramSmiles;
	
	@Override
	public double score(Map<Serializable, Set<MetaboliteStga>> clusters) {
		// TODO Auto-generated method stub
		return 0;
	}

}
