package pt.uminho.sysbio.biosynthframework.integration.etl;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;

public abstract class AbstractNeo4jEtl {
  
  protected final BiodbGraphDatabaseService service;
  
  public AbstractNeo4jEtl(GraphDatabaseService service) {
    this.service = new BiodbGraphDatabaseService(service);
  }
  
  public abstract void etl(String entry);
}
