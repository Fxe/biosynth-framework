package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class SimpleModelSpecie<I> {
  public I id;
  public String name = "";
  public I compartmentId;
  
  public Map<String, Object> attributes = new HashMap<>();
  
  public SimpleModelSpecie(I id, String name, I compartmentId) {
    if (DataUtils.empty(id) || DataUtils.empty(compartmentId)) {
      throw new IllegalArgumentException("empty");
    }
    if (DataUtils.empty(name)) {
      name = "";
    }
    this.id = id;
    this.name = name;
    this.compartmentId = compartmentId;
  }
  
  @Override
  public String toString() {
    return String.format("%s[%s]", id, compartmentId);
  }
}
