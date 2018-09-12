package pt.uminho.sysbio.biosynthframework.report.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

public class Neo4jUniversalDataReporter extends AbstractNeo4jReporter {

  public Neo4jUniversalDataReporter(GraphDatabaseService service) {
    super(service);
  }

  
}
