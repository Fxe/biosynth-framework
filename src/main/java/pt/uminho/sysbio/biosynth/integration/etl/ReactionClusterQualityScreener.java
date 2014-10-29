package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.io.dao.ReactionHeterogeneousDao;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.IntegrationLabel;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class ReactionClusterQualityScreener implements EtlQualityScreen<IntegratedCluster> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReactionClusterQualityScreener.class);
	
	private ReactionDao<GenericReaction> reactionDao;
	private int mismatchThreshold = 1;
	private String protonEntry = "BG_901";
	
	public ReactionClusterQualityScreener(ReactionDao<GenericReaction> reactionDao) {
		this.reactionDao = reactionDao;
	}
	
	public Set<ReactionQualityLabel> something(IntegratedCluster integratedCluster) {
//		if (!integratedCluster.getClusterType()
//				.equals(IntegrationLabel.ReactionCluster.toString())) {
//			return null;
//		}
		
		Set<ReactionQualityLabel> qualityLabels = new HashSet<> ();
		
		List<GenericReaction> rxnList = new ArrayList<> ();
		for (IntegratedClusterMember member : integratedCluster.getMembers()) {
			Long reid = member.getMember().getId();
//			Node reactionNode = 
			
			
			GenericReaction rxn = reactionDao.getReactionById(reid);
			
			if (rxn != null) {
				rxnList.add(rxn);
			} else {
				LOGGER.error("not found");
			}
		}
		
		/*
		 * Assume reaction metabolites are normalized
		 */
		
		//if has proton mismatch
		if (hasProtonMismatch(rxnList)) {
			qualityLabels.add(ReactionQualityLabel.PROTON_MISMATCH);
		}
		
//		ReactionQualityLabel qualityLabel = 
		
//		if (hasMetaboliteEqualSet(rxnList)) {
//			
//		} else if (hasMissingElements()) {
//			qualityLabels.add(ReactionQualityLabel.METABOLITE_MISMATCH);
//		} else {
//			qualityLabels.clear();
//			qualityLabels.add(ReactionQualityLabel.ERROR);
//			return qualityLabels;
//		}
		
		if ( !alignReactions(rxnList)) {
			qualityLabels.clear();
			qualityLabels.add(ReactionQualityLabel.ERROR);
			return qualityLabels;
		}
		
		if (!hasEqualOrientation(rxnList)) {
			qualityLabels.add(ReactionQualityLabel.ORIENTATION_MISMATCH);
		}
		
		if (!hasEqualStoichiometry(rxnList)) {
			qualityLabels.add(ReactionQualityLabel.STOICHIOMETRY_MISMATCH);
		}
		
		if (qualityLabels.isEmpty()) qualityLabels.add(ReactionQualityLabel.EXACT_MATCH);
		
		return qualityLabels;
	}
	
	private boolean alignReactions(List<GenericReaction> rxnList) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean hasMissingElements() {
		// TODO Auto-generated method stub
		return false;
	}

	private ReactionQualityLabel hasMetaboliteEqualSet(List<GenericReaction> rxnList) {
		LOGGER.debug("Test Metabolite Set Equality");
		
		Set<String> cpdTotalSet = new HashSet<> ();
		Map<String, Set<String>> metaboliteSetMap = new HashMap<> ();
		for (GenericReaction rxn : rxnList) {
			Set<String> cpdSet = new HashSet<> ();
			cpdSet.addAll(rxn.getProductStoichiometry().keySet());
			cpdSet.addAll(rxn.getReactantStoichiometry().keySet());
			cpdSet.remove(protonEntry);
			cpdTotalSet.addAll(cpdTotalSet);
			metaboliteSetMap.put(rxn.getEntry(), cpdSet);
		}
		
		int missing = 0;
		for (String key : metaboliteSetMap.keySet()) {
			Set<String> cpdSet_ = new HashSet<> (cpdTotalSet);
			cpdSet_.removeAll(metaboliteSetMap.get(key));
			if (cpdSet_.size() > mismatchThreshold) {
				//mismatch limit exceeded, throw error label
				return ReactionQualityLabel.ERROR;
			} else if (!cpdSet_.isEmpty()) {
				missing++;
			}
		}
		
		if (missing != 0) {
			//All OK !
			return ReactionQualityLabel.METABOLITE_MISMATCH;
		}
		
		return ReactionQualityLabel.OK;
	}

	//PROTON_MISMATCH
	public boolean hasProtonMismatch(List<GenericReaction> rxnList) {
		int res = 0;
		
		for (GenericReaction rxn : rxnList) {
			boolean containsProton = rxn.getProductStoichiometry().containsKey(protonEntry) ||
					rxn.getReactantStoichiometry().containsKey(protonEntry);
			if (containsProton) res++;
		}
		
		return res == 0 || res == rxnList.size();
	}
	
	//ORIENTATION_MISMATCH
	public boolean hasEqualOrientation(List<GenericReaction> rxnList) {
		for (GenericReaction rxn : rxnList) {
//			rxn.getOrientation();
		}
		
		return true;
	}
	
	//STOICHIOMETRY_MISMATCH
	public boolean hasEqualStoichiometry(List<GenericReaction> rxnList) {
		return true;
	}
	
	
	//METABOLITE_MISMATCH(n), ERROR
	public void checkMetabolites(List<GenericReaction> rxnList) {
		
	}
	

}
