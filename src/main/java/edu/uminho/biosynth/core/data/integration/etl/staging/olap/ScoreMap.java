package edu.uminho.biosynth.core.data.integration.etl.staging.olap;

import java.util.HashMap;
import java.util.Map;

public class ScoreMap {
	//Map<ClusterId, Map<TargetClusterId, ScoreValue>>
	private Map<Integer, Map<Integer, Float>> scoringMap = new HashMap<> ();
	
	
}
