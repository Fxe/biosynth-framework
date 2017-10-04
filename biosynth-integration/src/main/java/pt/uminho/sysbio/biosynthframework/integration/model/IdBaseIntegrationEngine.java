package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.BMap;

public class IdBaseIntegrationEngine implements BaseIntegrationEngine {

  private static final Logger logger = LoggerFactory.getLogger(IdBaseIntegrationEngine.class);
  
  protected final SearchTable<MetaboliteMajorLabel, String> searchTable;
  public Map<String, EntryPattern> patterns;
  
  
  public IdBaseIntegrationEngine(SearchTable<MetaboliteMajorLabel, String> searchTable) {
    this.searchTable = searchTable;
  }
  
  public Map<MetaboliteMajorLabel, LookupMethod> lookupMethods = new HashMap<> ();

  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate() {

    IntegrationMap<String, MetaboliteMajorLabel> result = new IntegrationMap<>();
    //  SearchTable<MetaboliteMajorLabel, String> searchTable = null;
    for (MetaboliteMajorLabel database : searchTable.get()) {
      LookupMethod lookupMethod = lookupMethods.get(database);
      if (lookupMethod == null) {
        logger.info("no lookup method for {} use identity", database);
        lookupMethod = new IdentityLookupMethod();
      }
//      System.out.println(database);
      BMap<String, String> mapping = searchTable.searchMap.get(database).get("entry");
//      System.out.println(mapping.bkeySet());
      if (mapping != null) {
        for (String id : patterns.keySet()) {
          EntryPattern p = patterns.get(id);
          String searchString = lookupMethod.lookup(p);
//          System.out.println(searchString);
          Set<String> ret = mapping.bget(searchString);
          if (ret == null || ret.isEmpty()) {
            ret = mapping.bget(p.getTrim(0, 1));
            if (ret == null || ret.isEmpty()) {
              ret = mapping.bget(p.getTrim(0, 1).replace("__", "-"));
            }
          }
          if (ret != null) {
            for (String dbRef : ret) {
              result.addIntegration(id, database, dbRef);
            }
          }

        }
      }
    }

    return result;
  }

}
