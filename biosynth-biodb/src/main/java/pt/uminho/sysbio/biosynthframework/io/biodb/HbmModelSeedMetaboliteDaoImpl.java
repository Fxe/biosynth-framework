package pt.uminho.sysbio.biosynthframework.io.biodb;

import org.hibernate.SessionFactory;

import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.AbstractHbmMetaboliteDao;

public class HbmModelSeedMetaboliteDaoImpl extends AbstractHbmMetaboliteDao<ModelSeedMetaboliteEntity> {

  public HbmModelSeedMetaboliteDaoImpl(SessionFactory sessionFactory) {
    super(sessionFactory, ModelSeedMetaboliteEntity.class);
  }
}
