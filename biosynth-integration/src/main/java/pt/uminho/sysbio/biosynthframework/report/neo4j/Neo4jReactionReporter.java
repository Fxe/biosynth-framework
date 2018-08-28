package pt.uminho.sysbio.biosynthframework.report.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

public class Neo4jReactionReporter extends AbstractNeo4jReporter {

  public Neo4jReactionReporter(GraphDatabaseService service) {
    super(service);
  }

}
