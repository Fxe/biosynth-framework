package edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation;

import java.io.Serializable;
import java.util.Map;

public interface IClusterEvaluator {
	public double score(Map<Serializable, ?> clusters);
}
