package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.io.Serializable;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.Reaction;

public interface ReactionHeterogeneousDao<R extends Reaction> {

	/**
	 * Looks up a reaction entity by id in the <code>tag</code> domain. 
	 * Note that the id is usually a surrogate key, which means it is 
	 * bad practice to refer them in this way. Use the entry instead 
	 * which is a database specified identifier. 
	 * 
	 * @param tag the domain of the reaction
	 * @param id the id of the reaction
	 * @return the reaction with id <code>id</code> if found
	 */
	public R getReactionById(String tag, Serializable id);

	public R getReactionByEntry(String tag, String entry);

	public R saveReaction(String tag, R reaction);
	
	/**
	 * Lists all ids from all domain tags.
	 * 
	 * @return a set that contains reaction ids
	 */
	public List<Long> getGlobalAllReactionIds();
	
	/**
	 * List all ids from a specific domain <code>tag</code>.
	 * 
	 * @param tag the domain of the reactions
	 * @return a set that contains reaction ids
	 */
	public List<Long> getAllReactionIds(String tag);
	
	public List<String> getAllReactionEntries(String tag);
}
