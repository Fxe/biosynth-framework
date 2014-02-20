package edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface IClusterEvaluator<T> {
	public double score(Map<Serializable, Set<T>> clusters);
}
