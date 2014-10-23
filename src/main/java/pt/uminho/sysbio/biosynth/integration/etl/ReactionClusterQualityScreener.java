package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		
		if (hasMetaboliteEqualSet(rxnList)) {
			
		} else if (hasMissingElements()) {
			qualityLabels.add(ReactionQualityLabel.METABOLITE_MISMATCH);
		} else {
			qualityLabels.clear();
			qualityLabels.add(ReactionQualityLabel.ERROR);
			return qualityLabels;
		}
		
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

	private boolean hasMetaboliteEqualSet(List<GenericReaction> rxnList) {
		// TODO Auto-generated method stub
		return false;
	}

	//PROTON_MISMATCH
	public boolean hasProtonMismatch(List<GenericReaction> rxnList) {
		return true;
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
