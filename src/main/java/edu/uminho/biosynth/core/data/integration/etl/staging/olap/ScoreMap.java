package edu.uminho.biosynth.core.data.integration.etl.staging.olap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.data.io.dao.IGenericDao;
import edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation.IClusterConsensus;

public class ScoreMap<ID extends Serializable, T> {
	//Map<ClusterId, Map<TargetClusterId, ScoreValue>>
	IGenericDao dao;
	private Map<ID, Map<ID, Double>> scoringMap = new HashMap<> ();
	private Map<Integer, Set<Integer>> clusterMap = new HashMap<> ();
	
	IClusterConsensus<T> clusterEval;
	Class<T> klass;
	
	
	public ScoreMap(Class<T> klass) {
		this.klass = klass;
	}
	
	public void updateScore(ID cluster1, ID cluster2){
		Map<Serializable, Set<T>> clusters = new HashMap<>();
		if (cluster1.equals(cluster2)) {
			clusters.put(cluster1, this.idSetToConcreteSet(null));
		} else {
			clusters.put(cluster1, this.idSetToConcreteSet(null));
			clusters.put(cluster2, this.idSetToConcreteSet(null));
		}
		
		double score = clusterEval.score(clusters);
		
		if (!this.scoringMap.containsKey(cluster1)) {
			this.scoringMap.put(cluster1, new HashMap<ID, Double>());
		}
		
		this.scoringMap.get(cluster1).put(cluster2, score);
	}
	
	private Set<T> idSetToConcreteSet(Set<ID> idSet) {
		Set<T> entitySet = new HashSet<> ();
		for (ID id : idSet) {
			T entity = dao.find(klass, id);
			if (entity != null) entitySet.add(entity);
		}
		
		return entitySet;
	}
}
