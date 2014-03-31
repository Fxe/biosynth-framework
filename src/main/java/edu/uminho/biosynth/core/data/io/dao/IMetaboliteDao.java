package edu.uminho.biosynth.core.data.io.dao;

import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.components.GenericMetabolite;

public interface IMetaboliteDao<M extends GenericMetabolite> {
	
	public M getMetaboliteById(Serializable id);
	public M getMetaboliteByEntry(String entry);
	public M saveMetabolite(M metabolite);
	public Serializable saveMetabolite(Object metabolite);
	public List<Serializable> getAllMetaboliteIds();
	public List<String> getAllMetaboliteEntries();
	@Deprecated
	public M find(Serializable id);
	
	@Deprecated
	public List<M> findAll();
	@Deprecated
	public Serializable save(M entity);
}
