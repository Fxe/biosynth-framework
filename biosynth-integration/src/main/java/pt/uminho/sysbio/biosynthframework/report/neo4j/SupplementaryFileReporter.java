package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynthframework.neo4j.LiteratureDatabase;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class SupplementaryFileReporter extends AbstractNeo4jReporter {

  public SupplementaryFileReporter(GraphDatabaseService service) {
    super(service);
  }
  
  public Map<String, Integer> report() {
    Map<String, Integer> count = new HashMap<>();
    for (Node node : service.listNodes(LiteratureDatabase.SupplementaryMaterial)) {
      String filename = (String) node.getProperty("file");
      if (filename.contains(".")) {
        String[] p = filename.split("\\.");
        String ext = p[p.length - 1];
        CollectionUtils.increaseCount(count, ext.toLowerCase(), 1);
      } else {
        CollectionUtils.increaseCount(count, "?", 1);
      }
    }
    
    return count;
  }
  
  public Map<String, Integer> reportJournalCount() {
    Map<String, Integer> count = new HashMap<>();
    for (Node node : service.listNodes(LiteratureDatabase.PubMed)) {
      String journal = (String) node.getProperty("journal_abbreviation");
      CollectionUtils.increaseCount(count, journal, 1);
    }
    
    return count;
  }
}
