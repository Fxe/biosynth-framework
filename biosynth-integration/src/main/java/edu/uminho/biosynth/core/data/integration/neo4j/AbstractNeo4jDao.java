package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;

@Deprecated
public abstract class AbstractNeo4jDao<T> {

  //	private static Logger LOGGER =

  
  protected BiodbGraphDatabaseService graphDatabaseService;

  public AbstractNeo4jDao() { }

  @Autowired
  public AbstractNeo4jDao(GraphDatabaseService graphdb) {
    this.graphDatabaseService = new BiodbGraphDatabaseService(graphdb);
  }

  public GraphDatabaseService getGraphDatabaseService() {
    return graphDatabaseService;
  }

  public void setGraphDatabaseService(GraphDatabaseService graphdb) {
    this.graphDatabaseService = new BiodbGraphDatabaseService(graphdb);
  }

  protected void create(
      Node node, Label majorLabel, Object value, 
      RelationshipType relationshipType, Map<String, Object> relationshipProperties) {

    ResourceIterator<Node> nodes = graphDatabaseService.findNodes(majorLabel, "entry", value);

    @SuppressWarnings("unused")
    boolean create = true;
    while (nodes.hasNext()) {
      Node proxyNode = nodes.next();
      create = false;
      //TODO: SET TO UPDATE
      Relationship relationship = node.createRelationshipTo(proxyNode, relationshipType);
      for (String key : relationshipProperties.keySet()) {
        relationship.setProperty(key, relationshipProperties.get(key));
      }
    }
  }


  protected abstract T nodeToObject(Node node);
}
