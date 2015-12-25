package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetabolicModelSubsystem extends AbstractBiosynthEntity 
implements MetabolicPathway {

  private static final long serialVersionUID = 1L;

  private Map<Long, String> reactions = new HashMap<> ();
  private Map<Long, String> species = new HashMap<> ();
  private Map<Long, String> metabolites = new HashMap<> ();
  private Map<Long, String> compartments = new HashMap<> ();
  
  public Map<Long, String> getSpecies() { return species;}
  public void setSpecies(Map<Long, String> species) { this.species = species;}

  public Map<Long, String> getMetabolites() { return metabolites;}
  public void setMetabolites(Map<Long, String> metabolites) { this.metabolites = metabolites;}
  
  public Map<Long, String> getCompartments() { return compartments;}
  public void setCompartments(Map<Long, String> compartments) { this.compartments = compartments;}

  public void setReactions(Map<Long, String> reactions) { this.reactions = reactions;}

  @Override
  public Set<String> getReactions() {
    return new HashSet<> (reactions.values());
  }
}
