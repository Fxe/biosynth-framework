package edu.uminho.biosynth.core.data.io.dao;

import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.components.GenericMetabolite;

public interface IMetaboliteDao<M extends GenericMetabolite> {
	
	public M getMetaboliteInformation(Serializable id);
	public M saveMetaboliteInformation(M metabolite);
	public List<Serializable> getAllMetaboliteIds();
	public M find(Serializable id);
	
	public List<M> findAll();
	public Serializable save(M entity);
	public Serializable save(Object entity);
}
