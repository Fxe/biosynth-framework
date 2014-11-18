package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

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
}
