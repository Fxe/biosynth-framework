package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import pt.uminho.sysbio.biosynth.integration.Neo4jNode;

public interface Neo4jSuperDao {
	
	public GraphDatabaseService getGraphDatabaseService();
	public Map<Set<String>, Set<Long>> superHeavyMethod();
	public Neo4jNode getAnyNode(long id);
	public Neo4jNode getMetaboliteNode(long id);
	public Neo4jNode getReactionNode(long id);
	public Neo4jNode getAnyNodeLimit(long id, int limit);
	public Set<Long> findNodesByLabelAndProperty(String label, String key, Object value);
	public void delete(Node node);
	public boolean linkIfNotExists(long src, long dst, 
			RelationshipType relationshipType, Map<String, Object> properties);
	public boolean unlinkIfExists(long src, long dst);
	public String executeQuery(String query, Map<String, Object> params);
	public Object getNodeProperty(long nodeId, String property);
	
}
