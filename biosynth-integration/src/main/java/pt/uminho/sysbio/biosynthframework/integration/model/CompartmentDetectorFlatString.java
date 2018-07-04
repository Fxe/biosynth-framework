package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class CompartmentDetectorFlatString implements CompartmentDetector {
  
  private boolean useName = false;
  
  private Map<String, SubcellularCompartment> flatMapping = new HashMap<>();
  
  public CompartmentDetectorFlatString() {
    this(false);
  }
  
  public CompartmentDetectorFlatString(boolean useName) {
    this.useName = useName;
    flatMapping.put("c", SubcellularCompartment.CYTOSOL);
    flatMapping.put("cytosol", SubcellularCompartment.CYTOSOL);
    
    flatMapping.put("e", SubcellularCompartment.EXTRACELLULAR);
    flatMapping.put("extra_organism", SubcellularCompartment.EXTRACELLULAR);
    flatMapping.put("extraorganism", SubcellularCompartment.EXTRACELLULAR);
    flatMapping.put("extra_cellular", SubcellularCompartment.EXTRACELLULAR);
    flatMapping.put("extracellular", SubcellularCompartment.EXTRACELLULAR);
    flatMapping.put("extc", SubcellularCompartment.EXTRACELLULAR);
    
    flatMapping.put("p", SubcellularCompartment.PERIPLASM);
    flatMapping.put("periplasm", SubcellularCompartment.PERIPLASM);
    flatMapping.put("periplasmic space", SubcellularCompartment.PERIPLASM);
    
    flatMapping.put("b", SubcellularCompartment.BOUNDARY);
    flatMapping.put("boundary", SubcellularCompartment.BOUNDARY);
    
    flatMapping.put("carboxysome", SubcellularCompartment.CARBOXYSOME);
    flatMapping.put("carboxyzome", SubcellularCompartment.CARBOXYSOME);
    flatMapping.put("thylakoid lumen", SubcellularCompartment.THYLAKOID_LUMEN);
    
    flatMapping.put("n", SubcellularCompartment.NUCLEUS);
    
    flatMapping.put("m", SubcellularCompartment.MITOCHONDRIA);
    
    flatMapping.put("x", SubcellularCompartment.PEROXISOME);
    
    flatMapping.put("v", SubcellularCompartment.VACUOLE);
    
    flatMapping.put("g", SubcellularCompartment.GOLGI);
    
    flatMapping.put("r", SubcellularCompartment.RETICULUM);
  }
  
  @Override
  public SubcellularCompartment predict(String id, String name) {
    SubcellularCompartment scmp = null;
    if (useName) {
      if (id != null) {
        scmp = flatMapping.get(id.toLowerCase());        
      }
    } else {
      if (name != null) {
        scmp = flatMapping.get(name.toLowerCase());        
      }
    }
    
    return scmp;
  }
}
