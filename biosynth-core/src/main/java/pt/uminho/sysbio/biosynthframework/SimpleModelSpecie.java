package pt.uminho.sysbio.biosynthframework;

import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class SimpleModelSpecie<I> {
  public I id;
  public String name = "";
  public I compartmentId;
  
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
}
