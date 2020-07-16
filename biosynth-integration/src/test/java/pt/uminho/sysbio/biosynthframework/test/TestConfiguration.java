package pt.uminho.sysbio.biosynthframework.test;

import edu.uminho.biosynth.core.data.integration.neo4j.HelperNeo4jConfigInitializer;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;

public class TestConfiguration {
  public static BiodbGraphDatabaseService getTestGraphDatabaseService() {
    return getTestGraphDatabaseService("/var/biobase/neo4j/bios.db");
  }
  
  public static BiodbGraphDatabaseService getTestGraphDatabaseService(String db) {
    BiodbGraphDatabaseService biodbGraphDatabaseService = 
        new BiodbGraphDatabaseService(HelperNeo4jConfigInitializer.initializeNeo4jDatabase(db));
//    biodbGraphDatabaseService.databasePath = db;
    return biodbGraphDatabaseService;
  }
}
