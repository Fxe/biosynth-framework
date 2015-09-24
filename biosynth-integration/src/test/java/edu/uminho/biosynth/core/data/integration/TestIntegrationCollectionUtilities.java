package edu.uminho.biosynth.core.data.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import edu.uminho.biosynth.core.data.integration.chimera.dao.IntegrationCollectionUtilities;

public class TestIntegrationCollectionUtilities {

//	@Test
//	public void testExample1() {
//		Map<Long, Set<Long>> cidToEidMap = new HashMap<> ();
//		Map<Long, Set<Long>> eidToCidMap = new HashMap<> ();
//		long[][] cidArr = {{1L}, {1L, 2L, 3L}, 
//						   {2L}, {6L, 13L},
//						   {3L}, {1L, 4L, 5L},
//						   {4L}, {7L, 8L, 9L},
//						   {5L}, {10L, 11L, 12L},
//						   {6L}, {10L, 7L, 20L},};
//		
//		for (int i = 0; i < cidArr.length; i+=2) {
//			long cid = cidArr[i][0];
//			long[] eids = cidArr[i + 1];
//			Set<Long> eidSet = new HashSet<Long> ();
//			for (long eid : eids) {
//				eidSet.add(eid);
//			}
//			cidToEidMap.put(cid, eidSet);
//			for (Long eid : eidSet) {
//				if (!eidToCidMap.containsKey(eid)) eidToCidMap.put(eid, new HashSet<Long> ());
//				eidToCidMap.get(eid).add(cid);
//			}
//		}
//		
//		Set<Long> survivded = new HashSet<> ();
//		Set<Long> deleted = new HashSet<> ();
//		Map<Long, Long> res = IntegrationCollectionUtilities.resolveConflicts(
//				cidToEidMap, survivded, deleted);
//
//		long v1 = res.get(1L);
//		long v2 = res.get(2L);
//		long v3 = res.get(3L);
//		long v4 = res.get(4L);
//		long v5 = res.get(5L);
//		long v6 = res.get(6L);
//		long v13 = res.get(13L);
//		
//		assertEquals(true, v1 == v2 && v2 == v3 && v3 == v4 && v4 == v5);
//		assertEquals(true, v6 == v13);
//		assertEquals(3, survivded.size());
//		assertEquals(3, deleted.size());
//	}
//	
//	@Test
//	public void testExample2() {
//		Map<String, Set<Long>> cidToEidMap = new HashMap<> ();
//		Map<Long, Set<String>> eidToCidMap = new HashMap<> ();
//		String[] cidArr = {"C1", "C2", "C3", "C4", "C5", "C6"};
//		long[][] eidArr = {{1L, 2L, 3L}, 
//						   {6L, 13L},
//						   {1L, 4L, 5L},
//						   {7L, 8L, 9L},
//						   {10L, 11L, 12L},
//						   {10L, 7L, 20L},};
//		
//		for (int i = 0; i < cidArr.length; i++) {
//			String cid = cidArr[i];
//			long[] eids = eidArr[i];
//			Set<Long> eidSet = new HashSet<Long> ();
//			for (long eid : eids) {
//				eidSet.add(eid);
//			}
//			cidToEidMap.put(cid, eidSet);
//			for (Long eid : eidSet) {
//				if (!eidToCidMap.containsKey(eid)) eidToCidMap.put(eid, new HashSet<String> ());
//				eidToCidMap.get(eid).add(cid);
//			}
//		}
//		
//		Set<String> survivded = new HashSet<> ();
//		Set<String> deleted = new HashSet<> ();
//		Map<Long, String> res = IntegrationCollectionUtilities.resolveConflicts(
//				cidToEidMap, survivded, deleted);
//
//		String v1 = res.get(1L);
//		String v2 = res.get(2L);
//		String v3 = res.get(3L);
//		String v4 = res.get(4L);
//		String v5 = res.get(5L);
//		String v6 = res.get(6L);
//		String v13 = res.get(13L);
//		
//		assertEquals(true, v1.equals(v2) && v2.equals(v3) && v3.equals(v4) && v4.equals(v5));
//		assertEquals(true, v6.equals(v13));
//		assertEquals(3, survivded.size());
//		assertEquals(3, deleted.size());
//	}
//	
//	@Test
//	public void testExample3() {
//		Map<String, Set<Long>> cidToEidMap = new HashMap<> ();
//		Map<Long, Set<String>> eidToCidMap = new HashMap<> ();
//		String[] cidArr = {"C1", "C2", "C3", "C4", "C5", "C6"};
//		long[][] eidArr = {{1L, 2L, 3L}, 
//						   {6L, 13L},
//						   {1L, 4L, 5L},
//						   {7L, 8L, 9L},
//						   {10L, 11L, 12L},
//						   {10L, 7L, 20L},};
//		
//		for (int i = 0; i < cidArr.length; i++) {
//			String cid = cidArr[i];
//			long[] eids = eidArr[i];
//			Set<Long> eidSet = new HashSet<Long> ();
//			for (long eid : eids) {
//				eidSet.add(eid);
//			}
//			cidToEidMap.put(cid, eidSet);
//			for (Long eid : eidSet) {
//				if (!eidToCidMap.containsKey(eid)) eidToCidMap.put(eid, new HashSet<String> ());
//				eidToCidMap.get(eid).add(cid);
//			}
//		}
//		
//		Map<String, Set<Long>> res = IntegrationCollectionUtilities.resolveConflicts2(cidToEidMap);
//
//		assertEquals(5, res.get("C3").size());
//		assertEquals(7, res.get("C6").size());
//		assertEquals(2, res.get("C2").size());
//		assertEquals(3, res.keySet().size());
//		assertEquals(3, cidToEidMap.keySet().size() - res.keySet().size());
//	}
//
//	@Test
//	public void testExample4() {
//		Map<String, Set<Long>> cidToEidMap = new HashMap<> ();
//		Map<Long, Set<String>> eidToCidMap = new HashMap<> ();
//		String[] cidArr = {"C1", "C2", "C3", "C4", "C5", "C6"};
//		long[][] eidArr = {{1L, 2L, 3L}, 
//						   {6L},
//						   {1L, 4L, 5L},
//						   {7L, 8L, 9L},
//						   {10L, 11L, 12L},
//						   {10L, 7L, 20L},};
//		
//		for (int i = 0; i < cidArr.length; i++) {
//			String cid = cidArr[i];
//			long[] eids = eidArr[i];
//			Set<Long> eidSet = new HashSet<Long> ();
//			for (long eid : eids) {
//				eidSet.add(eid);
//			}
//			cidToEidMap.put(cid, eidSet);
//			for (Long eid : eidSet) {
//				if (!eidToCidMap.containsKey(eid)) eidToCidMap.put(eid, new HashSet<String> ());
//				eidToCidMap.get(eid).add(cid);
//			}
//		}
//		
//		Map<String, Set<Long>> res = IntegrationCollectionUtilities.resolveConflicts2(cidToEidMap);
//
//		assertEquals(5, res.get("C3").size());
//		assertEquals(7, res.get("C6").size());
//		assertEquals(1, res.get("C2").size());
//		assertEquals(3, res.keySet().size());
//		assertEquals(3, cidToEidMap.keySet().size() - res.keySet().size());
//	}
}
