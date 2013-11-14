package edu.uminho.biosynth.core.data.io.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericEntityDAO {
	
	public <T> T find(Class<T> type, Serializable id);
    public <T> T[] find(Class<T> type, Serializable... ids);
    
    public <T> T getReference(Class<T> type, Serializable id);
    public <T> T[] getReferences(Class<T> type, Serializable... ids);

    public Serializable save(Object entity);
    public Serializable[] save(Object... entities);

    public boolean remove(Object entity);
    public void remove(Object... entities);

    public boolean removeById(Class<?> type, Serializable id);
    public void removeByIds(Class<?> type, Serializable... ids);

    public <T> List<T> findAll(Class<T> type);

    public boolean isAttached(Object entity);

    public void refresh(Object... entities);

    public void flush();

}
