package pt.uminho.sysbio.biosynthframework.io;

import java.util.Set;

import pt.uminho.sysbio.biosynthframework.Reaction;

/**
 * 
 * @author Filipe Liu
 *
 * @param <R> a GenericReaction entity.
 */
public interface ReactionDao<R extends Reaction> {

	/**
	 * Looks up a reaction entity by id. Note that the id is usually a 
	 * surrogate key, which means it is bad practice to refer them in
	 * this way. Use the entry instead which is a database specified
	 * identifier. 
	 * 
	 * @param id the id of the reaction
	 * @return the reaction with id <code>id</code> if found
	 */
	public R getReactionById(Long id);
	
	/**
	 * Looks up a reaction entity by it's entry.
	 * 
	 * @param entry the entry of the reaction
	 * @return the reaction with entry <code>entry</code> if found
	 */
	public R getReactionByEntry(String entry);
	
	/**
	 * 
	 * @param reaction the reaction entity to be inserted
	 * 
	 * @return the reaction saved with it's id updated with the
	 * generated id (only if the id is set as null otherwise the
	 * assigned id is used)
	 */
	public R saveReaction(R reaction);
	
	/**
	 * List all reaction id
	 * 
	 * @return a set that contains all reaction ids
	 */
	public Set<Long> getAllReactionIds();
	
	/**
	 * List all reaction entry
	 * 
	 * @return a set that contains all reaction entries
	 */
	public Set<String> getAllReactionEntries();

}
