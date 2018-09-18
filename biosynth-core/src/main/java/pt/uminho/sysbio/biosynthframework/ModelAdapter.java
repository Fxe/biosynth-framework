package pt.uminho.sysbio.biosynthframework;

import java.util.Set;

public interface ModelAdapter {
  public String getGpr(String mrxnEntry);

  public boolean isTranslocation(String mrxnEntry);

  public int getReactionSize(String mrxnEntry);

  public String getSpecieCompartment(String spiEntry);

  public Integer getSpecieDegree(String spiEntry);

  public boolean isDrain(String mrxnEntry);

  public boolean isBoundarySpecie(String spiEntry);

  public Range getBounds(String mrxnEntry);
  
  public SimpleModelReaction<String> getReaction(String rxnId);
  
  public SimpleModelSpecie<String> getSpecies(String spiId);
  
  public SimpleCompartment<String> getCompartment(String cmpId);
  
  public Set<String> getReactionIds();
  public Set<String> getSpeciesIds();
  public Set<String> getCompartmentIds();
  
  public Set<String> getReactionGeneIds(String rxnId);
  
  public CompartmentalizedStoichiometry<String, String> getCompartmentalizedStoichiometry(String mrxnEntry);
}
