package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

public class SimpleModelReaction {
  public String id;
  public String name;
  
  public Map<String, Double> stoichiometry = new HashMap<> ();
  
  public double lb;
  public double ub;
}
