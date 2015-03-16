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
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.helpers.collection.IteratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;

/**
 * Utilities used to perform several Neo4j operations.
 * 
 * @author Filipe
 */
public class Neo4jUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jUtils.class);
	
	public static Set<Long> collectNodes(Node node) {
		return null;
	}
	
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
		return IteratorUtil.asSet(node.getLabels());
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
			LOGGER.trace(String.format("Assign property - %s:%s", key, value));
			propertyContainer.setProperty(key, value);
		}
	}
	
	public static String resolvePropertyMajorLabel(Set<String> labels) {
		Set<String> labels_ = new HashSet<> (labels);
		labels_.remove(GlobalLabel.MetaboliteProperty.toString());
		labels_.remove(GlobalLabel.ReactionProperty.toString());
		
		if (labels_.size() > 1) LOGGER.warn("Multiple labels " + labels_);
		
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
	
	public static void printNode(Node node) {
		String header = String.format("[%d]%s", node.getId(), IteratorUtil.asCollection(node.getLabels()));
		System.out.println(header);
		System.out.println(getPropertiesMap(node));
		
		for (Relationship relationship : node.getRelationships()) {
			System.out.println("================" + relationship.getType());
			Node other = relationship.getOtherNode(node);
			System.out.println(getPropertiesMap(relationship));
			String header_ = String.format("[%d]%s", other.getId(), IteratorUtil.asCollection(other.getLabels()));
			System.out.println(header_);
			System.out.println(getPropertiesMap(other));
		}
	}

	public static void applyProperties(Node node, Map<String, Object> properties) {
		for (String key : properties.keySet()) {
			node.setProperty(key, properties.get(key));
		}
	}

	@Deprecated
	public static Node mergeNode(Label label, String key, Object value, GraphDatabaseService graphDatabaseService) {
		Node node = null;
		for (Node res : graphDatabaseService.findNodesByLabelAndProperty(label, key, value)) {
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
				LOGGER.warn("Resource not unique");
			}
			node = node_;
		}
		
		return node;
	}
	
	public static Node getExecutionResultGetSingle(String column, ExecutionResult executionResult) {
		if (executionResult == null) return null;
		
		Node node = null;
		for (Object object : IteratorUtil.asList(executionResult.columnAs(column))) {
			if (node != null) LOGGER.warn("Integrity failure. Not unique result.");
			node = (Node) object;
		}
		return node;
	}

	public static Node collectUniqueNodeRelationshipNodes(Node node, RelationshipType...relationshipTypes) {
		Set<Node> nodes = collectNodeRelationshipNodes(node, relationshipTypes);
		if (nodes.isEmpty()) return null;
		
		if (nodes.size() > 1) LOGGER.warn(String.format("Relationships not unique for %s", node));
		
		return nodes.iterator().next();
	}
	

	
	public static Node mergeUniqueNode(Label label, String key, Object value, ExecutionEngine ee) {
		String query = String.format("MERGE (n:%s {%s:{%s}}) "
				+ "ON CREATE SET n.created_at = timestamp(), n.updated_at = timestamp() "
				+ "ON MATCH SET n.updated_at = timestamp() RETURN n", 
				label, key, key);
		Map<String, Object> params = new HashMap<> ();
		params.put(key, value);
		
		LOGGER.trace(String.format("Cypher: %s - %s", query, params));
		Node node = getExecutionResultGetSingle("n", ee.execute(query, params));
		return node;
	}

	public static Node getOrCreateNode(Label label,
			String key, Object value, ExecutionEngine executionEngine) {
		String query = String.format(
				"MERGE (n:%s {%s:{%s}}) " + 
				"ON CREATE SET n.created_at=timestamp(), n.updated_at=timestamp() " + 
				"ON MATCH SET n.updated_at=timestamp() RETURN n", 
				label, key, key);
		LOGGER.trace("Query: " + query);
		Map<String, Object> params = new HashMap<> ();
		params.put(key, value);
		Node node = Neo4jUtils.getExecutionResultGetSingle("n", executionEngine.execute(query, params));
		
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


}
