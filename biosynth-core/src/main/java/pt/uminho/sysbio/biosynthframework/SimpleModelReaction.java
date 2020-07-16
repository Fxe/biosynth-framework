package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class SimpleModelReaction<I> {
  public I id;
  public String name;
  
  public Map<I, Double> stoichiometry = new HashMap<> ();
  public String gpr;
  public Range bounds;
  
  public Map<String, Object> attributes = new HashMap<>();
  
  public SimpleModelReaction(I id, double lb, double ub) {
    if (DataUtils.empty(id)) {
      throw new IllegalArgumentException("empty");
    }
    
    this.id = id;
    this.bounds = new Range(lb, ub);
  }
  
  @Override
  public String toString() {
    return String.format("%s", id);
  }
}
