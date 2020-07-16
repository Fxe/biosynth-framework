package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.function.BiFunction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;

public class Neo4jExtFunction implements BiFunction<Long, Long, Double> {
  
  private static Logger logger = LoggerFactory.getLogger(Neo4jExtFunction.class);
  
  private final BiodbGraphDatabaseService graphDataService;
  
  public Neo4jExtFunction(GraphDatabaseService graphDataService) {
    this.graphDataService = new BiodbGraphDatabaseService(graphDataService);
  }
  
  @Override
  public Double apply(Long t, Long u) {
    BiodbMetaboliteNode a = graphDataService.getMetabolite(t); //new BiodbMetaboliteNode(graphDataService.getNodeById(t));
    BiodbMetaboliteNode b = graphDataService.getMetabolite(u); //new BiodbMetaboliteNode(graphDataService.getNodeById(u));
    
    if (a.hasLabel(MetaboliteMajorLabel.BiGG) && b.hasLabel(MetaboliteMajorLabel.BiGGMetabolite) ||
        a.hasLabel(MetaboliteMajorLabel.BiGGMetabolite) && b.hasLabel(MetaboliteMajorLabel.BiGG)) {
      logger.debug("A: {}@{}, B: {}@[}", a.getEntry(), a.getDatabase(), b.getEntry(), b.getDatabase());
      
      if (a.getEntry().equals(b.getEntry())) {
        return 0.5;
      }
    }
    return 0.0;
  }
}
