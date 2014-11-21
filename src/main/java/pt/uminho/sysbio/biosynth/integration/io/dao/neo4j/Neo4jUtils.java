package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.IteratorUtil;

public class Neo4jUtils {
	
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
	
	public static Set<Long> collectNodeRelationshipNodeIds(Node node) {
		Set<Long> nodes = new HashSet<> ();
		
		for (Relationship relationship : node.getRelationships()) {
			Node other = relationship.getOtherNode(node);
			nodes.add(other.getId());
		}
		
		return nodes;
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
}
