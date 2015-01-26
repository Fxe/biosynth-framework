package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.Neo4jNode;

public interface Neo4jSuperDao {
	
	public Map<Set<String>, Set<Long>> superHeavyMethod();
	public Neo4jNode getAnyNode(long id);
	public Neo4jNode getMetaboliteNode(long id);
	public Neo4jNode getReactionNode(long id);
}
