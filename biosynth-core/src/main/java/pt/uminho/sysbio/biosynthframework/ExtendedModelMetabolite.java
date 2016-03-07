package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendedModelMetabolite extends GenericMetabolite implements PropertyContainer {

  private static final long serialVersionUID = 1L;



  private ExtendedMetabolicModelEntity metabolicModel;
  public ExtendedMetabolicModelEntity getMetabolicModel() { return metabolicModel;}
  public void setMetabolicModel(
      ExtendedMetabolicModelEntity metabolicModel) {
    this.metabolicModel = metabolicModel;
  }

  private Map<Long, ExtendedMetaboliteSpecie> species = new HashMap<> ();
  public Map<Long, ExtendedMetaboliteSpecie> getSpecies() { return species;}
  public void setSpecies(Map<Long, ExtendedMetaboliteSpecie> species) {
    this.species = species;
  }

  private Map<String, Object> properties = new HashMap<> ();

  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }
  @Override
  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
    if (properties.containsKey("entry")) {
      this.entry = ((String) properties.get("entry")).split("@")[0];
    }
    if (properties.containsKey("name")) {
      this.name = (String) properties.get("name");
    }
    if (properties.containsKey("formula")) {
      this.formula = (String) properties.get("formula");
    }
    if (properties.containsKey("source")) {
      this.source = (String) properties.get("source");
    }
    if (properties.containsKey("description")) {
      this.description = (String) properties.get("description");
    }
  }


  private List<DefaultMetaboliteSpecieReference> crossreferences = new ArrayList<> ();
  public List<DefaultMetaboliteSpecieReference> getCrossreferences() {
    return crossreferences;
  }
  public void setCrossreferences(
      List<DefaultMetaboliteSpecieReference> crossreferences) {
    this.crossreferences = crossreferences;
  }
  
  public int getReactionDegree() {
    int degree = 0;
    for (ExtendedMetaboliteSpecie spi : this.species.values()) {
      if (spi != null) {
        degree += spi.getReactionDegree();
      }
    }
    
    return degree;
  }
}
