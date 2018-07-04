package pt.uminho.sysbio.biosynthframework.integration.model;

import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public interface CompartmentDetector {
  public SubcellularCompartment predict(String id, String name);
}
