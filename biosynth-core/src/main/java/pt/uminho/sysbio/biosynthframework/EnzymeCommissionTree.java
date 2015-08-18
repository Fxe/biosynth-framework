package pt.uminho.sysbio.biosynthframework;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class EnzymeCommissionTree {
	
	private Map<Integer, Map<Integer, Map<Integer, Set<String>>>> tree = new TreeMap<>();
	
	
	public Map<Integer, Map<Integer, Map<Integer, Set<String>>>> getTree() {
		return tree;
	}
	public void setTree(
			Map<Integer, Map<Integer, Map<Integer, Set<String>>>> tree) {
		this.tree = tree;
	}

	public void clear() {
		tree.clear();
	}
	
	public void add(EnzymeCommissionNumber ecn) {
		Integer l1 = ecn.getLevel1();
		Integer l2 = ecn.getLevel2();
		Integer l3 = ecn.getLevel3();
		Integer l4 = ecn.getLevel4();
		if (l1 != null) {
			if ( !tree.containsKey(l1)) {
				tree.put(l1, new TreeMap<Integer, Map<Integer, Set<String>>> ());
			}
		}
		if (l2 != null) {
			Map<Integer, Map<Integer, Set<String>>> l2Map = tree.get(l1);
			if ( !l2Map.containsKey(l2)) {
				l2Map.put(l2, new TreeMap<Integer, Set<String>> ());
			}
		}
		if (l3 != null) {
			Map<Integer, Set<String>> l3Map = tree.get(l1).get(l2);
			if ( !l3Map.containsKey(l3)) {
				l3Map.put(l3, new TreeSet<String> ());
			}
		}
		if (l4 != null) {
			Set<String> l4Set = tree.get(l1).get(l2).get(l3);
			l4Set.add(ecn.toString());
		}
	}
	
	public String toPrettyString() {
		StringBuilder sb = new StringBuilder();
		for (Integer l1 : tree.keySet()) {
			sb.append(l1).append(".-.-.-").append('\n');
			for (Integer l2 : tree.get(l1).keySet()) {
				sb.append(String.format("\t%d.%d.-.-", l1, l2)).append('\n');
				for (Integer l3 : tree.get(l1).get(l2).keySet()) {
					sb.append(String.format("\t\t%d.%d.%d.- (%d)", l1, l2, l3, tree.get(l1).get(l2).get(l3).size())).append('\n');
				}
			}
		}
		return sb.toString();
	}
}
