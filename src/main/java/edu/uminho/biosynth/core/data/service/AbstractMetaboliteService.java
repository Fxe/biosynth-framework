package edu.uminho.biosynth.core.data.service;

import java.util.List;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.io.dao.IGenericEntityDao;

public abstract class AbstractMetaboliteService<T extends GenericMetabolite> 
	implements IMetaboliteService<T> {
	
	private IGenericEntityDao dao;
	private Class<T> metaboliteClass;
	
	public AbstractMetaboliteService(IGenericEntityDao dao) {
		this.dao = dao;
	}
	
	@Override
	public List<T> getAllMetabolites() {
		return dao.findAll(metaboliteClass);
	}
}
