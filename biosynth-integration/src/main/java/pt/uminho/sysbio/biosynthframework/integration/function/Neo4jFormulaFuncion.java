package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.Set;
import java.util.function.BiFunction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;

public class Neo4jFormulaFuncion implements BiFunction<Long, Long, Double> {

  private final GraphDatabaseService service;
  
  public Neo4jFormulaFuncion(GraphDatabaseService service) {
    this.service = service;
  }
  
  @Override
  public Double apply(Long t, Long u) {
    Node a = service.getNodeById(t);
    Node b = service.getNodeById(u);
    Set<Long> sa = Neo4jUtils.collectNodeRelationshipNodeIds(
        a, MetaboliteRelationshipType.has_molecular_formula);
    Set<Long> sb = Neo4jUtils.collectNodeRelationshipNodeIds(
        b, MetaboliteRelationshipType.has_molecular_formula);
    
    if (!Sets.intersection(sa, sb).isEmpty()) {
      return 0.1;
    }
    
    return 0.0;
  }
}
