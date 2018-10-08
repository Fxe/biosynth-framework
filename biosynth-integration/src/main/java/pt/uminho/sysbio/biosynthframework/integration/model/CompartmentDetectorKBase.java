package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class CompartmentDetectorKBase implements CompartmentDetector {
  
  private final Map<String, SubcellularCompartment> mapping = new HashMap<>();
  
  public CompartmentDetectorKBase() {
    mapping.put("b", SubcellularCompartment.BOUNDARY);
    mapping.put("e", SubcellularCompartment.EXTRACELLULAR);
    mapping.put("c", SubcellularCompartment.CYTOSOL);
    mapping.put("p", SubcellularCompartment.PERIPLASM);
    mapping.put("m", SubcellularCompartment.MITOCHONDRIA);
    mapping.put("n", SubcellularCompartment.NUCLEUS);
    mapping.put("v", SubcellularCompartment.VACUOLE);
    mapping.put("g", SubcellularCompartment.GOLGI);
    mapping.put("r", SubcellularCompartment.RETICULUM);
    mapping.put("x", SubcellularCompartment.PEROXISOME);
    mapping.put("a", SubcellularCompartment.CARBOXYSOME);
    mapping.put("l", SubcellularCompartment.LYSOSOME);
    mapping.put("l", SubcellularCompartment.LYSOSOME);
    mapping.put("j", SubcellularCompartment.MITOCHONDRIA_MEMBRANE);
    mapping.put("w", SubcellularCompartment.CELL_WALL);
    mapping.put("d", SubcellularCompartment.PLASTID);
    mapping.put("k", SubcellularCompartment.THYLAKOID_LUMEN);
    
    
    mapping.put("cytosol", SubcellularCompartment.CYTOSOL);
    
    
    mapping.put("extra_organism", SubcellularCompartment.EXTRACELLULAR);
    mapping.put("extraorganism", SubcellularCompartment.EXTRACELLULAR);
    mapping.put("extra_cellular", SubcellularCompartment.EXTRACELLULAR);
    mapping.put("extracellular", SubcellularCompartment.EXTRACELLULAR);
    mapping.put("extc", SubcellularCompartment.EXTRACELLULAR);
    
    
    mapping.put("periplasm", SubcellularCompartment.PERIPLASM);
    mapping.put("periplasmic space", SubcellularCompartment.PERIPLASM);
    
    
    mapping.put("boundary", SubcellularCompartment.BOUNDARY);
    
    mapping.put("carboxysome", SubcellularCompartment.CARBOXYSOME);
    mapping.put("carboxyzome", SubcellularCompartment.CARBOXYSOME);
    mapping.put("thylakoid lumen", SubcellularCompartment.THYLAKOID_LUMEN);
  }
  
  @Override
  public SubcellularCompartment predict(String id, String name) {
    int n = StringUtils.indexOfAny(id, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    if (n > 0) {
      id = id.substring(0, n);
      if (!id.trim().isEmpty()) {
        return mapping.get(id);
      }
    }
    return null;
  }
}
