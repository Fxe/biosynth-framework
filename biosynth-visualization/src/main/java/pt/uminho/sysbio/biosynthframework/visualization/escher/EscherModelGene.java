package pt.uminho.sysbio.biosynthframework.visualization.escher;

import java.util.HashMap;
import java.util.Map;

public class EscherModelGene {
  public String id;
  public String name;
  public Map<String, Object> notes = new HashMap<>();
  
  @Override
  public String toString() {
    return String.format("[EscherModelGene]%s", id);
  }
}
