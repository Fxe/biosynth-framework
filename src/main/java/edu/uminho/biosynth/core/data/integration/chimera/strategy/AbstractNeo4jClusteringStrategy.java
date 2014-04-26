package edu.uminho.biosynth.core.data.integration.chimera.strategy;

import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNeo4jClusteringStrategy implements ClusteringStrategy {

	@Autowired
	protected GraphDatabaseService db;
	
	protected Label initialNodeLabel;
	
	protected Node initialNode;
	
	public Node getInitialNode() { return initialNode;}
	public void setInitialNode(Node initialNode) { this.initialNode = initialNode;}

	public GraphDatabaseService getDb() { return db;}
	public void setDb(GraphDatabaseService db) { this.db = db;}
	
	@Override
	public void setInitialNode(Long id) {
		this.initialNode = db.getNodeById(id);
		if (!this.initialNode.hasLabel(initialNodeLabel)) {
			throw new RuntimeException("Invalid initial node");
		}
	}

	@Override
	abstract public Set<Long> execute();
}
