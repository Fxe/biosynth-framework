package pt.uminho.sysbio.biosynthframework.io;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public abstract class AbstractHbmMetaboliteDao<T extends Metabolite> implements MetaboliteDao<T> {

  protected final SessionFactory sessionFactory;
  private final Class<T> clazz;
  
  protected Session getSession() {
      return this.sessionFactory.getCurrentSession();
  }

  protected SessionFactory getSessionFactory() { return sessionFactory;}
  
  @Autowired  
  public AbstractHbmMetaboliteDao(SessionFactory sessionFactory, Class<T> clazz) {
    this.sessionFactory = sessionFactory;
    this.clazz = clazz;
  }
  
  @Override
  public T getMetaboliteById(Serializable id) {
    Object cpd = this.getSession().get(clazz, id);
    return clazz.cast(cpd);
  }

  @Override
  public T getMetaboliteByEntry(String entry) {
    T cpd = null;
    Criteria criteria = this.getSession().createCriteria(clazz);
    Object o = criteria.add(Restrictions.eq("entry", entry)).uniqueResult();
    if (o != null) {
      cpd = clazz.cast(o);
    }
    return cpd;
  }

  @Override
  public T saveMetabolite(T metabolite) {
    this.getSession().save(metabolite);
    return metabolite;
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    return this.getSession().save(metabolite);
  }

  @Override
  public abstract List<Serializable> getAllMetaboliteIds();

  @Override
  public abstract List<String> getAllMetaboliteEntries();

  @Override
  public Serializable save(T entity) {
    return this.getSession().save(entity);
  }

}
