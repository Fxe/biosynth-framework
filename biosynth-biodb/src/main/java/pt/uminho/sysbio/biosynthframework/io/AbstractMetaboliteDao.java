package pt.uminho.sysbio.biosynthframework.io;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public abstract class AbstractMetaboliteDao<M extends Metabolite> 
implements MetaboliteDao<M> {

  protected final String version;
  
  public AbstractMetaboliteDao(String version) {
    this.version = version;
  }
  
  public String getVersion() {
    return version;
  }
}
