package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.function.BiFunction;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;

public class Neo4jReferenceFunction implements BiFunction<Long, Long, Double> {

  private static Logger logger = LoggerFactory.getLogger(Neo4jReferenceFunction.class);
  
  private final GraphDatabaseService graphDataService;
  
  public double dsingle = 0.5;
  public double ddouble = 1.0;
  
  public Neo4jReferenceFunction(GraphDatabaseService graphDataService) {
    this.graphDataService = graphDataService;
  }
  
  @Override
  public Double apply(Long t, Long u) {
    Node a = graphDataService.getNodeById(t);
    Node b = graphDataService.getNodeById(u);
    
    if (a.hasLabel(MetaboliteMajorLabel.BiGGMetabolite) ||
        b.hasLabel(MetaboliteMajorLabel.BiGGMetabolite)) {
      return 0.0;
    }
    
    boolean ab = false;
    boolean ba = false;
//    System.out.println(Neo4jUtils.getPropertiesMap(a));
    for (Relationship r : a.getRelationships(
        Direction.OUTGOING, MetaboliteRelationshipType.has_crossreference_to)) {
      logger.debug("A: {}", r);
      Node other = r.getOtherNode(a);
//      System.out.println(Neo4jUtils.getPropertiesMap(other));
      if (other.getId() == b.getId()) {
        ab = true;
      }
    }
//    System.out.println(Neo4jUtils.getPropertiesMap(b));
    for (Relationship r : b.getRelationships(
        Direction.OUTGOING, MetaboliteRelationshipType.has_crossreference_to)) {
      logger.debug("B: {}", r);
      Node other = r.getOtherNode(b);
//      System.out.println(Neo4jUtils.getPropertiesMap(other));
      if (other.getId() == a.getId()) {
        ba = true;
      }
    }
    
    logger.debug("A->B: {}, B->A: {}", ab, ba);
    
//    Set<Long> sa = Neo4jUtils.collectNodeRelationshipNodeIds(
//        a, MetaboliteRelationshipType.has_crossreference_to);
//    Set<Long> sb = Neo4jUtils.collectNodeRelationshipNodeIds(
//        b, MetaboliteRelationshipType.has_crossreference_to);
    
    double directScore = 0.0;
    if (ba && ab) {
      directScore = ddouble;
    } else if (ba || ab) {
      directScore = dsingle;
    }
//    double d = 0.0;
//    if (sa.contains(b.getId()) && sb.contains(a.getId())) {
//      d = 1.0;
//    } else if (sa.contains(b.getId()) || sb.contains(a.getId())) {
//      d = 0.8;
//    }
//    
//    double p = 0.0;
//    if (!Sets.intersection(sa, sb).isEmpty()) {
//      p = 0.0;
//    }
    
    return directScore;
  }
}
