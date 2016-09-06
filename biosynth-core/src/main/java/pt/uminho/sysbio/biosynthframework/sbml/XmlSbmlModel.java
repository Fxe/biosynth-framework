package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlSbmlModel extends XmlObject {

  private Map<String, String> sbmlAttributes = new HashMap<> ();
  private List<XmlSbmlCompartment> compartments = new ArrayList<> ();
  private List<XmlSbmlSpecie> species = new ArrayList<> ();
  private List<XmlSbmlReaction> reactions = new ArrayList<> ();
  private List<XmlSbmlGroup> groups = new ArrayList<> ();
  private List<XmlObject> fluxBounds = new ArrayList<>();
  
  public Map<String, String> getSbmlAttributes() {
    return sbmlAttributes;
  }
  public void setSbmlAttributes(Map<String, String> sbmlAttributes) {
    this.sbmlAttributes = sbmlAttributes;
  }
  
  public List<XmlSbmlCompartment> getCompartments() { return compartments;}
  public void setCompartments(List<XmlSbmlCompartment> compartments) { this.compartments = compartments;}
  
  public List<XmlSbmlSpecie> getSpecies() { return species;}
  public void setSpecies(List<XmlSbmlSpecie> species) { this.species = species;}

  public List<XmlSbmlReaction> getReactions() { return reactions;}
  public void setReactions(List<XmlSbmlReaction> reactions) { this.reactions = reactions;}
  
  public List<XmlSbmlGroup> getGroups() { return groups;}
  public void setGroups(List<XmlSbmlGroup> groups) { this.groups = groups;}
  
  public List<XmlObject> getFluxBounds() { return fluxBounds;}
  public void setFluxBounds(List<XmlObject> fluxBounds) { this.fluxBounds = fluxBounds;}

}