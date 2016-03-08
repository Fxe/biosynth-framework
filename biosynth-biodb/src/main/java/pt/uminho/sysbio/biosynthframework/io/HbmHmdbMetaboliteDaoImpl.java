package pt.uminho.sysbio.biosynthframework.io;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteEntity;

public class HbmHmdbMetaboliteDaoImpl extends AbstractHbmMetaboliteDao<HmdbMetaboliteEntity> {

  @Autowired
  public HbmHmdbMetaboliteDaoImpl(SessionFactory sessionFactory) {
    super(sessionFactory, HmdbMetaboliteEntity.class);
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    Query query = this.getSession().createQuery("SELECT cpd.id FROM HmdbMetaboliteEntity cpd");
    @SuppressWarnings("unchecked")
    List<Serializable> res = query.list();
    return res;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    Query query = this.getSession().createQuery("SELECT cpd.entry FROM HmdbMetaboliteEntity cpd");
    @SuppressWarnings("unchecked")
    List<String> res = query.list();
    return res;
  }

}
