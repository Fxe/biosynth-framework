package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities used to perform several Neo4j operations 
 * specific to integration context.
 * 
 * @author Filipe
 */
public class Neo4jUtilsIntegration {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Neo4jUtilsIntegration.class);
	
	public static void deleteCluster(Node clusterNode) {
		
	}
	
	public static void removeMembersFromCluster(Node clusterNode, Long[] members) {
		Set<Long> memberSet = new HashSet<> (Arrays.asList(members));
		
		for (Relationship relationship : clusterNode.getRelationships()) {
			Node other = relationship.getOtherNode(clusterNode);
			Long eid = other.getId();
			if (memberSet.contains(eid)) {
				LOGGER.debug(String.format("Delete: [%d]-[%d]-[%d]", clusterNode.getId(), relationship.getId(), eid));
			}
		}
//		Map<Long, Set<Relationship>> memberRelationshipMap = new HashMap<> ();
//		for (Long member : members) memberRelationshipMap.put(member, new HashSet<Relationship> ());
//		
//		for (Relationship relationship :clusterNode.getRelationships()) {
//			Node other = relationship.getOtherNode(clusterNode);
//			Long eid = other.getId();
//			if (memberRelationshipMap.containsKey(eid)) {
//				memberRelationshipMap.get(eid).add(relationship);
//			}
//		}
		
//		for (Long eid : memberRelationshipMap.keySet()) {
//			
//		}
	}
}
