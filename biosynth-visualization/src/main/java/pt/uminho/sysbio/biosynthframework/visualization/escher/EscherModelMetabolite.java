package pt.uminho.sysbio.biosynthframework.visualization.escher;

import java.util.HashMap;
import java.util.Map;

public class EscherModelMetabolite {
  public String name;
  public String formula;
  public String compartment;
  public String id;
  public int charge;
  public Map<String, Object> notes = new HashMap<>();
  
  public EscherModelMetabolite() { }
  
  public EscherModelMetabolite(String id, String name, String formula, 
                               String compartment, int charge) { 
    this.id = id;
    this.name = name;
    this.formula = formula;
    this.compartment = compartment;
    this.charge = charge;
  }
  
  @Override
  public String toString() {
    return String.format("[EscherModelMetabolite]%s", id);
  }
}
