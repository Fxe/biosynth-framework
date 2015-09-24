package edu.uminho.biosynth.core.data.integration.etl.staging.olap.evalutation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uminho.biosynth.core.data.integration.etl.staging.components.MetaboliteInchiDim;

public class InChiDimAttributeConsensus implements IAttributeConsensus<MetaboliteInchiDim> {

	@Override
	public double score(List<MetaboliteInchiDim> values) {
		double score = 1.0d;
		Set<String> inchiSet = new HashSet<> ();
		for (MetaboliteInchiDim inchiDim : values) {
			inchiSet.add(inchiDim.getInchi());
		}
		return score / inchiSet.size();
	}

	@Override
	public MetaboliteInchiDim consensus(List<MetaboliteInchiDim> values) {
		MetaboliteInchiDim consensus = new MetaboliteInchiDim();
		MetaboliteInchiDim neutral = null;
		MetaboliteInchiDim first = values.iterator().next();
		for (MetaboliteInchiDim inchiDim : values) {
			if (inchiDim.getInchi().equals("neutral")) neutral = inchiDim;
		}
		
		if (neutral != null) {
			consensus.setInchi(neutral.getInchi());
			consensus.setInchiKey(neutral.getInchiKey());
		}
		
		return neutral!=null?neutral:first;
	}

}
