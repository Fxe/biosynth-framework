package pt.uminho.sysbio.biosynthframework.integration.function;

import java.util.function.Function;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;

public class Neo4jDatabaseConstraintValidator implements Function<Node, Boolean> {
  
  @Override
  public Boolean apply(Node t) {
    return t.hasProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT) && 
        t.hasProperty(Neo4jDefinitions.PROXY_PROPERTY) &&
        t.hasProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
  }
}
