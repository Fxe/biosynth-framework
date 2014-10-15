package edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.util.math.components.OrderedPair;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteSmilesDim;
import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteStga;

public class MetaboliteClusterEvaluator implements IClusterConsensus<MetaboliteStga>{

	//NAME
	//CHARGE
	//FORMULA
	double paramName;
	double paramCharge;
	double paramFormula;
	double paramInchi;
	double paramSmiles;
	
	private IAttributeConsensus<MetaboliteInchiDim> inchiConsensus;
	private IAttributeConsensus<MetaboliteSmilesDim> smilesConsensus;
	
	@Override
	public double score(Map<Serializable, Set<MetaboliteStga>> clusters) {
		Map<Serializable, OrderedPair<MetaboliteInchiDim, Double>> cluterInchiConsensus = new HashMap<> ();
		Map<Serializable, OrderedPair<MetaboliteSmilesDim, Double>> clusterSmilesConsensus = new HashMap<> ();
		
		for (Serializable key : clusters.keySet()) {
			List<MetaboliteInchiDim> inchiAttributeSet = new ArrayList<> ();
			List<MetaboliteSmilesDim> smilesAttributeSet = new ArrayList<> ();
			
			for (MetaboliteStga cpd : clusters.get(key)) {
				inchiAttributeSet.add(cpd.getMetaboliteInchiDim());
				smilesAttributeSet.add(cpd.getMetaboliteSmilesDim());
			}
			OrderedPair<MetaboliteInchiDim, Double> consensusInchiPair = 
					new OrderedPair<>(inchiConsensus.consensus(inchiAttributeSet), inchiConsensus.score(inchiAttributeSet));
			OrderedPair<MetaboliteSmilesDim, Double> consensusSmilesPair = 
					new OrderedPair<>(smilesConsensus.consensus(smilesAttributeSet), smilesConsensus.score(smilesAttributeSet));
			cluterInchiConsensus.put(key, consensusInchiPair);
			clusterSmilesConsensus.put(key, consensusSmilesPair);
		}
		
		if (clusters.size() > 1) {
			return 1;
		} else {
			//Return single consensus score
			Serializable key = clusters.keySet().iterator().next();
			return paramInchi * cluterInchiConsensus.get(key).getSecond();
		}
	}

}
