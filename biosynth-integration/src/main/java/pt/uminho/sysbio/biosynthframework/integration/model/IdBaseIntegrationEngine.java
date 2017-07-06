package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.BMap;

public class IdBaseIntegrationEngine implements BaseIntegrationEngine {

  protected final SearchTable<MetaboliteMajorLabel, String> searchTable;
  public Map<String, EntryPattern> patterns;
  
  
  public IdBaseIntegrationEngine(SearchTable<MetaboliteMajorLabel, String> searchTable) {
    this.searchTable = searchTable;
  }

  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate() {

    IntegrationMap<String, MetaboliteMajorLabel> result = new IntegrationMap<>();
    //  SearchTable<MetaboliteMajorLabel, String> searchTable = null;
    for (MetaboliteMajorLabel database : searchTable.get()) {
      BMap<String, String> mapping = searchTable.searchMap.get(database).get("entry");
      if (mapping != null) {
        for (String id : patterns.keySet()) {
          EntryPattern p = patterns.get(id);
          //TODO: implemente smart search
          Set<String> ret = mapping.bget(p.trim);
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
