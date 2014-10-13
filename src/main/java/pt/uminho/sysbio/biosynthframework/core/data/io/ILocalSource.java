package pt.uminho.sysbio.biosynthframework.core.data.io;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.GenericEnzyme;
import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;

@Deprecated
public interface ILocalSource extends ISource {
	public boolean hasCompoundInformation(String cpdId);
	public boolean hasReactionInformation(String rxnId);
	public boolean hasEnzymeInformation(String ecnId);
	public boolean hasPairInformation(String rprId);
	
	public void saveCompoundInformation( GenericMetabolite cpd);
	public void saveReactionInformation( GenericReaction rxn);
	public void saveEnzymeInformation( GenericEnzyme ecn);
	public void savePairInformation( GenericReactionPair rpr);
	
	public boolean removeReactionInformation( String rxnId);
	
	public void addMetatagToReaction(String rxnId, String metatag);
	public void addMetatagsToReaction(String rxnId, Set<String> metatags);
	public void addMetatagsToReactions(Map<String, Set<String>> metatags);
	
	public boolean isInitialized();
}
