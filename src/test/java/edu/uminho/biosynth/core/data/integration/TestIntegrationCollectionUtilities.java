package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationCollectionUtilities;

public class TestIntegrationCollectionUtilities {

	@Test
	public void testExample1() {
		Map<Long, Set<Long>> cidToEidMap = new HashMap<> ();
		Map<Long, Set<Long>> eidToCidMap = new HashMap<> ();
		long[][] cidArr = {{1L}, {1L, 2L, 3L}, 
						   {2L}, {6L, 13L},
						   {3L}, {1L, 4L, 5L},
						   {4L}, {7L, 8L, 9L},
						   {5L}, {10L, 11L, 12L},
						   {6L}, {10L, 7L, 20L},};
		
		for (int i = 0; i < cidArr.length; i+=2) {
			long cid = cidArr[i][0];
			long[] eids = cidArr[i + 1];
			Set<Long> eidSet = new HashSet<Long> ();
			for (long eid : eids) {
				eidSet.add(eid);
			}
			cidToEidMap.put(cid, eidSet);
			for (Long eid : eidSet) {
				if (!eidToCidMap.containsKey(eid)) eidToCidMap.put(eid, new HashSet<Long> ());
				eidToCidMap.get(eid).add(cid);
			}
		}
		
		System.out.println(cidToEidMap);
		System.out.println(eidToCidMap);
		
		Map<Long, Long> res = IntegrationCollectionUtilities.resolveConflicts(cidToEidMap, eidToCidMap);
		
		System.out.println(cidToEidMap);
		System.out.println(eidToCidMap);
		System.out.println(res);
//		assertEquals(res, );
	}

}
