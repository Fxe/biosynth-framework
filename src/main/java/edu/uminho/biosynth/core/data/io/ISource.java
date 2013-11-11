package edu.uminho.biosynth.core.data.io;

import java.util.Set;

import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;

public interface ISource {
	public GenericReaction getReactionInformation(String rxnId);
	public GenericMetabolite getMetaboliteInformation(String cpdId);
	public GenericEnzyme getEnzymeInformation(String ecnId);
	public GenericReactionPair getPairInformation(String rprId);
	
	public Set<String> getAllReactionIds();
	public Set<String> getAllMetabolitesIds();
	public Set<String> getAllEnzymeIds();
	public Set<String> getAllReactionPairIds();
	
	public void initialize();
}
