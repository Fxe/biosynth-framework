package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.integration.model.ConnectedComponents;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

/**
 * Utilities used to perform several Neo4j operations.
 * 
 * @author Filipe
 */
public class Neo4jUtils {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jUtils.class);

  public static void setCreatedTimestamp(PropertyContainer node) {
    node.setProperty("created_at", System.currentTimeMillis());
  }

  public static void setUpdatedTimestamp(PropertyContainer node) {
    node.setProperty("updated_at", System.currentTimeMillis()); 
  }

  public static Set<Long> collectNodes(Node node) {
    return null;
  }
  
  public static ConnectedComponents<String> translateCpds(ConnectedComponents<Long> ccs, BiodbGraphDatabaseService service) {
    ConnectedComponents<String> result = new ConnectedComponents<>();
    for (Set<Long> ids : ccs) {
      Set<String> t = new HashSet<>();
      for (long id : ids) {
        BiodbMetaboliteNode node = service.getMetabolite(id);
        if (node != null) {
          t.add(String.format("%s@%s", node.getEntry(), node.getDatabase()));
        }
      }
      if (!t.isEmpty()) {
        result.add(t);
      }
    }
    return result;
  }
  
  public static ConnectedComponents<String> translateRxns(ConnectedComponents<Long> ccs, BiodbGraphDatabaseService service) {
    ConnectedComponents<String> result = new ConnectedComponents<>();
    for (Set<Long> ids : ccs) {
      Set<String> t = new HashSet<>();
      for (long id : ids) {
        BiodbReactionNode node = service.getReaction(id);
        if (node != null) {
          t.add(String.format("%s@%s", node.getEntry(), node.getDatabase()));
        }
      }
      if (!t.isEmpty()) {
        result.add(t);
      }
    }
    return result;
  }
  
  public static ConnectedComponents<Long> toCpdIds(ConnectedComponents<String> ccs, Set<String> notfound, GraphDatabaseService graphDataService) {
    ConnectedComponents<Long> result = new ConnectedComponents<>();
    
    for (Set<String> set : ccs) {
      Set<Long> ids = new HashSet<> ();
      for (String s : set) {
        MetaboliteMajorLabel db = MetaboliteMajorLabel.valueOf(s.split("@")[1]);
        String e = s.split("@")[0];
        Node n = Neo4jUtils.getNodeByEntry(db, e, graphDataService);
        if (n != null) {
          ids.add(n.getId());
        } else {
          if (notfound != null) {
            notfound.add(s);
          }
          logger.debug("not found: {}", s);
        }
      }
      if (!ids.isEmpty()) {
        result.add(ids);
      }
    }
    
    return result;
  }
  
  public static ConnectedComponents<Long> toRxnIds(ConnectedComponents<String> ccs, Set<String> notfound, GraphDatabaseService graphDataService) {
    ConnectedComponents<Long> result = new ConnectedComponents<>();
    
    for (Set<String> set : ccs) {
      Set<Long> ids = new HashSet<> ();
      for (String s : set) {
        ReactionMajorLabel db = ReactionMajorLabel.valueOf(s.split("@")[1]);
        String e = s.split("@")[0];
        Node n = Neo4jUtils.getNodeByEntry(db, e, graphDataService);
        if (n != null) {
          ids.add(n.getId());
        } else {
          if (notfound != null) {
            notfound.add(s);
          }
          logger.debug("not found: {}", s);
        }
      }
      if (!ids.isEmpty()) {
        result.add(ids);
      }
    }
    
    return result;
  }
  
