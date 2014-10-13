package pt.uminho.sysbio.biosynthframework.core.data.io;

import java.util.Set;

import pt.uminho.sysbio.biosynthframework.GenericEnzyme;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;

@Deprecated
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
