package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynthframework.ModelAdapter;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;

public class DefaultMetaboliteModelIntegrationService implements MetaboliteModelIntegrationService {
  
  public Map<String, SubcellularCompartment> generateCompartments(ModelAdapter model) {
    CompartmentIntegration cintegration = 
        new CompartmentIntegration();
    cintegration.singleCompartmentAsCytosol = true;
    cintegration.createBoundaryCompartment = false;
//    cintegration.detectors.add(new CompartmentDetectorKBase());
    cintegration.detectors.add(new CompartmentDetectorFlatString(false));
    cintegration.detectors.add(new CompartmentDetectorFlatString(true));
    Map<String, SubcellularCompartment> cmap = cintegration.generateCompartmentMapping(model);
    
    return cmap;
  }
  
  public void wut() {
    BiodbService biodbService = null;
    SpecieIntegrationFacade spiIntegration = new SpecieIntegrationFacade();
    ReactionIntegrationFacade rxnIntegration = new ReactionIntegrationFacade(biodbService);
  }
}
