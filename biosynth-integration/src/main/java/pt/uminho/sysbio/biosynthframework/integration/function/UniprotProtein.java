package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.biodb.uniprot.UniprotEntry;

public class UniprotProtein implements Function<UniprotEntry, Map<String, Object>> {

  public Function<UniprotEntry, Boolean> validator = null;
  public Function<Map<String, Object>, Boolean> propertiesValidador = null;
  
  public Map<String, String> mapping = new HashMap<> ();
  public Map<String, Function<Object, Object>> transform = new HashMap<> ();
  
  public Map<String, Object> getProperties(UniprotEntry entry) {
    Map<String, Object> properties = new HashMap<> ();
    properties.put("name", entry.name);
    properties.put("created", entry.created);
    properties.put("modified", entry.modified);
    properties.put("dataset", entry.dataset);
    properties.put("version", entry.version);
//    properties.put("ParentTaxId", taxon.ParentTaxId);
//    properties.put("PubDate", taxon.PubDate);
//    properties.put("rank", taxon.rank);
//    properties.put("ScientificName", taxon.ScientificName);
//    properties.put("TaxId", taxon.TaxId);
//    properties.put("UpdateDate", taxon.UpdateDate);
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
    
    String entry = properties.get("name").toString().trim();
    result.put(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry);
    result.put(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, GlobalLabel.UniProt.toString());
    result.put(Neo4jDefinitions.PROXY_PROPERTY, false);
    return result;
  }
  
  @Override
  public Map<String, Object> apply(UniprotEntry entry) {
    if (validator != null && validator.apply(entry) == false) {
      return null;
    }
    
    Map<String, Object> properties = getProperties(entry);
    Map<String, Object> mproperties = mapProperties(properties);

    if (propertiesValidador != null && propertiesValidador.apply(mproperties) == false) {
      return null;
    }

    return mproperties;
  }

}
