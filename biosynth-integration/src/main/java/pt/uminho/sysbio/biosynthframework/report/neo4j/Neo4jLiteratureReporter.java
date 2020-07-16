package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.Dataset;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosLiteratureNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosMetabolicModelNode;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosSupplementaryFileNode;
import pt.uminho.sysbio.biosynthframework.neo4j.LiteratureDatabase;

public class Neo4jLiteratureReporter extends AbstractNeo4jReporter {

  public Neo4jLiteratureReporter(GraphDatabaseService service) {
    super(service);
  }
  
  /**
   * reports orphan models and pmid aliases using model ID
   */
  public Dataset<String, String, String> report() {
    Dataset<String, String, String> report = new Dataset<>();
    Set<String> models = new HashSet<>();
    
    for (BiosMetabolicModelNode modelNode : service.listMetabolicModels()) {
      models.add(modelNode.getEntry());
    }

    Set<String> assigned = new HashSet<>();
    for (Node nn : service.listNodes(LiteratureDatabase.PubMed)) {
      BiosLiteratureNode n = new BiosLiteratureNode(nn, null);
      Set<String> alias = new HashSet<>();
      
      for (BiosSupplementaryFileNode supNode : n.getSupplementaryFiles()) {
        for (Node mnode : Neo4jUtils.collectNodeRelationshipNodes(supNode, GenericRelationship.has_source_file)) {
          String e = (String) mnode.getProperty("entry");
          assigned.add(e);
          alias.add(e);
        }
      }
      report.add(n.getEntry(), "Model Aliases", StringUtils.join(alias, ';'));
    }
    
    models.removeAll(assigned);

    report.add("ORPHAN", "Model Aliases", StringUtils.join(models, ';'));
    
    return report;
  }

}
