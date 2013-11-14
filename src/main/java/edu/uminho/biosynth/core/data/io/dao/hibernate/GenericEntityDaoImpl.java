package edu.uminho.biosynth.core.data.io.dao.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import edu.uminho.biosynth.core.data.io.dao.GenericEntityDAO;

public class GenericEntityDaoImpl implements GenericEntityDAO {

	protected SessionFactory sessionFactory;
	
	public GenericEntityDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Session currentSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T find(Class<T> type, Serializable id) {
		return (T) this.currentSession().load(type, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] find(Class<T> type, Serializable... ids) {
		Object[] res = new Object[ids.length];
		for (int i = 0; i < ids.length; i++) {
			res[i] = find(type, ids[i]);
		}
		return (T[]) res;
	}

	@Override
	public <T> T getReference(Class<T> type, Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] getReferences(Class<T> type, Serializable... ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable save(Object entity) {
		return this.sessionFactory.getCurrentSession().save(entity);
	}
	@Override
	public Serializable[] save(Object... entities) {
		Serializable[] ret = new Serializable[entities.length];
		for (int i = 0; i < entities.length; i++) {
			ret[i] = this.save(entities[i]);
		}
		return ret;
	}

	@Override
	public boolean remove(Object entity) {
		this.sessionFactory.getCurrentSession().delete(entity);
		return true;
	}
	@Override
	public void remove(Object... entities) {
		for (Object entity : entities) {
			this.refresh(entity);
		}
	}

	@Override
	public boolean removeById(Class<?> type, Serializable id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeByIds(Class<?> type, Serializable... ids) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> findAll(Class<T> type) {
		Criteria criteria = this.currentSession().createCriteria(type);
		return criteria.list();
	}

	@Override
	public boolean isAttached(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh(Object... entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}
	
	

}
