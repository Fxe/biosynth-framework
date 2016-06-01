package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.Neo4jNode;
import pt.uminho.sysbio.biosynth.integration.Neo4jRelationship;

public class Neo4jSuperDaoImpl implements Neo4jSuperDao {

  private final static Logger logger = LoggerFactory.getLogger(Neo4jSuperDaoImpl.class);

  private final GraphDatabaseService graphDatabaseService;
  private final ExecutionEngine executionEngine;

  public Neo4jSuperDaoImpl(GraphDatabaseService graphDatabaseService) {
    this.graphDatabaseService = graphDatabaseService;
    this.executionEngine = new ExecutionEngine(graphDatabaseService);
  }

  private Neo4jNode toNeo4jNode(Node node) {
    Neo4jNode neo4jNode = new Neo4jNode();
    neo4jNode.setId(node.getId());
    neo4jNode.setLabels2(Neo4jUtils.getLabels(node));
    neo4jNode.setPropertyContainer(Neo4jUtils.getPropertiesMap(node));

    return neo4jNode;
  }

  private Neo4jRelationship toNeo4jRelationship(Relationship relationship) {
    Neo4jRelationship neo4jRelationship = new Neo4jRelationship();
    neo4jRelationship.setId(relationship.getId());
    neo4jRelationship.setType(relationship.getType().name());
    neo4jRelationship.setProperties(Neo4jUtils.getPropertiesMap(relationship));

    return neo4jRelationship;
  }

  @Override
  public Neo4jNode getAnyNode(long id) {
    Node node = graphDatabaseService.getNodeById(id);

    Neo4jNode neo4jNode = toNeo4jNode(node);

    for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
      Node otherNode = relationship.getOtherNode(node);

      Neo4jRelationship neo4jRelationship = toNeo4jRelationship(relationship);
      neo4jRelationship.setDireaction(Direction.OUTGOING);
      Neo4jNode neo4jOtherNode = toNeo4jNode(otherNode);
      long rId = relationship.getId();
      long nId = otherNode.getId();
      neo4jNode.getNodes().put(nId, neo4jOtherNode);
      neo4jNode.getEdges().put(rId, neo4jRelationship);
      neo4jNode.getLinks().put(rId, nId);
    }

    for (Relationship relationship : node.getRelationships(Direction.INCOMING)) {
      Node otherNode = relationship.getOtherNode(node);

      Neo4jRelationship neo4jRelationship = toNeo4jRelationship(relationship);
      neo4jRelationship.setDireaction(Direction.INCOMING);
      Neo4jNode neo4jOtherNode = toNeo4jNode(otherNode);
      long rId = relationship.getId();
      long nId = otherNode.getId();
      neo4jNode.getNodes().put(nId, neo4jOtherNode);
      neo4jNode.getEdges().put(rId, neo4jRelationship);
      neo4jNode.getLinks().put(rId, nId);
    }

