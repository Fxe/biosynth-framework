package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.MetabolicLayoutDao;
import pt.uminho.sysbio.biosynthframework.LayoutNode;
import pt.uminho.sysbio.biosynthframework.MetabolicLayout;
import pt.uminho.sysbio.biosynthframework.MetabolicLayout.LayoutEdge;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;

public class Neo4jMetabolicLayoutDao implements MetabolicLayoutDao {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jMetabolicLayoutDao.class);
  
  protected GraphDatabaseService service;
  protected ExecutionEngine executionEngine;
  protected AnnotationPropertyContainerBuilder propertyContainerBuilder = 
      new AnnotationPropertyContainerBuilder();
  
  public Neo4jMetabolicLayoutDao(GraphDatabaseService graphDatabaseService) {
    this.service = graphDatabaseService;
    this.executionEngine = new ExecutionEngine(graphDatabaseService);
  }
  
  @Override
  public MetabolicLayout findById(long id) {
    Node node = service.getNodeById(id);
    if (node == null || !node.hasLabel(Neo4jLayoutLabel.MetabolicLayout)) {
      return null;
    }
    
    logger.trace("found node {}", id);
    MetabolicLayout metabolicLayout = new MetabolicLayout();
    Map<String, Object> properties = Neo4jUtils.getPropertiesMap(node);
    metabolicLayout.setId(node.getId());
    metabolicLayout.setEntry((String) properties.get("entry"));
    metabolicLayout.setName((String) properties.get("name"));
    metabolicLayout.setSource((String) properties.get("source"));
    metabolicLayout.setDescription((String) properties.get("description"));
    
    for (Node layoutNode : Neo4jUtils.collectNodeRelationshipNodes(
        node, Neo4jLayoutRelationship.has_layout_node)) {
      for (Relationship r : layoutNode.getRelationships(
          Neo4jLayoutRelationship.has_edge)) {
        long edgeId = r.getId();
        if (!metabolicLayout.edges.containsKey(edgeId)) {
          LayoutEdge edge = new LayoutEdge();
          edge.fromNodeId = r.getStartNode().getId();
          edge.toNodeId = r.getEndNode().getId();
          metabolicLayout.edges.put(edgeId, edge);
        }
      }
      metabolicLayout.nodes.put(layoutNode.getId(), null);
    }
    
    return metabolicLayout;
  }

  @Override
  public MetabolicLayout findByEntry(String entry) {
    Node node = Neo4jUtils.getUniqueResult(
        service.findNodesByLabelAndProperty(Neo4jLayoutLabel.MetabolicLayout, 
                                            "entry", entry));
    return findById(node.getId());
  }

  @Override
  public Long save(MetabolicLayout entity) {
    if (entity == null || 
        entity.getId() != null ||
        entity.getEntry() == null) {
      
      logger.debug("invalid MetabolicLayout {}", entity);
      return null;
    }
    
    Long result = null;
    
    try {
      Map<String, Object> properties = 
          propertyContainerBuilder.extractProperties(entity, MetabolicLayout.class);
      Node node = service.createNode();
      node.addLabel(Neo4jLayoutLabel.MetabolicLayout);
      entity.setId(node.getId());
      result = node.getId();
      for (String k : properties.keySet()) {
        Object v = properties.get(k);
        if (v instanceof Double) {
          node.setProperty(k, v);
        } else if (v instanceof String) {
          node.setProperty(k, v);
        } else if (v instanceof Long) {
          node.setProperty(k, v);
        } else {
          node.setProperty(k, v.toString());
        }
      }
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    // TODO Auto-generated method stub
    return result;
  }

  @Override
  public boolean update(MetabolicLayout entity) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean delete(MetabolicLayout entity) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Map<Long, String> list() {
    Map<Long, String> result = new HashMap<> ();
    for (Node node : GlobalGraphOperations.at(service)
        .getAllNodesWithLabel(Neo4jLayoutLabel.MetabolicLayout)) {
      result.put(node.getId(), (String) node.getProperty("entry"));
    }
    return result;
  }

  @Override
  public void addNode(MetabolicLayout metabolicLayout, LayoutNode layoutNode) {
    Node metabolicLayoutNode = service.getNodeById(metabolicLayout.getId());
    Node node = service.getNodeById(layoutNode.getId());
    
    //check type o graph node
    if (!metabolicLayoutNode.hasLabel(Neo4jLayoutLabel.MetabolicLayout) || 
        !node.hasLabel(Neo4jLayoutLabel.LayoutNode)) {
      logger.warn("invalid parameters {}, {}", metabolicLayoutNode, node);
      return;
    }
    
    if (!Neo4jUtils.isConnected(metabolicLayoutNode, node)) {
      logger.trace("link {} -> {}", metabolicLayoutNode, node);
      metabolicLayoutNode.createRelationshipTo(
          node, Neo4jLayoutRelationship.has_layout_node);
    }
  }

  @Override
  public void addEdge(LayoutNode from, LayoutNode to) {
    Node fromNode = service.getNodeById(from.getId());
    Node toNode   = service.getNodeById(to.getId());
    
    //check if both nodes are LayoutNode
    if (!fromNode.hasLabel(Neo4jLayoutLabel.LayoutNode) || 
        !toNode.hasLabel(Neo4jLayoutLabel.LayoutNode)) {
      logger.warn("invalid parameters {}, {}", fromNode, toNode);
      return;
    }
    
    if (!Neo4jUtils.isConnected(fromNode, toNode)) {
      logger.trace("link {} -> {}", fromNode, toNode);
      fromNode.createRelationshipTo(toNode, Neo4jLayoutRelationship.has_edge);
    }
  }

}
