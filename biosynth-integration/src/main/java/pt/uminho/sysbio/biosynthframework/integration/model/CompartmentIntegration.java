package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.ModelAdapter;
import pt.uminho.sysbio.biosynthframework.SimpleModelSpecie;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlCompartment;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlSpecie;

public class CompartmentIntegration {

  private static final Logger logger = LoggerFactory.getLogger(CompartmentIntegration.class);

  public boolean createBoundaryCompartment = true;
  public boolean singleCompartmentAsCytosol = true;
  public boolean forceCytosol = true;
  public List<CompartmentDetector> detectors = new ArrayList<>();

  public void generateBoundaryCompartment() {
    
  }
  
  public void generateExtracellularCompartment() {
    
  }
  
  public Map<String, SubcellularCompartment> generateCompartmentMapping(ModelAdapter model) {
    Map<String, String> cmpMap = new HashMap<>();
    Map<String, Set<String>> cmpIdToSpecies = new HashMap<>();
    for (String cmpId : model.getCompartmentIds()) {
      cmpMap.put(cmpId, model.getCompartment(cmpId).name);
    }
    
    for (String spiId : model.getSpeciesIds()) {
      String cmpId = model.getSpecies(spiId).compartmentId;
      if (cmpIdToSpecies.containsKey(cmpId)) {
        cmpIdToSpecies.get(cmpId).add(cmpId);
      }      
//      String bcStr = xspi.getAttributes().get("boundaryCondition");
//      if (bcStr != null && Boolean.parseBoolean(bcStr.toLowerCase())) {
//        bcSpecies.add(xspi);
//      }
    }
    
    Set<SubcellularCompartment> mapped = new HashSet<>();
    Map<String, SubcellularCompartment> result = new HashMap<>();
    if (singleCompartmentAsCytosol && cmpMap.size() == 1) {
      mapped.add(SubcellularCompartment.CYTOSOL);
      result.put(cmpMap.keySet().iterator().next(), SubcellularCompartment.CYTOSOL);
    } else {
      for (CompartmentDetector detector : detectors) {
        if (result.size() != cmpMap.size()) {
          for (String cmpId : cmpMap.keySet()) {
            if (!result.containsKey(cmpId)) {
              SubcellularCompartment scmp = detector.predict(cmpId, cmpMap.get(cmpId));
              if (scmp != null &&
                  !mapped.contains(scmp)) {
                logger.info("[{}:{}] -> [{}]", cmpId, cmpMap.get(cmpId), scmp);
                mapped.add(scmp);
                result.put(cmpId, scmp);
              }
            }
          }
        }
      }
      
      if (forceCytosol && !mapped.contains(SubcellularCompartment.CYTOSOL)) {
        String cmpId = getLargestCompartment(cmpIdToSpecies);
        if (cmpMap.containsKey(cmpId)) {
          mapped.add(SubcellularCompartment.CYTOSOL);
          result.put(cmpId, SubcellularCompartment.CYTOSOL);
        }
      }
    }

    return result;
  }
  
  public Map<String, SubcellularCompartment> generateCompartmentMapping(XmlSbmlModel model) {
    Set<XmlSbmlSpecie> bcSpecies = new HashSet<>();
    Map<String, String> aa = new HashMap<>();
    Map<String, Set<XmlSbmlSpecie>> cmpIdToSpecies = new HashMap<>();
    Map<String, SubcellularCompartment> result = new HashMap<>();
    for (XmlSbmlCompartment xcmp : model.getCompartments()) {
      String id = xcmp.getAttributes().get("id");
      String name = xcmp.getAttributes().get("name");
      cmpIdToSpecies.put(id, new HashSet<XmlSbmlSpecie>());
     aa.put(id, name);
    }
    
    logger.debug("detected {}", aa);
    
    for (XmlSbmlSpecie xspi : model.getSpecies()) {
      String cmpId = xspi.getAttributes().get("compartment");
      if (cmpIdToSpecies.containsKey(cmpId)) {
        cmpIdToSpecies.get(cmpId).add(xspi);
      }
      
      String bcStr = xspi.getAttributes().get("boundaryCondition");
      if (bcStr != null && Boolean.parseBoolean(bcStr.toLowerCase())) {
        bcSpecies.add(xspi);
      }
    }
    
    Set<SubcellularCompartment> mappedScmp = new HashSet<>();
    if (singleCompartmentAsCytosol && aa.size() == 1) {
      mappedScmp.add(SubcellularCompartment.CYTOSOL);
      result.put(aa.keySet().iterator().next(), SubcellularCompartment.CYTOSOL);
    } else {
      for (CompartmentDetector detector : detectors) {
        if (result.size() != aa.size()) {
          for (String cmpId : aa.keySet()) {
            if (!result.containsKey(cmpId)) {
              SubcellularCompartment scmp = detector.predict(cmpId, aa.get(cmpId));
              if (scmp != null &&
                  !mappedScmp.contains(scmp)) {
                logger.info("[{}:{}] -> [{}]", cmpId, aa.get(cmpId), scmp);
                mappedScmp.add(scmp);
                result.put(cmpId, scmp);
              }
            }
          }
        }
      }

      if (forceCytosol && !mappedScmp.contains(SubcellularCompartment.CYTOSOL)) {
        String cmpId = getLargestCompartment(cmpIdToSpecies);
        if (aa.containsKey(cmpId)) {
          mappedScmp.add(SubcellularCompartment.CYTOSOL);
          result.put(cmpId, SubcellularCompartment.CYTOSOL);
        }
      }
    }
    
    for (String cmpId : aa.keySet()) {
      if (!result.containsKey(cmpId)) {
        logger.warn("not mapped {} - {}", cmpId , aa.get(cmpId));
      }
    }

    
    if (createBoundaryCompartment && 
        mappedScmp.size() == aa.size() &&
        !mappedScmp.contains(SubcellularCompartment.BOUNDARY) &&
        bcSpecies.size() > 0) {
      String cmpBoundaryId = "b";
      result.put(cmpBoundaryId, SubcellularCompartment.BOUNDARY);
      XmlSbmlCompartment cmpBoundary = new XmlSbmlCompartment();
      cmpBoundary.getAttributes().put("id", cmpBoundaryId);
      cmpBoundary.getAttributes().put("name", "boundary");
      model.getCompartments().add(cmpBoundary);
      for (XmlSbmlSpecie xspi : bcSpecies) {
        logger.debug("{} -> {}", xspi, SubcellularCompartment.BOUNDARY);
        xspi.getAttributes().put("compartment", cmpBoundaryId);
      }
    }
    
    return result;
  }
  
  public<T> String getLargestCompartment(Map<String, Set<T>> a) {
    int s = 0;
    String result = null;
    for (String k : a.keySet()) {
      int size = a.get(k).size();
      if (s < size) {
        s = size;
        result = k;
      }
    }
    return result;
  }
}