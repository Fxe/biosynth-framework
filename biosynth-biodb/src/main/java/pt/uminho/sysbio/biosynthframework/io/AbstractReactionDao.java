package pt.uminho.sysbio.biosynthframework.io;

import pt.uminho.sysbio.biosynthframework.Reaction;

public abstract class AbstractReactionDao<R extends Reaction> 
implements ReactionDao<R>{

  protected final String version;
  
  public AbstractReactionDao(String version) {
    this.version = version;
  }
  
  public String getVersion() {
    return version;
  }
}
