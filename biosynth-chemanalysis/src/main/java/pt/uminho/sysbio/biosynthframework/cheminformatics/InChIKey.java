package pt.uminho.sysbio.biosynthframework.cheminformatics;

public class InChIKey {
  public String fikhb;
  public String sikhb;
  public Character p;
  
  public InChIKey(String inchikey) {
    String[] parts = inchikey.split("-");
    fikhb = parts[0];
    sikhb = parts[1];
    p = parts[2].charAt(0);
  }
  
  @Override
  public String toString() {
    return String.format("%s-%s-%c", fikhb, sikhb, p);
  }
}
