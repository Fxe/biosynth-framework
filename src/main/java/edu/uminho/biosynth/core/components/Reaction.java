package edu.uminho.biosynth.core.components;


public interface Reaction {
	public Long getId();
	public String getEntry();
	
	public Orientation getOrientation();
	
//	public List<StoichiometryPair> getLeft();
//	public Map<M, Double> getRight();
}
