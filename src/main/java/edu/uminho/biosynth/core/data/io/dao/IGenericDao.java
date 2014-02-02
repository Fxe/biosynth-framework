package edu.uminho.biosynth.core.data.io.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;

public interface IGenericDao {
	
	public <T> T find(Class<T> type, Serializable id);
    public <T> T[] find(Class<T> type, Serializable... ids);
    
    public <T> List<T> criteria(Class<T> type, Criterion criterion);
    public <T> List<T> projection(Class<T> type, Projection projection);
    
    public List<Object[]> query(String queryString);
    
    public <T> T getReference(Class<T> type, Serializable id);
    public <T> T[] getReferences(Class<T> type, Serializable... ids);

    public Serializable save(Object entity);
    public Serializable[] save(Object... entities);
    
    public void saveOrUpdate(Object entity);

    public boolean remove(Object entity);
    public void remove(Object... entities);

    public boolean removeById(Class<?> type, Serializable id);
    public void removeByIds(Class<?> type, Serializable... ids);

    public <T> List<T> findAll(Class<T> type);

    public boolean isAttached(Object entity);

    public void refresh(Object... entities);

    public void flush();

}
