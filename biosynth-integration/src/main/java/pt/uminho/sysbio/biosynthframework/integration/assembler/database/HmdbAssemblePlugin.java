package pt.uminho.sysbio.biosynthframework.integration.assembler.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class HmdbAssemblePlugin extends AbstractNeo4jAssemblePlugin {
  
  public HmdbAssemblePlugin(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }
  
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    //ontology_applications
    //ontology_origins
    //ontology_biofuncions
    //ontology_cellular_location
    //biofluids
    //tissues
    Map<String, Object> result = new HashMap<>();
    
    Set<BiodbMetaboliteNode> cpds = filter(refs, MetaboliteMajorLabel.HMDB.toString());
    
    for (BiodbMetaboliteNode cpdNode : cpds) {
      String applications = (String) cpdNode.getProperty("ontology_applications", null);
      String origins = (String) cpdNode.getProperty("ontology_origins", null);
      String biofuncions = (String) cpdNode.getProperty("ontology_biofuncions", null);
      String cellularLocation = (String) cpdNode.getProperty("ontology_cellular_location", null);
      String biofluids = (String) cpdNode.getProperty("biofluids", null);
      String tissues = (String) cpdNode.getProperty("tissues", null);
//      System.out.println(cpdNode.getAllProperties().keySet());
      
      Set<String> applicationsSet     = setupStringArray(applications, ";");
      Set<String> originsSet          = setupStringArray(origins, ";");
      Set<String> biofuncionsSet      = setupStringArray(biofuncions, ";");
      Set<String> cellularLocationSet = setupStringArray(cellularLocation, ";");
      Set<String> biofluidsSet        = setupStringArray(biofluids, ";");
      Set<String> tissuesSet          = setupStringArray(tissues, ";");
      
      if (applicationsSet != null) {
        result.put("applications", applicationsSet);
      }
      if (originsSet != null) {
        result.put("origins", originsSet);
      }
      if (biofuncionsSet != null) {
        result.put("biofuncions", biofuncionsSet);
      }
      if (cellularLocationSet != null) {
        result.put("cellularLocation", cellularLocationSet);
      }
      if (biofluidsSet != null) {
        result.put("biofluids", biofluidsSet);
      }
      if (tissuesSet != null) {
        result.put("tissues", tissuesSet);
      }
    }
    
    return result;
  }
}

