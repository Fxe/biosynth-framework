package edu.uminho.biosynth.core.data.io.dao;

import java.io.Serializable;
import java.util.List;

import edu.uminho.biosynth.core.components.GenericMetabolite;

public interface IMetaboliteDao<C extends GenericMetabolite> {
	public C find(Serializable id);
	public List<C> findAll();
	public Serializable save(C entity);
}
