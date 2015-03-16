package pt.uminho.sysbio.biosynth.integration.curation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurationDecisionMap {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CurationDecisionMap.class);
	
	private Map<Long, Long> eidToSetId = new HashMap<> ();
	private Map<Long, Set<Long>> mergedSets = new HashMap<> ();
	public Map<Long, Set<Long>> getMergedSets() { return mergedSets;}
	public void setMergedSets(Map<Long, Set<Long>> mergedSets) { 
		this.mergedSets = mergedSets;
		for (Long id : mergedSets.keySet()) {
			for (Long eid : mergedSets.get(id)) 
				eidToSetId.put(eid, id);
		}
	}
	public void addMergedSet(Long id, Set<Long> set) { 
		mergedSets.put(id, set);
		for (Long eid : set) eidToSetId.put(eid, id);
	}
	
	private Map<Long, Long> distinctSets = new HashMap<> ();
	public Map<Long, Long> getDistinctSets() { return distinctSets; }
	public void setDistinctSets(Map<Long, Long> distinctSets) {
		this.distinctSets = distinctSets;
	}
	
	public Set<Long> getAllEids() {
		Set<Long> result = new HashSet<> ();
		for (Set<Long> eids : mergedSets.values()) {
			result.addAll(eids);
		}
		
		return result;
	}
	
	public void addMemberRejectionPair(long refEid1, long refEid2) {
		Long setId1 = this.eidToSetId.get(refEid1);
		Long setId2 = this.eidToSetId.get(refEid2);
		if (setId1 == null || setId2 == null) {
			LOGGER.debug(String.format("SET NOT PRESENT [%s] - [%s] SKIP ", setId1, setId2));
			return;
		}
		LOGGER.debug(String.format("%s <- REJECT -> %s", this.mergedSets.get(setId1), this.mergedSets.get(setId2)));
		this.distinctSets.put(setId1, setId2);
	}
}
