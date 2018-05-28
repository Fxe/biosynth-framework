package pt.uminho.sysbio.biosynthframework.report.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;

public abstract class AbstractNeo4jReporter {
  
  protected final BiodbGraphDatabaseService service;
  
  public AbstractNeo4jReporter(GraphDatabaseService service) {
    this.service = new BiodbGraphDatabaseService(service);
  }
}
