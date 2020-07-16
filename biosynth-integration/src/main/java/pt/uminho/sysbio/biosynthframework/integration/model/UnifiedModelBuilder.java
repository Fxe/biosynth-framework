package pt.uminho.sysbio.biosynthframework.integration.model;

public interface UnifiedModelBuilder {
  @Deprecated
  public void setupCompartments();
  @Deprecated
  public void setupSpecies();
  @Deprecated
  public void setupReactions();
  
  public void setupCompartments(UnifiedModel umodel);
  public void setupSpecies(UnifiedModel umodel);
  public void setupReactions(UnifiedModel umodel);
}
