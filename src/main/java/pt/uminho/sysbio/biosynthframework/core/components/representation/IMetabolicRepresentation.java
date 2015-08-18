package pt.uminho.sysbio.biosynthframework.core.components.representation;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;

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
