package edu.uminho.biosynth.core.components;

import java.util.ArrayList;
import java.util.List;

public class DefaultGenericReaction extends GenericReaction {
	
	private static final long serialVersionUID = 1L;
	private List<StoichiometryPair> reactant = new ArrayList<> ();
	private List<StoichiometryPair> product = new ArrayList<> ();
	
	
	public List<StoichiometryPair> getReactant() {
		return reactant;
	}
	public void setReactant(List<StoichiometryPair> reactant) {
		this.reactant = reactant;
	}
	public List<StoichiometryPair> getProduct() {
		return product;
	}
	public void setProduct(List<StoichiometryPair> product) {
		this.product = product;
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("entry:").append(entry).append(sep);
		sb.append("name:").append(name).append(sep);
		sb.append("reactants:").append(reactant).append(sep);
		sb.append("products:").append(product);
		return sb.toString();
	}
}
