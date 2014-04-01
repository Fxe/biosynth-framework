package edu.uminho.biosynth.core.data.io.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;

import edu.uminho.biosynth.core.data.io.dao.IGenericDao;

public class GenericEntityDaoImpl implements IGenericDao {

	protected SessionFactory sessionFactory;
	public SessionFactory getSessionFactory() { return sessionFactory;}
	public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}

	public GenericEntityDaoImpl() {};
	
	public GenericEntityDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Session currentSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@Override
	public <T> T find(Class<T> type, Serializable id) {
//		System.out.println(type.getClass().toString());
		return type.cast(this.currentSession().get(type, id));
	}

	@Override
	public <T> List<T> find(Class<T> type, Serializable... ids) {
		List<T> res = new ArrayList<> ();
		for (int i = 0; i < ids.length; i++) {
			res.add(find(type, ids[i]));
			System.out.println(res);
		}
		return res;
	}
	
	@Override
	public <T> T getReference(Class<T> type, Serializable id) {
		return type.cast(this.currentSession().load(type, id));
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
	
	@SuppressWarnings("unchecked")
	public <T> List<T> projection(Class<T> type, Projection projection) {
		System.out.println(projection);
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(type).setProjection(projection);
//		System.out.println(criteria.);
		System.out.println("hi");
		System.out.println(criteria.list());
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> criteria(Class<T> type, Criterion criterion) {
		Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(type).add(criterion);
		
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> query(String queryString) {
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
//		Set<Integer> aux = new HashSet<> ();
//		aux.add(317772);
//		aux.add(1017310);aux.add(839831);
//		query.setParameterList("cpdIdList", aux);
		return query.list();
	}
	
	@Override
	public void saveOrUpdate(Object entity) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(entity);
	}
	
	@Override
	public Query createQuery(String query) {
		return this.sessionFactory.getCurrentSession().createQuery(query);
	}

}
