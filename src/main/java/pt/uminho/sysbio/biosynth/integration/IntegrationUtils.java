package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.Reaction;

public class IntegrationUtils {
	
	/**
	 * Applies a metabolite unification map to reconcialiate metabolite ids
	 */
	public static void reconciliateReactionMetabolites(Reaction reaction, Map<Long, Long> unificationMap) {
		Map<String, Double> left = reaction.getLeftStoichiometry();
		Map<String, Double> left_ = new HashMap<> ();
		for (String id : left.keySet()) {
			Double stoich = left.get(id);
			Long prev_id = Long.parseLong(id);
			Long unifi_id = unificationMap.get(prev_id);
			unifi_id = unifi_id == null ? prev_id : unifi_id;
			left_.put(unifi_id.toString(), stoich);
		}
		reaction.setLeftStoichiometry(left_);
		
		Map<String, Double> right = reaction.getRightStoichiometry();
		Map<String, Double> right_ = new HashMap<> ();
		for (String id : right.keySet()) {
			Double stoich = right.get(id);
			Long prev_id = Long.parseLong(id);
			Long unifi_id = unificationMap.get(prev_id);
			unifi_id = unifi_id == null ? prev_id : unifi_id;
			right_.put(unifi_id.toString(), stoich);
		}
		reaction.setRightStoichiometry(right_);
	}
}
