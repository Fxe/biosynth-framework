package edu.uminho.biosynth.core.data.io.dao;

import java.util.List;

import edu.uminho.biosynth.core.components.AbstractGenericEntity;

public interface GenericEntityDAO<T extends AbstractGenericEntity> {
	public void addEntity(T entity);
	
	public T getEntityById(int id);
	public T getEntityByEntry(String entry);
	
	public boolean contains(int id);
	public boolean contains(String entry);
	public boolean contains(T entity);
	
	public void updateEntity(T entity);
	public List<T> getAllEntities();
	public void deleteEntity(Integer entityId);
}
