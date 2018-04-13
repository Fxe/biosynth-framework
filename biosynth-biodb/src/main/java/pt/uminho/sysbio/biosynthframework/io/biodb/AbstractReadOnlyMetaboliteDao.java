package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.Serializable;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public abstract class AbstractReadOnlyMetaboliteDao<M extends Metabolite> implements MetaboliteDao<M> {

  @Override
  public abstract List<String> getAllMetaboliteEntries();

  @Override
  public abstract M getMetaboliteByEntry(String entry);
  
  @Override
  public M getMetaboliteById(Serializable id) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public M saveMetabolite(M metabolite) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    throw new RuntimeException("Operation not supported");
  }
  
  @Override
  public Serializable save(M entity) {
    throw new RuntimeException("Operation not supported");
  }

}
