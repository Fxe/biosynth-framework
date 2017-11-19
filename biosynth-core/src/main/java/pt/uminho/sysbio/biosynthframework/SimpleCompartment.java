package pt.uminho.sysbio.biosynthframework;

import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class SimpleCompartment<I> {
  public SubcellularCompartment scmp;
  public I id;
  public String name;
  
  public SimpleCompartment(I id, String name, SubcellularCompartment scmp) {
    if (DataUtils.empty(id)) {
      throw new IllegalArgumentException("empty");
    }
    
    this.id = id;
    this.scmp = scmp;
    this.name = name;
  }
  
  public SimpleCompartment(I id) {
    this(id, null, SubcellularCompartment.UNKNOWN);
  }
}
