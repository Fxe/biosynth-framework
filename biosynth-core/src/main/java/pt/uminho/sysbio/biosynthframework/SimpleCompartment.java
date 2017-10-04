package pt.uminho.sysbio.biosynthframework;

public class SimpleCompartment<I> {
  public SubcellularCompartment scmp;
  public I id;
  public String name;
  
  public SimpleCompartment(I id, String name, SubcellularCompartment scmp) {
    this.id = id;
    this.scmp = scmp;
    this.name = name;
  }
  
  public SimpleCompartment(I id) {
    this(id, null, SubcellularCompartment.UNKNOWN);
  }
}
