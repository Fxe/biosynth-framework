package pt.uminho.sysbio.biosynthframework.io;

import java.io.Serializable;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.Metabolite;

/**
 * 
 * @author Filipe Liu
 *
 * @param <M> a GenericMetabolite entity.
 */
public interface MetaboliteDao<M extends Metabolite> {
	
	/**
	 * Looks up a metabolite entity by id. Note that the id is usually a 
	 * surrogate key, which means it is bad practice to refer them in
	 * this way. Use the entry instead which is a database specified
	 * identifier. 
	 * 
	 * @param id the id of the metabolite
	 * @return the metabolite with id <code>id</code> if found
	 */
	public M getMetaboliteById(Serializable id);
	
	/**
	 * Looks up a metabolite entity by it's entry.
	 * 
	 * @param entry the entry of the metabolite
	 * @return the metabolite with entry <code>entry</code> if found
	 */
	public M getMetaboliteByEntry(String entry);
	
	/**
	 * 
	 * @param metabolite the metabolite entity to be inserted
	 * 
	 * @return the metabolite saved with it's id updated with the
	 * generated id (only if the id is set as null otherwise the
	 * assigned id is used)
	 */
	public M saveMetabolite(M metabolite);
	
	
	public Serializable saveMetabolite(Object metabolite);
	
	/**
	 * List all metabolite id
	 * 
	 * @return a set that contains all metabolite ids
	 */
	public List<Serializable> getAllMetaboliteIds();
	
	/**
	 * List all metabolite entry
	 * 
	 * @return a set that contains all metabolite entries
	 */
	public List<String> getAllMetaboliteEntries();

	@Deprecated
	public Serializable save(M entity);
}
