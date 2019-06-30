package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;

public class KBaseIntegrationEngine extends SimpleStringMatchEngine implements BaseIntegrationEngine {
  
  public Map<String, String> remap = new HashMap<>();
  public Map<String, Boolean> obsolete = new HashMap<>();
  
  public KBaseIntegrationEngine() {
    super("cpd", "M_");
  }
  
  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate() {
    
    IntegrationMap<String, MetaboliteMajorLabel> result = new IntegrationMap<>();
    Map<String, String> match = this.match();
    for (String spiId : match.keySet()) {
      String msCpdEntry = match.get(spiId);
      if (remap.containsKey(msCpdEntry)) {
        msCpdEntry = remap.get(msCpdEntry);
      }
      result.addIntegration(spiId, MetaboliteMajorLabel.ModelSeed, msCpdEntry);
    }
    
    return result;
  }

}
