package pt.uminho.sysbio.biosynth.integration.lostandfound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationSetOperation {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(IntegrationSetOperation.class);
	
	public Map<String, Set<Long>> merge(Map<String, Set<Long>> ctr1, Map<String, Set<Long>> ctr2) {
		Map<Long, String> ctr1_ = invert(ctr1);
		Map<Long, String> ctr2_ = invert(ctr2);
		Map<String, Set<Long>> pass = new HashMap<> ();
		Set<Map<String, Set<Long>>> conflict = new HashSet<> ();
		Set<Long> eidDom1 = new HashSet<> ();
		Set<Long> eidDom2 = new HashSet<> ();
		for (Set<Long> eids : ctr1.values()) eidDom1.addAll(eids);
		for (Set<Long> eids : ctr2.values()) eidDom2.addAll(eids);
		Set<Long> eids = new HashSet<> (eidDom1);
		eids.addAll(eidDom2);
		Set<Long> conflictEntities = new HashSet<> ();
		
		LOGGER.debug("resolve conflicting clusters");
		for (long eid : eids) {
			LOGGER.trace("Test: " + eid);
			if (!conflictEntities.contains(eid)) {
				LOGGER.trace("Verify conflicts for: " + eid);
				String ctr1Entry = ctr1_.get(eid);
				String ctr2Entry = ctr2_.get(eid);
				boolean in1 = ctr1Entry != null;
				boolean in2 = ctr2Entry != null;
				if (in1 && in2) {
					LOGGER.trace(String.format("%d is conflictous", eid));
					Set<String> omg_java_v = new HashSet<> ();
					omg_java_v.add(ctr1Entry);
					omg_java_v.add(ctr2Entry);
					Map<String, Set<Long>> conflictMap = collectCtr(omg_java_v, ctr1, ctr2, ctr1_, ctr2_);
					conflictMap.put(ctr1Entry, ctr1.get(ctr1Entry));
					conflictMap.put(ctr2Entry, ctr2.get(ctr2Entry));
					conflict.add(conflictMap);
					
					//resolve conflict -> change to operator interface
//					Map<String, Set<Long>> res = resolveTypeMerge(
//							ctr1Entry, ctr1.get(ctr1Entry),
//							ctr2Entry, ctr2.get(ctr2Entry));
					Map<String, Set<Long>> res = resolveTypeMerge(conflictMap);
					
					for (String key : res.keySet()) {
						Set<Long> e = res.get(key);
						pass.put(key, e);
						conflictEntities.addAll(e);
						LOGGER.trace(String.format("Added to conflict set: %s", e));
//						if (stupidprint && skip.contains(41634L)) System.out.println(key + " fugiu! por " + eid);
					}
				} else if (in1) {
//					pass.put(ctr1Entry, ctr1.get(ctr1Entry));
//					skip.addAll(ctr1.get(ctr1Entry));
//					if (stupidprint && skip.contains(41634L)) System.out.println(ctr1Entry + " fugiu! por " + eid);
				} else if (in2) {
//					pass.put(ctr2Entry, ctr2.get(ctr2Entry));
//					skip.addAll(ctr2.get(ctr2Entry));
//					if (stupidprint && skip.contains(41634L)) System.out.println(ctr2Entry + " fugiu! por " + eid);
				} else {
					LOGGER.error("ai ai !");
				}
			}
		}
		
		LOGGER.debug("resolve non conflicting clusters");
		eids.removeAll(conflictEntities);
		Set<Long> skip = new HashSet<> ();
		for (long eid : eids) {
			LOGGER.trace("Process non conflict: " + eid);
			if (!skip.contains(eid)) {
				String ctr1Entry = ctr1_.get(eid);
				String ctr2Entry = ctr2_.get(eid);
				boolean in1 = ctr1Entry != null;
				boolean in2 = ctr2Entry != null;
				if (in1 && in2) {
					LOGGER.error("why !!!!");
				}else if (in1) {
					pass.put(ctr1Entry, ctr1.get(ctr1Entry));
					LOGGER.trace(String.format("Added to processed set: %s", ctr1.get(ctr1Entry)));
					skip.addAll(ctr1.get(ctr1Entry));
				} else if (in2) {
					pass.put(ctr2Entry, ctr2.get(ctr2Entry));
					LOGGER.trace(String.format("Added to processed set: %s", ctr2.get(ctr2Entry)));
					skip.addAll(ctr2.get(ctr2Entry));
				} else {
					LOGGER.error("ai ai ai ai!");
				}
			}
		}
		
		System.out.println("P: " + pass.size());
		System.out.println("C: " + conflict.size());
		return pass;
	}
	
	private static Map<Long, String> invert(Map<String, Set<Long>> ctr1) {
		Map<Long, String> inv = new HashMap<> ();
		for (String k : ctr1.keySet()) {
			for (long v : ctr1.get(k)) {
				if (inv.put(v, k) != null) {
					LOGGER.error("ai !");
				}
			}
		}
		return inv;
	}
	
	public static Map<String, Set<Long>> resolveTypeMerge(Map<String, Set<Long>> conflictMap) {
//		System.out.println(String.format("%s <M> %s", c1, c2));
		Map<String, Set<Long>> result = new HashMap<> ();
		List<String> c = new ArrayList<> ();
		Set<Long> e = new HashSet<> ();
		for (String k : conflictMap.keySet()) {
			e.addAll(conflictMap.get(k));
			c.add(k);
		}

		result.put(String.format("(%s)", StringUtils.join(c, "_<M>_")), e);

		return result;
	}
	
	public static Map<String, Set<Long>> resolveTypeMerge(String c1, Set<Long> e1, String c2, Set<Long> e2) {
//		System.out.println(String.format("%s <M> %s", c1, c2));
		Map<String, Set<Long>> result = new HashMap<> ();
		
		Set<Long> e = new HashSet<> (e1);
		e.addAll(e2);
		result.put("(" + c1 + "#" + c2 + ")", e);

		return result;
	}
	
	private static Map<String, Set<Long>> collectCtr(Set<String> c, 
			Map<String, Set<Long>> ctr1, Map<String, Set<Long>> ctr2, 
			Map<Long, String> ctr1_, Map<Long, String> ctr2_) {
		Map<String, Set<Long>> a = new HashMap<> ();
		Set<String> v = new HashSet<> ();
		
		//Y U N BUILD GRAPH ?
		while (!c.isEmpty()) {
			String c_ = c.iterator().next();
			c.remove(c_);
			v.add(c_);
			Set<Long> e = getCorrect(c_, ctr1, ctr2);
			c.addAll(collectCtrFrom(e, ctr1_, ctr2_));
			c.removeAll(v);
		}
		
		for (String v_ : v) {
			if (ctr1.containsKey(v_)) a.put(v_, ctr1.get(v_));
			if (ctr2.containsKey(v_)) a.put(v_, ctr2.get(v_));
		}
		return a;
	}

	/**
	 * all this just because I am lazy to implement a graph and do traversal
	 * shame on me
	 * @param e
	 * @param ctr1_
	 * @param ctr2_
	 * @return
	 */
	private static Set<String> collectCtrFrom(Set<Long> e, 
			Map<Long, String> ctr1_, Map<Long, String> ctr2_) {
		Set<String> omg = new HashSet<> ();
		for (long e_ : e) {
			if (ctr1_.containsKey(e_)) omg.add(ctr1_.get(e_));
			if (ctr2_.containsKey(e_)) omg.add(ctr2_.get(e_));
		}
		return omg;
	}

	/**
	 * c_ is either found in ctr1 or ctr2 just that ! (why not merge ??)
	 * @param c_
	 * @param ctr1
	 * @param ctr2
	 * @return
	 */
	private static Set<Long> getCorrect(String c_, Map<String, Set<Long>> ctr1,
			Map<String, Set<Long>> ctr2) {
		if (ctr1.containsKey(c_) && ctr2.containsKey(c_)) {
			LOGGER.error("duplicate: " + c_);
		}
		if (ctr1.containsKey(c_)) return ctr1.get(c_);
		if (ctr2.containsKey(c_)) return ctr2.get(c_);
		
		throw new RuntimeException("wut " + c_ + " not found !!!");
	}
}
