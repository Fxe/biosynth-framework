package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

public class ExtendedMetabolicModelEntity extends AbstractBiosynthEntity implements PropertyContainer {

  private static final long serialVersionUID = 1L;

  @MetaProperty
  @Column(name="md5", length=32, nullable=false)
  private String md5;

  public String getMd5() { return md5;}
  public void setMd5(String md5) { this.md5 = md5;}


  private List<OptfluxContainerReactionEntity> reactions = new ArrayList<> ();
  public List<OptfluxContainerReactionEntity> getReactions() { return reactions;}
  public void setReactions(List<OptfluxContainerReactionEntity> reactions) { this.reactions = reactions;}

  private List<ExtendedModelMetabolite> metabolites = new ArrayList<> ();
  public List<ExtendedModelMetabolite> getMetabolites() { return metabolites;}
  public void setMetabolites(List<ExtendedModelMetabolite> metabolites) { this.metabolites = metabolites;}

  private List<ExtendedMetaboliteSpecie> species = new ArrayList<> ();
  public List<ExtendedMetaboliteSpecie> getSpecies() { return species; }
  public void setSpecies(List<ExtendedMetaboliteSpecie> species) { this.species = species;}

  private List<DefaultSubcellularCompartmentEntity> subcellularCompartments = new ArrayList<> ();
  public List<DefaultSubcellularCompartmentEntity> getSubcellularCompartments() { return subcellularCompartments;}
  public void setSubcellularCompartments(
      List<DefaultSubcellularCompartmentEntity> subcellularCompartments) {
    this.subcellularCompartments = subcellularCompartments;
  }

  private Map<Long, String> subsystems = new HashMap<> ();
  public Map<Long, String> getSubsystems() { return subsystems;}
  public void setSubsystems(Map<Long, String> subsystems) { this.subsystems = subsystems;}
  
  @Override
  public String toString() {
    return String.format("MetabolicModel[%d:%s]", id, entry);
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
      this.entry = (String) properties.get("entry");
    }
    if (properties.containsKey("source")) {
      this.source = (String) properties.get("source");
    }
    if (properties.containsKey("description")) {
      this.description = (String) properties.get("description");
    }
    if (properties.containsKey("md5")) {
      this.md5 = (String) properties.get("md5");
    }
  }
}
