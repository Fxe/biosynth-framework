package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.Bigg2MetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

@Repository
public class HbmBigg2MetaboliteDaoImpl implements MetaboliteDao<Bigg2MetaboliteEntity> {

  private SessionFactory sessionFactory;
  
  private Session getSession() {
      return this.sessionFactory.getCurrentSession();
  }
  
  @Autowired
  public HbmBigg2MetaboliteDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
  
  @Override
  public Bigg2MetaboliteEntity getMetaboliteById(Serializable id) {
    Object cpd = this.getSession().get(Bigg2MetaboliteEntity.class, id);
    return Bigg2MetaboliteEntity.class.cast(cpd);
  }

  @Override
  public Bigg2MetaboliteEntity getMetaboliteByEntry(String entry) {
    Criteria criteria = this.getSession()
        .createCriteria(Bigg2MetaboliteEntity.class)
        .add(Restrictions.eq("entry", entry));

    return (Bigg2MetaboliteEntity) criteria.uniqueResult();
  }

  @Override
  public Bigg2MetaboliteEntity saveMetabolite(Bigg2MetaboliteEntity metabolite) {
    this.getSession().save(metabolite);
    return metabolite;
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    this.getSession().save(metabolite);
    return null;
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    Query query = this.getSession().createQuery("SELECT cpd.id FROM Bigg2MetaboliteEntity cpd");
    @SuppressWarnings("unchecked")
    List<Serializable> res = query.list();
    return res;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    Query query = this.getSession().createQuery("SELECT cpd.entry FROM Bigg2MetaboliteEntity cpd");
    @SuppressWarnings("unchecked")
    List<String> res = query.list();
    return res;
  }

  @Override
  public Serializable save(Bigg2MetaboliteEntity entity) {
    this.getSession().save(entity);
    return null;
  }

}
