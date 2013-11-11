package edu.uminho.biosynth.core.components.representation;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;

public interface IMetabolicRepresentation {

	public boolean isReactionPair();
	
	public boolean addReactionPair(GenericReactionPair rpr);
	public boolean addReactionPair(GenericReactionPair rpr, boolean duplicateForReverse);
	
	public boolean addReaction(GenericReaction rxn);
	public boolean addReaction(GenericReaction rxn, boolean duplicateForReverse);
	
	public boolean addMetabolite(GenericMetabolite cpd);
	public boolean removeReaction();
	public boolean removeMetabolite();
}
