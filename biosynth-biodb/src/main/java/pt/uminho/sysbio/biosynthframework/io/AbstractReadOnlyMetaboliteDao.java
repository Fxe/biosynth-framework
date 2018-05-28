package pt.uminho.sysbio.biosynthframework.io;

import java.io.Serializable;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public abstract class AbstractReadOnlyMetaboliteDao<M extends Metabolite> 
extends AbstractMetaboliteDao<M> {
  
  public AbstractReadOnlyMetaboliteDao(String version) {
    super(version);
  }

  @Override
  public abstract List<Serializable> getAllMetaboliteIds();
  
  @Override
  public abstract List<String> getAllMetaboliteEntries();

  @Override
  public abstract M getMetaboliteById(Serializable id);

  @Override
  public abstract M getMetaboliteByEntry(String entry);

  @Override
  public M saveMetabolite(M metabolite) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Serializable save(M entity) {
    throw new RuntimeException("Operation not supported");
  }

}
