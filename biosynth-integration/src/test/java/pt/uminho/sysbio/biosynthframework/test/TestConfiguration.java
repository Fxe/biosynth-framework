package pt.uminho.sysbio.biosynthframework.test;

import org.neo4j.graphdb.GraphDatabaseService;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;

public class TestConfiguration {
  public static GraphDatabaseService getTestGraphDatabaseService() {
    return HelperNeo4jConfigInitializer.initializeNeo4jDatabase(
        "/var/biobase/neo4j/bios.db");
  }
}
