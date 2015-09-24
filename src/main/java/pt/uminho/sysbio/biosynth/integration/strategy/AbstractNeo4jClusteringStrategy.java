package pt.uminho.sysbio.biosynth.integration.strategy;

import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import edu.uminho.biosynth.core.data.integration.chimera.strategy.ClusteringStrategy;

public abstract class AbstractNeo4jClusteringStrategy implements ClusteringStrategy {

	protected GraphDatabaseService db;
	
	protected Label initialNodeLabel;
	
	protected Node initialNode;
	
	public Node getInitialNode() { return initialNode;}
	public void setInitialNode(Node initialNode) { this.initialNode = initialNode;}

	public GraphDatabaseService getDb() { return db;}
	public void setDb(GraphDatabaseService db) { this.db = db;}
	
	public AbstractNeo4jClusteringStrategy(GraphDatabaseService graphDatabaseService) {
		this.db = graphDatabaseService;
	}
	
	@Override
	public void setInitialNode(Long id) {
		this.initialNode = db.getNodeById(id);
		if (!this.initialNode.hasLabel(initialNodeLabel)) {
			throw new RuntimeException(String.format("Invalid initial node %s. Expected %s", Neo4jUtils.getLabels(initialNode), initialNodeLabel));
		}
	}

	@Override
	abstract public Set<Long> execute();
}
