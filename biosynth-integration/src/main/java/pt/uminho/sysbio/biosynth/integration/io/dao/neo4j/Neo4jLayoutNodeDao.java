package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.LayoutNodeDao;
import pt.uminho.sysbio.biosynthframework.LayoutNode;
import pt.uminho.sysbio.biosynthframework.LayoutNode.LayoutNodeType;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.annotations.AnnotationPropertyContainerBuilder;

public class Neo4jLayoutNodeDao implements LayoutNodeDao {
  
  private static final Logger logger = LoggerFactory.getLogger(Neo4jLayoutNodeDao.class);
  
  protected GraphDatabaseService service;
  protected ExecutionEngine executionEngine;
  protected AnnotationPropertyContainerBuilder propertyContainerBuilder = 
      new AnnotationPropertyContainerBuilder();
  
  public Neo4jLayoutNodeDao(GraphDatabaseService graphDatabaseService) {
    this.service = graphDatabaseService;
    this.executionEngine = new ExecutionEngine(graphDatabaseService);
  }
  
  private Node getOrCreateReferenceNode(String database,
                                        String entry,
                                        long referenceId,
                                        Neo4jLayoutLabel label) {
    
    Node node = Neo4jUtils.getUniqueResult(service
        .findNodesByLabelAndProperty(label, 
            Neo4jDefinitions.MEMBER_REFERENCE, referenceId));
    if (node == null) {
      logger.trace("created {} node [{}]{}:{}", 
          label, referenceId, database, entry);
      
      node = service.createNode();
      node.addLabel(label);
      node.setProperty(Neo4jDefinitions.MEMBER_REFERENCE, referenceId);
      node.setProperty("database", database);
      node.setProperty("entry", entry);
    }
    return node;
  }

  @Override
  public LayoutNode findById(long id) {
    Node node = service.getNodeById(id);
    if (node == null || !node.hasLabel(Neo4jLayoutLabel.LayoutNode)) {
      return null;
    }
    
    logger.trace("found node {}", id);
    LayoutNode layoutNode = new LayoutNode();
    Map<String, Object> properties = Neo4jUtils.getPropertiesMap(node);
    layoutNode.setId(node.getId());
    layoutNode.setSource((String) properties.get("source"));
    layoutNode.setDescription((String) properties.get("description"));
    layoutNode.label = (String) properties.get("label");
    layoutNode.x = (Double) properties.get("x");
    layoutNode.y = (Double) properties.get("y");
    layoutNode.type = LayoutNodeType.valueOf((String) properties.get("type"));
    if (properties.containsKey("compartment")) {
      layoutNode.compartment = SubcellularCompartment.valueOf(
          (String) properties.get("compartment"));
    }
    
    for (Node refNode : Neo4jUtils.collectNodeRelationshipNodes(
        node, Neo4jLayoutRelationship.is_a)) {
      String database = (String) refNode.getProperty("database");
      String entry = (String) refNode.getProperty("entry");
      long refId = (long) refNode.getProperty(Neo4jDefinitions.MEMBER_REFERENCE);
      layoutNode.addAnnotation(database, refId, entry);
    }
    
    return layoutNode;
  }

  @Override
  public LayoutNode findByEntry(String entry) {
    Node node = Neo4jUtils.getUniqueResult(
        service.findNodesByLabelAndProperty(Neo4jLayoutLabel.LayoutNode, 
                                            "entry", entry));
    return findById(node.getId());
  }

  @Override
  public Long save(LayoutNode layoutNode) {
    if (layoutNode == null || 
        layoutNode.getId() != null ||
        layoutNode.type == null ||
        layoutNode.x == null ||
        layoutNode.y == null) {
      logger.debug("invalid layout node {}", layoutNode);
      return null;
    }
    
    Long result = null;
    
    try {
      Map<String, Object> properties = 
          propertyContainerBuilder.extractProperties(layoutNode, LayoutNode.class);
      Node node = service.createNode();
      layoutNode.setId(node.getId());
      node.addLabel(Neo4jLayoutLabel.LayoutNode);
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

      for (String database : layoutNode.annotation.keySet()) {
        for (long id : layoutNode.annotation.get(database).keySet()) {
          Neo4jLayoutLabel refType = 
              layoutNode.type.equals(LayoutNodeType.SPECIE) ?
                  Neo4jLayoutLabel.MetaboliteReference : 
                  Neo4jLayoutLabel.ReactionReference;
          Node refNode = getOrCreateReferenceNode(database, 
              layoutNode.annotation.get(database).get(id), id, refType);
          node.createRelationshipTo(refNode, Neo4jLayoutRelationship.is_a);
        }
      }
      
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    
    return result;
  }

  @Override
  public boolean update(LayoutNode entity) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean delete(LayoutNode entity) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Map<Long, String> list() {
    Map<Long, String> result = new HashMap<> ();
    for (Node node : GlobalGraphOperations
        .at(service)
        .getAllNodesWithLabel(Neo4jLayoutLabel.LayoutNode)) {
      String entry = (String) node.getProperty("entry", "");
      if (entry.isEmpty()) {
        logger.warn("found node {} with empty entry", node.getId());
      } else {
        result.put(node.getId(), entry);
      }
    }
    
    return result;
  }

  @Override
  public Map<Long, LayoutNode> listNodes(Set<Long> nodeIds) {
    Map<Long, LayoutNode> result = new HashMap<> ();
    for (long nodeId : nodeIds) {
      LayoutNode layoutNode = this.findById(nodeId);
      if (layoutNode != null) {
        result.put(nodeId, layoutNode);
      }
    }
    return result;
  }

  @Override
  public void updateAnnotation(long nodeId, 
                               Map<String, Map<Long, String>> annotation, 
                               String referenceType) {
    Node node = service.getNodeById(nodeId);
    
    Neo4jLayoutLabel referenceLabel = Neo4jLayoutLabel.valueOf(referenceType);
    Set<Long> annotationRelationshipIds = new HashSet<> ();
    for (String database : annotation.keySet()) {
      for (long id : annotation.get(database).keySet()) {
        Node refNode = getOrCreateReferenceNode(database, 
            annotation.get(database).get(id), id, referenceLabel);
        
        Relationship relationship = Neo4jUtils.getRelationshipBetween(
            node, refNode, Direction.BOTH);
        if (relationship == null) {
          relationship = node.createRelationshipTo(
              refNode, Neo4jLayoutRelationship.is_a);
          logger.trace("{} -N-[{}]-> {}", node, relationship.getType().name(), refNode);
        } else {
          logger.trace("{} -O-[{}]-> {}", node, relationship.getType().name(), refNode);
        }
        annotationRelationshipIds.add(relationship.getId());
      }
    }
    
    for (Relationship relationship : node.getRelationships(
        Neo4jLayoutRelationship.is_a)) {
      if (!annotationRelationshipIds.contains(relationship.getId())) {
        logger.trace("{} -X-[{}]-> {}", relationship.getStartNode(), relationship.getType().name(), relationship.getEndNode());
        relationship.delete();
      }
    }
  }

}
