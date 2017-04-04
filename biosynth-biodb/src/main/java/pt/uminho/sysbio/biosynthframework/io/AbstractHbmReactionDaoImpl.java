package pt.uminho.sysbio.biosynthframework.io;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.Reaction;

public abstract class AbstractHbmReactionDaoImpl<R extends Reaction> implements ReactionDao<R> {

  protected final SessionFactory sessionFactory;
  private final Class<R> clazz;
  private final String listIdQuery;
  private final String listEntryQuery;
  
  protected Session getSession() {
    return this.sessionFactory.getCurrentSession();
  }

  protected SessionFactory getSessionFactory() { return sessionFactory;}
  
  @Autowired  
  public AbstractHbmReactionDaoImpl(SessionFactory sessionFactory, Class<R> clazz) {
    this.sessionFactory = sessionFactory;
    this.clazz = clazz;
    this.listIdQuery = String.format("SELECT rxn.id FROM %s rxn", clazz.getSimpleName());
    this.listEntryQuery = String.format("SELECT rxn.entry FROM %s rxn", clazz.getSimpleName());
  }
  
  @Override
  public R getReactionById(Long id) {
    Object rxn = this.getSession().get(clazz, id);
    return clazz.cast(rxn);
  }

  @Override
  public R getReactionByEntry(String entry) {
    R rxn = null;
    Criteria criteria = this.getSession().createCriteria(clazz);
    Object o = criteria.add(Restrictions.eq("entry", entry)).uniqueResult();
    if (o != null) {
      rxn = clazz.cast(o);
    }
    return rxn;
  }

  @Override
  public R saveReaction(R reaction) {
    this.getSession().save(reaction);
    return reaction;
  }

  @Override
  public Set<Long> getAllReactionIds() {
    Query query = this.getSession().createQuery(listIdQuery);
    @SuppressWarnings("unchecked")
    Set<Long> res =  new HashSet<> (query.list());
    return res;
  }

  @Override
  public Set<String> getAllReactionEntries() {
    Query query = this.getSession().createQuery(listEntryQuery);
    @SuppressWarnings("unchecked")
    Set<String> res = new HashSet<> (query.list());
    return res;
  }

}
