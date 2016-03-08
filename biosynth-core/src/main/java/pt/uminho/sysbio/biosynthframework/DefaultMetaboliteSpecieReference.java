package pt.uminho.sysbio.biosynthframework;

public class DefaultMetaboliteSpecieReference extends GenericCrossreference {

  private static final long serialVersionUID = 1L;

  private ReferenceSource source;

  public ReferenceSource getSource() { return source;}
  public void setSource(ReferenceSource source) { this.source = source;}
}
