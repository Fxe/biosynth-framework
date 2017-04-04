package pt.uminho.sysbio.biosynthframework.io.biodb;

import org.hibernate.SessionFactory;

import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.io.AbstractHbmReactionDaoImpl;

public class HbmModelSeedReactionDaoImpl extends AbstractHbmReactionDaoImpl<ModelSeedReactionEntity> {

  public HbmModelSeedReactionDaoImpl(SessionFactory sessionFactory) {
    super(sessionFactory, ModelSeedReactionEntity.class);
  }
}
