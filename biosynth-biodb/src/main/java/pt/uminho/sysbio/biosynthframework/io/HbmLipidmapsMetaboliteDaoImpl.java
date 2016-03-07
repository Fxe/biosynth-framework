package pt.uminho.sysbio.biosynthframework.io;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsMetaboliteEntity;

public class HbmLipidmapsMetaboliteDaoImpl implements MetaboliteDao<LipidmapsMetaboliteEntity> {


  private final SessionFactory sessionFactory;
  
  private Session getSession() {
      return this.sessionFactory.getCurrentSession();
  }

  public SessionFactory getSessionFactory() { return sessionFactory;}
  
  @Autowired  
  public HbmLipidmapsMetaboliteDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public LipidmapsMetaboliteEntity getMetaboliteById(Serializable id) {
    Object cpd = this.getSession().get(LipidmapsMetaboliteEntity.class, id);
    return LipidmapsMetaboliteEntity.class.cast(cpd);
  }

  @Override
  public LipidmapsMetaboliteEntity getMetaboliteByEntry(String entry) {
    LipidmapsMetaboliteEntity cpd = null;
    Criteria criteria = this.getSession().createCriteria(LipidmapsMetaboliteEntity.class);
    Object o = criteria.add(Restrictions.eq("entry", entry)).uniqueResult();
    if (o != null) {
      cpd = LipidmapsMetaboliteEntity.class.cast(o);
    }
    return cpd;
  }

  @Override
  public LipidmapsMetaboliteEntity saveMetabolite(LipidmapsMetaboliteEntity metabolite) {
    this.getSession().save(metabolite);
    return metabolite;
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    return this.getSession().save(metabolite);
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    Query query = this.getSession().createQuery("SELECT cpd.id FROM LipidmapsMetaboliteEntity cpd");
    @SuppressWarnings("unchecked")
    List<Serializable> res = query.list();
    return res;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    Query query = this.getSession().createQuery("SELECT cpd.entry FROM LipidmapsMetaboliteEntity cpd");
    @SuppressWarnings("unchecked")
    List<String> res = query.list();
    return res;
  }

  @Override
  public Serializable save(LipidmapsMetaboliteEntity entity) {
    return this.getSession().save(entity);
  }

}
