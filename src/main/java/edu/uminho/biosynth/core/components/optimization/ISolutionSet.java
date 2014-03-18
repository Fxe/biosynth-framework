package edu.uminho.biosynth.core.components.optimization;

public interface ISolutionSet<S> {
	
	public void add(S solution);
	public void remove(S solution);
	public void size();
	public void clear();
}
