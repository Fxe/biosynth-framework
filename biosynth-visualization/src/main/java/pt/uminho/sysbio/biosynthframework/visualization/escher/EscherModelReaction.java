package pt.uminho.sysbio.biosynthframework.visualization.escher;

import java.util.HashMap;
import java.util.Map;

public class EscherModelReaction {

  public String id;
  public String gene_reaction_rule = "";
  public String subsystem = "";
  public String name = "";
  public Double upper_bound;
  public Double lower_bound;
  public Map<String, Double> metabolites = new HashMap<> ();
  public Map<String, Object> notes = new HashMap<>();
  public Double objective_coefficient;
  
  @Override
  public String toString() {
    return String.format("[EscherModelReaction]%s", id);
  }
}