    return neo4jNode;
  }

  @Override
  public Neo4jNode getAnyNodeLimit(long id, int limit) {
    int limit_counter;
    Node node = graphDatabaseService.getNodeById(id);

    Neo4jNode neo4jNode = toNeo4jNode(node);

    int totalRelationships = 0;

    limit_counter = 0;
    for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
      Node otherNode = relationship.getOtherNode(node);
      if (limit_counter < limit) {
        Neo4jRelationship neo4jRelationship = toNeo4jRelationship(relationship);
        neo4jRelationship.setDireaction(Direction.OUTGOING);
        Neo4jNode neo4jOtherNode = toNeo4jNode(otherNode);
        long rId = relationship.getId();
        long nId = otherNode.getId();
        neo4jNode.getNodes().put(nId, neo4jOtherNode);
        neo4jNode.getEdges().put(rId, neo4jRelationship);
        neo4jNode.getLinks().put(rId, nId);
        limit_counter++;
      }
      totalRelationships++;
    }
    limit_counter = 0;
    for (Relationship relationship : node.getRelationships(Direction.INCOMING)) {
      Node otherNode = relationship.getOtherNode(node);
      if (limit_counter < limit) {
        Neo4jRelationship neo4jRelationship = toNeo4jRelationship(relationship);
        neo4jRelationship.setDireaction(Direction.INCOMING);
        Neo4jNode neo4jOtherNode = toNeo4jNode(otherNode);
        long rId = relationship.getId();
        long nId = otherNode.getId();
        neo4jNode.getNodes().put(nId, neo4jOtherNode);
        neo4jNode.getEdges().put(rId, neo4jRelationship);
        neo4jNode.getLinks().put(rId, nId);
        limit_counter++;
      }
      totalRelationships++;
    }
    neo4jNode.totalRelationships = totalRelationships;

    return neo4jNode;
  }

  @Override
  public Neo4jNode getMetaboliteNode(long id) {
    Node node = graphDatabaseService.getNodeById(id);
    if (!node.hasLabel(GlobalLabel.Metabolite)) return null;

    Neo4jNode neo4jNode = new Neo4jNode();
    neo4jNode.setId(id);
    neo4jNode.setPropertyContainer(Neo4jUtils.getPropertiesMap(node));

    return neo4jNode;
  }

  @Override
  public Neo4jNode getReactionNode(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<Set<String>, Set<Long>> superHeavyMethod() {
    Map<Set<String>, Set<Long>> result = new HashMap<> ();
    for (Node node : GlobalGraphOperations.at(graphDatabaseService).getAllNodes()) {
      Set<String> labels = Neo4jUtils.getLabelsAsString(node);
      if (!result.containsKey(labels)) {
        result.put(labels, new HashSet<Long> ());
      }
      result.get(labels).add(node.getId());
    }
    return result;
  }

  @Override
  public void delete(Node node) {
    if (node != null) {
      for (Relationship relationship : node.getRelationships()) {
        logger.debug("Delete edge: " + relationship);
        relationship.delete();
      }
      logger.debug(String.format("Delete vertex - [%d*%s] ", node.getId(), StringUtils.join(Neo4jUtils.getLabels(node), ':')));
      node.delete();
    }
  }

  @Override
  public boolean linkIfNotExists(long src, long dst,
      RelationshipType relationshipType, Map<String, Object> properties) {

    Node srcNode = graphDatabaseService.getNodeById(src);
    Node dstNode = graphDatabaseService.getNodeById(dst);
    logger.trace("Link if not exists {} -[{}]-> {}", src, relationshipType, dst);
    for (Relationship relationship : srcNode.getRelationships(Direction.OUTGOING)) {
      Node other = relationship.getOtherNode(srcNode);
      if (other.getId() == dst) {
        logger.trace("Link exists");
        return false;
      }
    }
    logger.trace("Link created");
    Relationship relationship = srcNode.createRelationshipTo(dstNode, relationshipType);
    Neo4jUtils.setPropertiesMap(properties, relationship);
    Neo4jUtils.setCreatedTimestamp(relationship);
    Neo4jUtils.setUpdatedTimestamp(relationship);
    return true;
  }

  @Override
  public boolean unlinkIfExists(long src, long dst) {
    Node srcNode = graphDatabaseService.getNodeById(src);
    logger.trace("Link if exists {} -[*]-> {}", src, dst);
    boolean modified = false;
    for (Relationship relationship : srcNode.getRelationships(Direction.OUTGOING)) {
      Node other = relationship.getOtherNode(srcNode);
      if (other.getId() == dst) {
        logger.trace("Deleted Link {}:{} - {}", relationship.getId(), relationship.getType().name(), Neo4jUtils.getPropertiesMap(relationship));
        relationship.delete();
        modified = true;
      }
    }
    return modified;
  }

  @Override
  public GraphDatabaseService getGraphDatabaseService() {
    return this.graphDatabaseService;
  }

  @Override
  public String executeQuery(String query, Map<String, Object> params) {
    return this.executionEngine.execute(query, params).dumpToString();
  }

  @Override
  public Set<Long> findNodesByLabelAndProperty(String label, String key, Object value) {
    Set<Long> idSet = new HashSet<> ();
    for (Node node : IteratorUtil.asList(graphDatabaseService
        .findNodesByLabelAndProperty(DynamicLabel.label(label), key, value))) {
      idSet.add(node.getId());
    }
    return idSet;
  }

  @Override
  public Object getNodeProperty(long nodeId, String property) {
    Node node = graphDatabaseService.getNodeById(nodeId);
    return node.getProperty(property, null);
  }

}
