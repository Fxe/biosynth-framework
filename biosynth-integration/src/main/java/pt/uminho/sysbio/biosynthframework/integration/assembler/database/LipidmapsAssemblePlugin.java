package pt.uminho.sysbio.biosynthframework.integration.assembler.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.ExternalReference;
import pt.uminho.sysbio.biosynthframework.integration.assembler.AbstractNeo4jAssemblePlugin;

public class LipidmapsAssemblePlugin extends AbstractNeo4jAssemblePlugin {

  public LipidmapsAssemblePlugin(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService);
  }

  @Override
  public Map<String, Object> assemble(Set<ExternalReference> refs) {
    /* 
     * mainClass Glycerophosphoserines [GP03]
     * subSlass  Diacylglycerophosphoserines [GP0301]
     * category  Glycerophospholipids [GP]
     * classLevel4 ???
     */
    
    Map<String, Object> result = new HashMap<>();
    
    Set<BiodbMetaboliteNode> cpds = filter(refs, MetaboliteMajorLabel.LipidMAPS.toString());
    
    for (BiodbMetaboliteNode cpdNode : cpds) {
      String mainClass = (String) cpdNode.getProperty("mainClass", null);
      String subSlass = (String) cpdNode.getProperty("subSlass", null);
      String category = (String) cpdNode.getProperty("category", null);
//      String classLevel4 = (String) cpdNode.getProperty("classLevel4", null);
      
      result.put("mainClass", mainClass);
      result.put("subSlass", subSlass);
      result.put("category", category);
//      result.put("classLevel4", classLevel4);
    }
    
    return result;
  }

}
