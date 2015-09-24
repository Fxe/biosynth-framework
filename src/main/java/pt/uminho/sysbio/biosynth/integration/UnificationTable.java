package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnificationTable {
	
	//id to unified id
	protected final Map<Long, Long> unificationMap;
	//unified id to id
	protected final Map<Long, Set<Long>> unificationMapReverse;
	
	public UnificationTable(Map<Long, Long> unificationMap) {
		this.unificationMap = unificationMap;
		this.unificationMapReverse = new HashMap<> ();
		for (Long id : unificationMap.keySet()) {
			Long cid = unificationMap.get(id);
			if (!unificationMapReverse.containsKey(cid)) {
				unificationMapReverse.put(cid, new HashSet<Long> ());
			}
			
			unificationMapReverse.get(cid).add(id);
		}
	}
	
	public Set<Long> getIdMappingsTo(long id) {
		Set<Long> mappings = this.unificationMapReverse.get(id);
		if (mappings == null) mappings = new HashSet<> ();
		return mappings;
	}
	
	public long reconciliateId(long id) {
		Long unifId = unificationMap.get(id);
		unifId = unifId == null ? id : unifId;
		
		return unifId;
	}
	
	public Long reconciliateId2(long id) {
		return unificationMap.get(id);
	}
}
