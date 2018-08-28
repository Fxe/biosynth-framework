package pt.uminho.sysbio.biosynthframework.cheminformatics;

public class SMARTS {

  private final String smarts;
  
  public SMARTS(String smarts) {
    this.smarts = smarts;
  }
  
  public String getValue() {
    return smarts;
  }

  @Override
  public String toString() {
    return smarts;
  }
}
