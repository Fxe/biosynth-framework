package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import org.hibernate.SessionFactory;

import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.AbstractHbmReactionDaoImpl;

public class HbmModelSeedReactionDaoImpl extends AbstractHbmReactionDaoImpl<ModelSeedReactionEntity> {

  public HbmModelSeedReactionDaoImpl(SessionFactory sessionFactory) {
    super(sessionFactory, ModelSeedReactionEntity.class);
  }
}
