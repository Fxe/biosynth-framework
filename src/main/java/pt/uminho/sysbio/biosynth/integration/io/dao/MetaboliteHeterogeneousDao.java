package pt.uminho.sysbio.biosynth.integration.io.dao;

import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.components.Metabolite;

/**
 * Data Access Interface for Multiple Domain metabolites.
 * For single domain use MetaboliteDao
 * 
 * @author Filipe Liu
 *
 * @param <M>
 */
public interface MetaboliteHeterogeneousDao<M extends Metabolite> {

	/**
	 * Looks up a metabolite entity by id in the <code>tag</code> domain. 
	 * Note that the id is usually a surrogate key, which means it is 
	 * bad practice to refer them in this way. Use the entry instead 
	 * which is a database specified identifier. 
	 * 
	 * @param tag the domain of the metabolite
	 * @param id the id of the metabolite
	 * @return the metabolite with id <code>id</code> if found
	 */
	public M getMetaboliteById(String tag, Serializable id);

	public M getMetaboliteByEntry(String tag, String entry);

	public M saveMetabolite(String tag, M metabolite);
	
	
	/**
	 * Lists all ids from all domain tags.
	 * 
	 * @return a set that contains metabolite ids
	 */
	public List<Long> getGlobalAllMetaboliteIds();
	
	/**
	 * List all ids from a specific domain <code>tag</code>.
	 * 
	 * @param tag the domain of the metabolites
	 * @return a set that contains metabolite ids
	 */
	public List<Long> getAllMetaboliteIds(String tag);
	
	public List<String> getAllMetaboliteEntries(String tag);
}
