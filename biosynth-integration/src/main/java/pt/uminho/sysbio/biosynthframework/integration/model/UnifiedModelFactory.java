package pt.uminho.sysbio.biosynthframework.integration.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.integration.model.UnifiedModel.ModelMetadata;
import pt.uminho.sysbio.biosynthframework.sbml.XmlSbmlModel;
import pt.uminho.sysbio.biosynthframework.sbml.XmlStreamSbmlReader;

public class UnifiedModelFactory {
  
  private static final Logger logger = LoggerFactory.getLogger(UnifiedModelFactory.class);
  
  protected Map<String, UnifiedModelBuilder> builderMap = new HashMap<>();
//  protected Map<String, XmlSbmlModel> modelMap = new HashMap<>();
//  protected Map<String, FBAModel> kmodelMap = new HashMap<>();
  protected Map<String, ModelMetadata> metaMap = new HashMap<>();
  protected Map<String, String> stoichSwap = new HashMap<>();
  
  public UnifiedModelFactory withStoichSwap(Map<String, String> stoichSwap) {
    this.stoichSwap.putAll(stoichSwap);
    return this;
  }
  
  public UnifiedModelFactory withModel(String modelEntry, UnifiedModelBuilder builder) {
    builderMap.put(modelEntry, builder);
    return this;
  }
  
  public UnifiedModelFactory withModelMetadata(String modelEntry, String path) {
    logger.info("loading metadata {}", path);
    
//    try {
//      String json = BIOUtils.readFileAsString(path);
//      ModelMetadata meta = KBaseIOUtils.getObject(json, ModelMetadata.class);
//      if (meta != null) {
//        metaMap.put(modelEntry, meta);
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    
    return this;
  }
  
//  public UnifiedModelFactory withKBaseModel(String modelEntry, FBAModel kmodel) {
//    this.builderMap.put(modelEntry, new KbaseUnifiedModelBuilder(kmodel, modelEntry));
//    return this;
//  }
  
//  public UnifiedModelFactory withModel(String modelEntry, String ngam, String biomass, String path) {
//    XmlStreamSbmlReader reader;
//    
//    try {
//      reader = new XmlStreamSbmlReader(path);
//      XmlSbmlModel xmodel = reader.parse();
//      if (xmodel != null) {
//        modelMap.put(modelEntry, xmodel);
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    
//    return this;
//  }
  
  protected<K, V> Map<K, V> swap(Map<K, V> s, Map<K, K> swap) {
    Map<K, V> result = new HashMap<>();
    for (K key : s.keySet()) {
      V value = s.get(key);
      if (swap.containsKey(key)) {
        key = swap.get(key);
      }
      result.put(key, value);
    }
    return result;
  }
  
  public UnifiedModel build() {
    UnifiedModel umodel = new UnifiedModel();
    
//    for (String modelEntry : modelMap.keySet()) {
//      XmlSbmlModel xmodel = modelMap.get(modelEntry);
//      
//      UnifiedModelBuilder builder = 
//          new XmlUnifiedModelBuilder(modelEntry, xmodel, umodel);
//      builder.setupCompartments();
//      builder.setupSpecies();
//      builder.setupReactions();
//      ModelMetadata metadata = this.metaMap.get(modelEntry);
//      if (metadata != null) {
//        for (String k : metadata.metabolites.keySet()) {
//          MetaboliteMetadata cpdData = metadata.metabolites.get(k);
//          umodel.registerMetaboliteSet(modelEntry, cpdData.species);
//          umodel.registerSpecieAnnoation(modelEntry, 
//              cpdData.species.iterator().next(), cpdData.databaseReferences);
//        }
//      }
//    }
    
    for (String modelEntry : builderMap.keySet()) {
      UnifiedModelBuilder builder = builderMap.get(modelEntry);
      builder.setupCompartments(umodel);
      builder.setupSpecies(umodel);
      builder.setupReactions(umodel);
    }
    
//    for (String modelEntry : kmodelMap.keySet()) {
////      ReferencePropagation propagation = new ReferencePropagation();
//      
//      FBAModel kmodel = kmodelMap.get(modelEntry);
//      UnifiedModelBuilder builder = 
//          new KbaseUnifiedModelBuilder(modelEntry, kmodel, umodel);
//      builder.setupCompartments();
//      builder.setupSpecies();
//      builder.setupReactions();
//      
////      ConnectedComponents<String> curation = FileImport.loadConnectedComponents("/var/biobase/integration/cc/cpd_curation_bigg.tsv");
////      IntegrationMap<String, MetaboliteMajorLabel> imap = propagation.propagate(true, curation);
////      DataUtils.printData(imap, "species");
//    }
    
//    umodel.idToRxnIds = new BHashMap<>(degreeMap);
    
    return umodel;
  }
}
