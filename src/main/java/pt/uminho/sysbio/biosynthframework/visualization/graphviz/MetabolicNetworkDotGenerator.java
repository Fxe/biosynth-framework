package pt.uminho.sysbio.biosynthframework.visualization.graphviz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.Reaction;

public class MetabolicNetworkDotGenerator {
	
	private Map<String, Metabolite> metaboliteMap = new HashMap<> ();
	private List<Reaction> reactionList = new ArrayList<> ();
	
	public void addReaction(Reaction rxn) {
	  reactionList.add(rxn);
	  for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
	  	if (!metaboliteMap.containsKey(cpdEntry)) {
	  		GenericMetabolite cpd = new GenericMetabolite();
	  		cpd.setEntry(cpdEntry);
	  		metaboliteMap.put(cpdEntry, cpd);
	  	}
	  }
	  
	  for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
	  	if (!metaboliteMap.containsKey(cpdEntry)) {
	  		GenericMetabolite cpd = new GenericMetabolite();
	  		cpd.setEntry(cpdEntry);
	  		metaboliteMap.put(cpdEntry, cpd);
	  	}
	  }
  }
	
	public String build() {
		List<String> lines = new ArrayList<> ();
		lines.add("digraph {");
		for (Metabolite cpd : metaboliteMap.values()) {
			DotNode node = new DotNode();
			node.id = cpd.getEntry();
			node.setLabel(cpd.getEntry());
			
			lines.add(node.toString());
		}
		for (Reaction rxn : reactionList) {
			for (String cpdEntry : rxn.getLeftStoichiometry().keySet()) {
				String dotEdgeStr = String.format("%s->%s", cpdEntry, rxn.getEntry());
				lines.add(dotEdgeStr);
			}
			for (String cpdEntry : rxn.getRightStoichiometry().keySet()) {
				String dotEdgeStr = String.format("%s->%s", rxn.getEntry(), cpdEntry);
				lines.add(dotEdgeStr);
			}
		}
		lines.add("}");
		return StringUtils.join(lines, '\n');
	}
}
