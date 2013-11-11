package edu.uminho.biosynth.core.data.io.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import edu.uminho.biosynth.core.components.AbstractGenericEntity;

public abstract class GenericEntityDaoImpl<T extends AbstractGenericEntity> implements GenericEntityDAO<T>{

	protected SessionFactory sessionFactory;
	
	public GenericEntityDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public void addEntity(T entity) {
		this.sessionFactory.getCurrentSession().save(entity);
	}

	@Override
	public abstract List<T> getAllEntities();

	@Override
	public void deleteEntity(Integer entityId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public abstract T getEntityById(int id);

	@Override
	public abstract T getEntityByEntry(String entry);
	

	@Override
	public void updateEntity(T entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public abstract boolean contains(int id);

	@Override
	public boolean contains(String entry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(T entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
