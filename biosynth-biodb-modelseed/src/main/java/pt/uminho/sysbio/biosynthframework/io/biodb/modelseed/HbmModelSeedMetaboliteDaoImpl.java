package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import org.hibernate.SessionFactory;

import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.AbstractHbmMetaboliteDao;

public class HbmModelSeedMetaboliteDaoImpl extends AbstractHbmMetaboliteDao<ModelSeedMetaboliteEntity> {

  public HbmModelSeedMetaboliteDaoImpl(SessionFactory sessionFactory) {
    super(sessionFactory, ModelSeedMetaboliteEntity.class);
  }
}
