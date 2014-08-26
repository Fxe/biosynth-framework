package edu.uminho.biosynth.core.components;

import java.util.List;

public interface ChemicalReaction<M> {
	public List<M> getSubstrates();
	public List<M> getReactants();
}
