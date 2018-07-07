package pt.uminho.sysbio.biosynthframework.integration.assembler.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class ModelseedMetaboliteAssemblePlugin extends AbstractNeo4jAssemblePlugin {

  public ModelseedMetaboliteAssemblePlugin(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }

  @Override
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    Map<String, Object> result = new HashMap<>();
    
    Set<BiodbMetaboliteNode> cpds = filter(refs, MetaboliteMajorLabel.ModelSeed.toString());
    for (BiodbMetaboliteNode cpdNode : cpds) {
      Object cofactor = cpdNode.getProperty("cofactor", null);
      Object deltaG = cpdNode.getProperty("deltaG", null);
      Object deltaGErr = cpdNode.getProperty("deltaGErr", null);
      Object core = cpdNode.getProperty("core", null);
      
      result.put("cofactor", cofactor);
      result.put("deltag", deltaG);
      result.put("deltagerr", deltaGErr);
      result.put("core", core);
    }
    
    return result;
  }
  
  public void collect() {
    /*
     * major_label
     * cofactor
     * obsolete
     * description
     * created_at
     * deltaG
     * deltaGErr
     * abbreviation
     * structure
     * metaboliteClass
     * core
     * entry
     * proxy
     * updated_at
     * name, defaultCharge, formula]
     */
  }
}
