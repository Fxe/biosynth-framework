package edu.uminho.biosynth.core.components;

import java.util.Map;
import java.util.Set;

public interface IReactionEntity<M extends AbstractGenericMetabolite> {
	
	public Set<M> getReactants();
	public Set<M> getProducts();
	
	public Map<M, Double> getReactantsStoichiometryMap();
	public Map<M, Double> getProductsStoichiometryMap();
}
