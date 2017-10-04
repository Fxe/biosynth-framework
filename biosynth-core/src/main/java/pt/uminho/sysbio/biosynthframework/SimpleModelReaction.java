package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

public class SimpleModelReaction<I> {
  public I id;
  public String name;
  
  public Map<I, Double> stoichiometry = new HashMap<> ();
  
  public double lb;
  public double ub;
  
  public SimpleModelReaction(I id, double lb, double ub) {
    // TODO Auto-generated constructor stub
  }
}
