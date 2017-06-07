package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2ReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class HbmBigg2ReactionDaoImpl implements ReactionDao<Bigg2ReactionEntity> {

  
  private SessionFactory sessionFactory;
  
  private Session getSession() {
      return this.sessionFactory.getCurrentSession();
  }
  
  @Autowired
  public HbmBigg2ReactionDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
  
  @Override
  public Bigg2ReactionEntity getReactionById(Long id) {
    Object rxn = this.getSession().get(Bigg2ReactionEntity.class, id);
    return Bigg2ReactionEntity.class.cast(rxn);
  }

  @Override
  public Bigg2ReactionEntity getReactionByEntry(String entry) {
    Criteria criteria = this.getSession()
        .createCriteria(Bigg2ReactionEntity.class)
        .add(Restrictions.eq("entry", entry));

    return (Bigg2ReactionEntity) criteria.uniqueResult();
  }

  @Override
  public Bigg2ReactionEntity saveReaction(Bigg2ReactionEntity reaction) {
    this.getSession().save(reaction);
    return reaction;
  }

  @Override
  public Set<Long> getAllReactionIds() {
    Query query = this.getSession().createQuery("SELECT cpd.id FROM Bigg2ReactionEntity cpd");
    @SuppressWarnings("unchecked")
    List<Long> res = query.list();
    return new HashSet<> (res);
  }

  @Override
  public Set<String> getAllReactionEntries() {
    Query query = this.getSession().createQuery("SELECT cpd.entry FROM Bigg2ReactionEntity cpd");
    @SuppressWarnings("unchecked")
    List<String> res = query.list();
    return new HashSet<> (res);
  }

}
