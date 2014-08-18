package edu.uminho.biosynth.core.components.optimization;

public interface SolutionSet<S extends Solution<?>> {
	
	public S get(Long id);
	public void add(S solution);
	public void remove(S solution);
	public void size();
	public void clear();
}
