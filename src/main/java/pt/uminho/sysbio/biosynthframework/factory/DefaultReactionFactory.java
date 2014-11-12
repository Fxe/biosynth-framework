package pt.uminho.sysbio.biosynthframework.factory;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.DefaultReaction;

public class DefaultReactionFactory extends AbstractGenericEntityFactory {

	private Map<String, Double> left = new HashMap<> ();
	private Map<String, Double> right = new HashMap<> ();
	
	public DefaultReactionFactory withEntry(String entry) {
		this.entry = entry;
		return this;
	}
	
	public DefaultReactionFactory withName(String name) {
		this.name = name;
		return this;
	}
	
	public DefaultReactionFactory withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public DefaultReactionFactory withUnitLeftStoichiometry(String[] metabolites) {
		for (String metabolite : metabolites) this.left.put(metabolite, 1.0d);
		return this;
	}
	
	public DefaultReactionFactory withUnitRightStoichiometry(String[] metabolites) {
		for (String metabolite : metabolites) this.right.put(metabolite, 1.0d);
		return this;
	}
	
	public DefaultReaction build() {
		DefaultReaction rxn = new DefaultReaction();
		rxn.setId(id);
		rxn.setEntry(entry);
		rxn.setName(name);
		rxn.setDescription(description);
		rxn.setSource(source);
		
		rxn.setReactantStoichiometry(left);
		rxn.setProductStoichiometry(right);
		
		return rxn;
	}
}