//  public static ConnectedComponents<Long> toSpiIds(ConnectedComponents<String> ccs, Set<String> notfound, GraphDatabaseService graphDataService) {
//    ConnectedComponents<Long> result = new ConnectedComponents<>();
//    
//    for (Set<String> set : ccs) {
//      Set<Long> ids = new HashSet<> ();
//      for (String s : set) {
//        Neo4jUtils.
//        String e = s.split("@")[0];
////        Node n = Neo4jUtils.getNodeByKey(MetabolicModelLabel.MetaboliteSpecie, "id", graphDataService);
//        if (n != null) {
//          ids.add(n.getId());
//        } else {
//          if (notfound != null) {
//            notfound.add(s);
//          }
//          logger.debug("not found: {}", s);
//        }
//      }
//      if (!ids.isEmpty()) {
//        result.add(ids);
//      }
//    }
//    
//    return result;
//  }

  //	public static Set<Long> collectNodes(Set<Long> eids, ReactionRelationshipType...relationshipTypes) {
  //		Set<Long> nodes = new HashSet<> ();
  //		
  //		for (Long eid : eids) nodes.addAll(collectNodes(graphDataService.getNodeById(eid), relationshipTypes));
  //		
  //		return nodes;
  //	}
  //	public static Set<Long> findNodeIdsByLabelAndProperty(Label label, String key, Object value) {
  //		Set<Long> nodes = new HashSet<> ();
  //		this.graphDatabaseService.findNodesByLabelAndProperty(label, key, value);
  //
  //		
  //		return nodes;
  //	}

  public static Set<Long> collectNodeIdsFromNodes(Collection<Node> nodes) {
    Set<Long> ids = new HashSet<> ();

    for (Node node : nodes) ids.add(node.getId());

    return ids;
  }

  public static Set<Long> collectNodeRelationshipNodeIds(Node node, RelationshipType...relationshipTypes) {
    Set<Long> nodes = new HashSet<> ();

    for (Relationship relationship : node.getRelationships(relationshipTypes)) {
      Node other = relationship.getOtherNode(node);
      nodes.add(other.getId());
    }

    return nodes;
  }

  public static Set<Node> collectNodeRelationshipNodes(Node node, RelationshipType...relationshipTypes) {
    Set<Node> nodes = new HashSet<> ();

    for (Relationship relationship : node.getRelationships(relationshipTypes)) {
      Node other = relationship.getOtherNode(node);
      nodes.add(other);
    }

    return nodes;
  }

  public static Set<Node> collectNodeRelationshipNodes(Node node, Label...labels) {
    Set<Node> nodes = new HashSet<> ();

    for (Relationship relationship : node.getRelationships()) {
      Node other = relationship.getOtherNode(node);
      if (hasAnyLabel(other, labels)) nodes.add(other);
    }
    return nodes;
  }

  public static boolean hasAnyLabel(Node node, Label...labels) {
    for (Label label : labels) {
      if (node.hasLabel(label)) return true;
    }
    return false;
  }

  public static Set<Long> collectNodeRelationshipNodeIds(Node node) {
    Set<Long> nodes = new HashSet<> ();

    for (Relationship relationship : node.getRelationships()) {
      Node other = relationship.getOtherNode(node);
      nodes.add(other.getId());
    }

    return nodes;
  }

  public static Set<Label> getLabels(Node node) {
    Set<Label> result = new HashSet<> ();
    for (Label l : node.getLabels()) {
      result.add(l);
    }
    return result;
  }
  public static Set<String> getLabelsAsString(Node node) {
    Set<String> labels = new HashSet<> ();
    for (Label label : node.getLabels()) {
      labels.add(label.toString());
    }
    return labels;
  }

  public static Map<String, Object> getPropertiesMap(Node node) {
    return getPropertiesFromPropertyContainer(node);
  }
  public static Map<String, Object> getPropertiesMap(Relationship relationship) {
    return getPropertiesFromPropertyContainer(relationship);
  }
  private static Map<String, Object> getPropertiesFromPropertyContainer(PropertyContainer propertyContainer) {
    if (propertyContainer == null) return null;

    Map<String, Object> map = new HashMap<> ();
    for (String key : propertyContainer.getPropertyKeys()) { 
      map.put(key, propertyContainer.getProperty(key));
    }

    return map;
  }
  public static void setPropertiesMap(Map<String, Object> properties, Node node) {
    setPropertiesFromPropertyContainer(properties, node);
  }
  public static void setPropertiesMap(Map<String, Object> properties, Relationship relationship) {
    setPropertiesFromPropertyContainer(properties, relationship);
  }
  private static void setPropertiesFromPropertyContainer(
      Map<String, Object> properties, PropertyContainer propertyContainer) {
    if (propertyContainer == null) return;

    for (String key : properties.keySet()) { 
      Object value = properties.get(key);
      
      
      if (value instanceof String ||
          value instanceof Long ||
          value instanceof Double ||
          value instanceof Float ||
          value instanceof Integer ||
          value instanceof Boolean) {
        logger.trace(String.format("Assign property - %s:%s", key, value));
        propertyContainer.setProperty(key, value);
      } else {
        logger.trace(String.format("Assign property (toString) - %s:%s", key, value));
        propertyContainer.setProperty(key, value.toString());
      }
//      try {
//        propertyContainer.setProperty(key, value);
//      } catch (IllegalArgumentException e) {
//        if (value != null) {
//          propertyContainer.setProperty(key, value.toString());
//        }
//      }
    }
  }

  public static String resolvePropertyMajorLabel(Set<String> labels) {
    Set<String> labels_ = new HashSet<> (labels);
    labels_.remove(GlobalLabel.MetaboliteProperty.toString());
    labels_.remove(GlobalLabel.ReactionProperty.toString());

    if (labels_.size() > 1) logger.warn("Multiple labels " + labels_);

    if (labels_.isEmpty()) return null;

    return labels_.iterator().next();
  }

  @Deprecated
  public static List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> getPropertyEntities(Node node) {
    //		System.out.println("--->");
    List<Pair<GraphPropertyEntity, GraphRelationshipEntity>> propetyList = new ArrayList<> ();
    for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
      Node other = relationship.getOtherNode(node);

      if (other.hasLabel(GlobalLabel.MetaboliteProperty) || other.hasLabel(GlobalLabel.ReactionProperty)) {

        //				System.out.println(relationship.getType());

        GraphPropertyEntity graphPropertyEntity = new GraphPropertyEntity(); 
        //						new GraphPropertyEntity(Neo4jUtils.getPropertiesMap(other));
        for (Label label : other.getLabels()) {
          graphPropertyEntity.addLabel(label.toString());
        }

        graphPropertyEntity.setMajorLabel(resolvePropertyMajorLabel(graphPropertyEntity.getLabels()));

        GraphRelationshipEntity graphRelationshipEntity =
            new GraphRelationshipEntity();
        graphRelationshipEntity.setProperties(Neo4jUtils.getPropertiesMap(relationship));

        Pair<GraphPropertyEntity, GraphRelationshipEntity> pair = 
            new ImmutablePair<>(graphPropertyEntity, graphRelationshipEntity);

        propetyList.add(pair);

      }
    }

    return propetyList;
    //		System.out.println("<---");
    //		for (Relationship relationship : node.getRelationships(Direction.INCOMING)) {
    //			System.out.println(relationship.getType());
    //		}
    //		System.out.println("<-->");
    //		for (Relationship relationship : node.getRelationships(Direction.BOTH)) {
    //			System.out.println(relationship.getType());
    //		}
  }

  public static List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> getCrossreferences(Node node) {

    List<Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity>> proxyEntities = new ArrayList<> ();

    for (Relationship relationship : node.getRelationships(
        MetaboliteRelationshipType.has_crossreference_to,
        Direction.OUTGOING)) {
      Node other = relationship.getOtherNode(node);

      //			System.out.println(relationship.getType());

      GraphMetaboliteProxyEntity proxyEntity = new GraphMetaboliteProxyEntity();
      proxyEntity.setProperties(Neo4jUtils.getPropertiesMap(other));
      proxyEntity.setMajorLabel((String) proxyEntity.getProperty("major-label", null));
      GraphRelationshipEntity relationshipEntity = new GraphRelationshipEntity();
      relationshipEntity.setMajorLabel(relationship.getType().toString());
      relationshipEntity.setProperties(Neo4jUtils.getPropertiesMap(relationship));

      Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> pair = 
          new ImmutablePair<> (proxyEntity, relationshipEntity);
      proxyEntities.add(pair);
    }

    return proxyEntities;
  }

  @Deprecated
  public static void printNode(Node node) {
    logger.warn("not implemented");
//    String header = String.format("[%d]%s", node.getId(), IteratorUtil.asCollection(node.getLabels()));
//    System.out.println(header);
//    System.out.println(getPropertiesMap(node));
//
//    for (Relationship relationship : node.getRelationships()) {
//      System.out.println("================" + relationship.getType());
//      Node other = relationship.getOtherNode(node);
//      System.out.println(getPropertiesMap(relationship));
//      String header_ = String.format("[%d]%s", other.getId(), IteratorUtil.asCollection(other.getLabels()));
//      System.out.println(header_);
//      System.out.println(getPropertiesMap(other));
//    }
  }

  public static void applyProperties(Node node, Map<String, Object> properties) {
    for (String key : properties.keySet()) {
      node.setProperty(key, properties.get(key));
    }
  }
  
  public static Set<Node> findNodes(Label label, String key, Object value, 
                             GraphDatabaseService graphDatabaseService) {
    Set<Node> result = new HashSet<> ();
    
    ResourceIterator<Node> ri = graphDatabaseService.findNodes(label, key, value);
    while (ri.hasNext()) {
      result.add(ri.next());
    }
    
    return result;
  }

  @Deprecated
  public static Node mergeNode(Label label, String key, Object value, GraphDatabaseService graphDatabaseService) {
    Node node = null;
    for (Node res : findNodes(label, key, value, graphDatabaseService)) {
      node = res;
      System.out.println(res);
    }

    if (node == null) {
      node = graphDatabaseService.createNode();
      node.setProperty(key, value);
      node.addLabel(label);
    }

    return node;
  }

  @Deprecated
  public static Node mergeNode(String label, String key, Object value, GraphDatabaseService graphDatabaseService) {
    
    return mergeNode(DynamicLabel.label(label), key, value, graphDatabaseService);
  }

  public static Node getUniqueResult(
      ResourceIterable<Node> findNodesByLabelAndProperty) {
    Node node = null;

    for (Node node_ : findNodesByLabelAndProperty) {
      if (node != null) {
        logger.warn("Resource not unique");
      }
      node = node_;
    }

    return node;
  }
  
  public static Node getUniqueResult(
      ResourceIterator<Node> findNodesByLabelAndProperty) {
    Node node = null;
    
    while (findNodesByLabelAndProperty.hasNext()) { 
      if (node != null) {
        logger.warn("Resource not unique");
      }
      node = findNodesByLabelAndProperty.next(); 
    }

    return node;
  }
  
  public static Node getUniqueResult(
      Collection<Node> findNodesByLabelAndProperty) {
    Node node = null;

    for (Node node_ : findNodesByLabelAndProperty) {
      if (node != null) {
        logger.warn("Resource not unique");
      }
      node = node_;
    }

    return node;
  }

  public static Node getExecutionResultGetSingle(String column, Result executionResult) {
    if (executionResult == null) return null;
    
    ResourceIterator<Object> ri = executionResult.columnAs(column);
    
    Node node = null;
    while(ri.hasNext()) {
      Object object = ri.next();
      if (node != null) logger.warn("Integrity failure. Not unique result.");
      node = (Node) object;
    }
    return node;
  }

  public static Node collectUniqueNodeRelationshipNodes(Node node, RelationshipType...relationshipTypes) {
    Set<Node> nodes = collectNodeRelationshipNodes(node, relationshipTypes);
    if (nodes.isEmpty()) return null;

    if (nodes.size() > 1) logger.warn(String.format("Relationships not unique for %s", node));

    return nodes.iterator().next();
  }



  public static Node mergeUniqueNode(Label label, String key, Object value, 
                                     GraphDatabaseService graphDatabaseService) {
    String query = String.format("MERGE (n:%s {%s:{%s}}) "
        + "ON CREATE SET n.created_at = timestamp(), n.updated_at = timestamp() "
        + "ON MATCH SET n.updated_at = timestamp() RETURN n", 
        label, key, key);
    Map<String, Object> params = new HashMap<> ();
    params.put(key, value);

    logger.trace(String.format("Cypher: %s - %s", query, params));
    Node node = getExecutionResultGetSingle("n", graphDatabaseService.execute(query, params));
    return node;
  }

  public static Node getOrCreateNode(Label label,
      String key, Object value, GraphDatabaseService service) {
    String query = String.format(
        "MERGE (n:%s {%s:{%s}}) " + 
            "ON CREATE SET n.created_at=timestamp(), n.updated_at=timestamp() " + 
            "ON MATCH SET n.updated_at=timestamp() RETURN n", 
            label, key, key);
    logger.trace("Query: " + query);
    Map<String, Object> params = new HashMap<> ();
    params.put(key, value);
    Node node = getExecutionResultGetSingle("n", service.execute(query, params));

    return node;
  }

  /**
   * Deletes all relationships of a given node
   * @param node
   * @return the number of relationships deleted
   */
  public static int deleteAllRelationships(Node node) {
    int i = 0;
    for (Relationship relationship : node.getRelationships()) {
      relationship.delete();
      i++;
    }
    return i;
  }


  /**
   * 
   * @param src Node to move
   * @param dst Node 
   * @return number of relationships moved
   */
  public static int joinNodes(Node src, Node dst) {
    int movedEdges = 0;
    for (Relationship r : src.getRelationships()) {
      if (r.getStartNode().getId() == src.getId()) {
        Relationship r_ = dst.createRelationshipTo(r.getEndNode(), r.getType());
        Neo4jUtils.setPropertiesMap(Neo4jUtils.getPropertiesMap(r), r_);
      } else {
        Relationship r_ = r.getEndNode().createRelationshipTo(dst, r.getType());
        Neo4jUtils.setPropertiesMap(Neo4jUtils.getPropertiesMap(r), r_);
      }
      movedEdges++;
      r.delete();
    }

    return movedEdges;
  }

  public static boolean exitsRelationshipBetween(Node node1,
      Node node2, Direction direction) {
    for (Relationship r : node1.getRelationships(direction)) {
      Node other = r.getOtherNode(node1);
      if (other.getId() == node2.getId()) {
        return true;
      }
    }

    return false;
  }
  
  public static boolean exitsRelationshipBetween(Node node1,
      Node node2, Direction direction, RelationshipType type) {
    for (Relationship r : node1.getRelationships(type, direction)) {
      Node other = r.getOtherNode(node1);
      if (other.getId() == node2.getId()) {
        return true;
      }
    }

    return false;
  }

  public static Relationship getRelationshipBetween(Node node1,
      Node node2, Direction direction) {
    for (Relationship r : node1.getRelationships(direction)) {
      Node other = r.getOtherNode(node1);
      if (other.getId() == node2.getId()) {
        return r;
      }
    }

    return null;
  }

  public static boolean isConnected(Node node1, Node node2) {
    return exitsRelationshipBetween(node1, node2, Direction.BOTH);
  }

  public static Node getSingleRelationshipNode(Node node, RelationshipType type) {
    
    Relationship relationship = node.getSingleRelationship(type, Direction.BOTH);
    if (relationship == null) {
      return null;
    }
    
    return relationship.getOtherNode(node);
  }

  /**
   * Gets entity node
   * @param label
   * @param entry
   * @param db
   * @return
   */
  public static Node getNodeByEntry(Label label, String entry, GraphDatabaseService db) {
    Node node = getUniqueResult(
        findNodes(label, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, entry, db));
    return node;
  }
  
  /**
   * Gets property node
   * @param label
   * @param entry
   * @param db
   * @return
   */
  public static Node getNodeByKey(Label label, String entry, GraphDatabaseService db) {
    Node node = getUniqueResult(
        findNodes(label, Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT, entry, db));
    return node;
  }

  public static Label buildNamespace(String ns) {
    return DynamicLabel.label(ns);
  }

  public static boolean isMetaboliteDatabase(String label) {
    try {
      MetaboliteMajorLabel.valueOf(label);
      logger.trace(label + " is a Metabolite Database");
      return true;
    } catch (IllegalArgumentException e) {
      logger.trace(label + " is not Metabolite Database - " + e.getMessage());
    }
    
    return false;
  }
  
  public static<T extends Enum<T>> boolean isReactionDatabase(String str, Class<T> enumType) {
    try {
      ReactionMajorLabel.valueOf(str);
      logger.trace(str + " is a Reaction Database");
      return true;
    } catch (IllegalArgumentException e) {
      logger.trace(str + " is not Reaction Database - " + e.getMessage());
    }
    
    return false;
  }
  
  public static<T extends Enum<T>> boolean isDatabase(String str, Class<T> enumType) {
    try {
      Enum.valueOf(enumType, str);
      logger.trace(str + " is a Database");
      return true;
    } catch (IllegalArgumentException e) {
      logger.trace(str + " is not Database - " + e.getMessage());
    }
    
    return false;
  }

  public static Map<String, Integer> countLinkType(Node node) {
    return countLinkType(node, Direction.BOTH);
  }
  
  public static Map<String, Integer> countLinkType(Node node, Direction dir) {
    Map<String, Integer> count = new HashMap<> ();
    for (Relationship r : node.getRelationships(dir)) {
      CollectionUtils.increaseCount(count, r.getType().name(), 1);
    }
    return count;
  }
}
