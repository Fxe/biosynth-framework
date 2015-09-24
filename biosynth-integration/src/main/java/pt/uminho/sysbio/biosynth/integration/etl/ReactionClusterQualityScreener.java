package pt.uminho.sysbio.biosynth.integration.etl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uminho.biosynth.core.data.integration.IntegrationMessageLevel;
import pt.uminho.sysbio.biosynth.integration.IntegratedCluster;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMember;
import pt.uminho.sysbio.biosynth.integration.IntegratedClusterMeta;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class ReactionClusterQualityScreener implements EtlQualityScreen<IntegratedCluster> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReactionClusterQualityScreener.class);
	
	private ReactionDao<GenericReaction> reactionDao;
	private int mismatchThreshold = 2;
	private String protonEntry = "BG_901";
	
	public String getProtonEntry() { return protonEntry;}
	public void setProtonEntry(String protonEntry) { this.protonEntry = protonEntry;}

	public ReactionClusterQualityScreener(ReactionDao<GenericReaction> reactionDao) {
		this.reactionDao = reactionDao;
	}
	
	@Override
	public void evaluate(IntegratedCluster integratedCluster) {
		Set<ReactionQualityLabel> labels = this.something(integratedCluster);
		
		List<IntegratedClusterMeta> meta = new ArrayList<> ();
		
		for (ReactionQualityLabel label : labels) {
			IntegratedClusterMeta clusterMeta = new IntegratedClusterMeta();
			IntegrationMessageLevel level;
			switch (label) {
				case ERROR: level = IntegrationMessageLevel.ERROR; break;
				case EXACT_MATCH: level = IntegrationMessageLevel.INFO; break;
				case OK: level = IntegrationMessageLevel.INFO; break;
				case PROTON_MISMATCH: level = IntegrationMessageLevel.INFO; break;
				case STOICHIOMETRY_MISMATCH: level = IntegrationMessageLevel.WARNING; break;
				case METABOLITE_MISMATCH: level = IntegrationMessageLevel.WARNING; break;
				default:
					throw new RuntimeException("Unsupported Assertion Label: " + label);
			}
			
			clusterMeta.setLevel(level);
			clusterMeta.setIntegratedCluster(integratedCluster);
			clusterMeta.setMessage("massage !");
			clusterMeta.setMetaType(label.toString());
			
			meta.add(clusterMeta);
		}
		
		integratedCluster.setMeta(meta);
	}
	
	public Set<ReactionQualityLabel> something(List<GenericReaction> rxnList) {
		Set<ReactionQualityLabel> qualityLabels = new HashSet<> ();
		if (rxnList.isEmpty()) {
			LOGGER.warn("String empty cluster.");	
			return qualityLabels;
		}
		
		if ( !alignReactions(rxnList)) {
			qualityLabels.clear();
			qualityLabels.add(ReactionQualityLabel.ERROR);
			return qualityLabels;
		}
		
		/*
		 * Assume reaction metabolites are normalized
		 */
		
		//if has proton mismatch
		if (hasProtonMismatch(rxnList)) {
			qualityLabels.add(ReactionQualityLabel.PROTON_MISMATCH);
		}
		
		Pair<Integer, Integer> mismatch = this.getMaximumMismatchPair(rxnList);
		int totalMismatch = mismatch.getLeft() + mismatch.getRight();
		if (totalMismatch > mismatchThreshold) {
			qualityLabels.add(ReactionQualityLabel.ERROR);
			return qualityLabels;
		}
		
		if (totalMismatch > 0) qualityLabels.add(ReactionQualityLabel.METABOLITE_MISMATCH);
		
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
		

		
		if (!hasEqualOrientation(rxnList)) {
			qualityLabels.add(ReactionQualityLabel.ORIENTATION_MISMATCH);
		}
		
		if (!hasEqualStoichiometry(rxnList)) {
			qualityLabels.add(ReactionQualityLabel.STOICHIOMETRY_MISMATCH);
		}
		
		if (qualityLabels.isEmpty()) qualityLabels.add(ReactionQualityLabel.EXACT_MATCH);
		
		return qualityLabels;
	}
	
	public Set<ReactionQualityLabel> something(IntegratedCluster integratedCluster) {
//		if (!integratedCluster.getClusterType()
//				.equals(IntegrationLabel.ReactionCluster.toString())) {
//			return null;
//		}
//		System.out.println();
		
		List<GenericReaction> rxnList = new ArrayList<> ();
		for (IntegratedClusterMember member : integratedCluster.getMembers()) {
			Long reid = member.getMember().getReferenceId();
//			Node reactionNode = 	
			GenericReaction rxn = reactionDao.getReactionById(reid);
			
			if (rxn != null) {
				rxnList.add(rxn);
			} else {
				LOGGER.error("Reaction not found: " + reid);
			}
		}
		
		return this.something(rxnList);
	}
	
	private boolean alignReactions(List<GenericReaction> rxnList) {
		LOGGER.debug("Align reactions");
		
		if (rxnList.isEmpty()) return false;
		
		GenericReaction rxnPivot = rxnList.get(0);
		Set<String> leftPivot = new HashSet<> (rxnPivot.getReactantStoichiometry().keySet());
		Set<String> rightPivot = new HashSet<> (rxnPivot.getProductStoichiometry().keySet());
		System.out.println(leftPivot + "\t" + rightPivot);
		for (int i = 1; i < rxnList.size(); i++) {
			GenericReaction rxn = rxnList.get(i);
			Set<String> left_ = new HashSet<> (rxn.getReactantStoichiometry().keySet());
//			Set<String> right_ = new HashSet<> (rxn.getProductStoichiometry().keySet());
			
			Double l_l = jaccard(left_, leftPivot);
			Double l_r = jaccard(left_, rightPivot);
			if (l_r > l_l) this.swapStoichiometry(rxn);
			
			System.out.println(rxn.getReactantStoichiometry().keySet() + "\t" + rxn.getProductStoichiometry().keySet());
		}
		return true;
	}
	
	private Pair<Integer, Integer> getMaximumMismatchPair(List<GenericReaction> rxnList) {
		if (rxnList.isEmpty()) return new ImmutablePair<Integer, Integer>(0, 0);
		
		int leftMax = 0;
		int rightMax = 0;
		
		GenericReaction rxnPivot = rxnList.get(0);
		Set<String> leftPivot = new HashSet<> (rxnPivot.getReactantStoichiometry().keySet());
		Set<String> rightPivot = new HashSet<> (rxnPivot.getProductStoichiometry().keySet());
		leftPivot.remove(protonEntry);
		rightPivot.remove(protonEntry);
		
		for (int i = 1; i < rxnList.size(); i++) {
			GenericReaction rxn = rxnList.get(i);
			Set<String> left_ = new HashSet<> (rxn.getReactantStoichiometry().keySet());
			Set<String> right_ = new HashSet<> (rxn.getProductStoichiometry().keySet());
			left_.remove(protonEntry);
			left_.removeAll(leftPivot);
			right_.remove(protonEntry);
			right_.removeAll(rightPivot);
			int l = left_.size();
			int r = right_.size();
			
			leftMax = l > leftMax ? l : leftMax;
			rightMax = r > rightMax ? r : rightMax;
		}
//		System.out.println(leftMax + " - " + rightMax);
		
		return new ImmutablePair<Integer, Integer>(leftMax, rightMax);
	}
	
	private<E> double jaccard(Collection<E> a, Collection<E> b) {
		if (a.isEmpty() && b.isEmpty()) return 1.0;
		
		Set<E> A_union_B = new HashSet<> (a);
		A_union_B.addAll(b);
		Set<E> A_intersect_B = new HashSet<> (a);
		A_intersect_B.retainAll(b);
		
		return A_intersect_B.size() / (double)A_union_B.size();
	}
	
	private void swapStoichiometry(GenericReaction rxn) {
		LOGGER.debug("Swap eq rxn: " + rxn.getEntry());
		Map<String, Double> left = rxn.getLeftStoichiometry();
		Map<String, Double> right = rxn.getRightStoichiometry();
		rxn.setLeftStoichiometry(right);
		rxn.setRightStoichiometry(left);
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
		
		LOGGER.debug("Testing for proton mismatch.");
		
		for (GenericReaction rxn : rxnList) {
			boolean containsProton = rxn.getProductStoichiometry().containsKey(protonEntry) ||
					rxn.getReactantStoichiometry().containsKey(protonEntry);
			if (containsProton) res++;
		}
		
		return !(res == 0 || res == rxnList.size());
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
