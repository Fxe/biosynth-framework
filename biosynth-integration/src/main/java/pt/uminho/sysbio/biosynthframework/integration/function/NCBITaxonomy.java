package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezTaxon;

public class NCBITaxonomy implements Function<EntrezTaxon, Map<String, Object>> {

//  private final GraphDatabaseService databaseService;
  
  public Function<EntrezTaxon, Boolean> validator = null;
  public Function<Map<String, Object>, Boolean> propertiesValidador = null;
  
  public Map<String, String> mapping = new HashMap<> ();
  public Map<String, Function<Object, Object>> transform = new HashMap<> ();
  
//  public NCBITaxonomy(GraphDatabaseService databaseService) {
//    this.databaseService = databaseService;
//  }
  
  public Map<String, Object> getProperties(EntrezTaxon taxon) {
    Map<String, Object> properties = new HashMap<> ();
    properties.put("CreateDate", taxon.CreateDate);
    properties.put("Division", taxon.Division);
    //skip lineage -> can be assembled later with scientific_name
//    properties.put("Lineage", taxon.Lineage);
    properties.put("ParentTaxId", taxon.ParentTaxId);
    properties.put("PubDate", taxon.PubDate);
    properties.put("rank", taxon.rank);
    properties.put("ScientificName", taxon.ScientificName);
    properties.put("TaxId", taxon.TaxId);
    properties.put("UpdateDate", taxon.UpdateDate);
    return properties;
  }
  
  public Map<String, Object> mapProperties(Map<String, Object> properties) {
    Map<String, Object> result = new HashMap<> ();
    for (String k : properties.keySet()) {
      if (mapping.containsKey(k)) {
        String k_ = mapping.get(k);
        Object v_ = properties.get(k);
        if (transform.containsKey(k)) {
          v_ = transform.get(k).apply(v_);
        }
        
        if (v_ != null) {
          result.put(k_, v_);
        }
        
      } else {
        throw new RuntimeException("! " + k);
      }
    }
    
    String entry = String.format("txid%d", properties.get("TaxId"));
    result.put(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);
    result.put(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, GlobalLabel.EntrezTaxonomy.toString());
    result.put(Neo4jDefinitions.PROXY_PROPERTY, false);
    return result;
  }
  
  @Override
  public Map<String, Object> apply(EntrezTaxon taxon) {
    
    if (validator != null && validator.apply(taxon) == false) {
      return null;
    }
    
//    long id = taxon.TaxId;
//    Node node = null;
//    try {
//      node = databaseService.getNodeById(id);
//    } catch (NotFoundException e) {
//      
//    }
    
    Map<String, Object> properties = getProperties(taxon);
    Map<String, Object> mproperties = mapProperties(properties);
//    Node node = databaseService.createNode(GlobalLabel.EntrezTaxonomy);
//    Neo4jUtils.setPropertiesMap(mproperties, node);
//    Neo4jUtils.setUpdatedTimestamp(node);
//    Neo4jUtils.setCreatedTimestamp(node);
    
    if (propertiesValidador != null && propertiesValidador.apply(mproperties) == false) {
      return null;
    }
//    if (!nodeValidador.apply(t)) {
//      
//    }
    
//    Neo4jUtils.setUpdatedTimestamp(mproperties);
    return mproperties;
  }

}
