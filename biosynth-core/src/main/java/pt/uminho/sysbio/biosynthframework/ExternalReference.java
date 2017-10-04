package pt.uminho.sysbio.biosynthframework;

public class ExternalReference {
  public final String source;
  public final String entry;
  
  public ExternalReference(String e, String s) {
    this.source = s;
    this.entry = e;
  }
  
  public ExternalReference(String sref) {
    if (sref.contains("@")) {
      this.source = sref.split("@")[1];
      this.entry = sref.split("@")[0];
    } else {
      throw new IllegalArgumentException("invalid reference string: missing separator @");
    }
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof ExternalReference) {
      ExternalReference other = (ExternalReference) o;
      return this.source.equals(other.source) && 
             this.entry.equals(other.entry);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return source.hashCode() + 7 * entry.hashCode();
  }
  
  @Override
  public String toString() {
    return String.format("<%s, %s>", entry, source);
  }
}
