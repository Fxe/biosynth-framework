package pt.uminho.sysbio.biosynthframework.neo4j.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class TotalLabelSetReporter extends AbstractNeo4jReporter {
  
  public TotalLabelSetReporter(GraphDatabaseService service) {
    super(service);
  }

  public Map<Set<String>, Integer> report() {
    Map<Set<String>, Integer> result = new HashMap<> ();
    for (Node node : service.getAllNodes()) {
      Set<String> labels = Neo4jUtils.getLabelsAsString(node);
      CollectionUtils.increaseCount(result, labels, 1);
    }
    return result;
  }
}
