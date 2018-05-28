package pt.uminho.sysbio.biosynthframework.report.neo4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.Tuple2;
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
  
  public Map<String, Map<Tuple2<Set<String>>, Integer>> reportRel() {
    Map<String, Map<Tuple2<Set<String>>, Integer>> result = new HashMap<> ();
    for (Relationship relationship : service.getAllRelationships()) {
      Node start = relationship.getStartNode();
      Node end = relationship.getEndNode();
      Tuple2<Set<String>> t = new Tuple2<Set<String>>(
          Neo4jUtils.getLabelsAsString(start),
          Neo4jUtils.getLabelsAsString(end));
      String type = relationship.getType().name();
      if (!result.containsKey(type)) {
        result.put(type, new HashMap<Tuple2<Set<String>>, Integer>());
      }
      CollectionUtils.increaseCount(result.get(type), t, 1);
    }
    
    return result;
  }
}
