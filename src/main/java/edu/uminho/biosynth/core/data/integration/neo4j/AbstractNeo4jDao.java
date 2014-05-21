package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public abstract class AbstractNeo4jDao<T> {
	
	protected GraphDatabaseService graphDatabaseService;
	protected ExecutionEngine executionEngine;
	
	public AbstractNeo4jDao() { }
	
	public AbstractNeo4jDao(GraphDatabaseService graphdb) {
		this.graphDatabaseService = graphdb;
		this.executionEngine = new ExecutionEngine(graphdb);
	}
	
	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}

	public void setGraphDatabaseService(GraphDatabaseService graphdb) {
		this.graphDatabaseService = graphdb;
		this.executionEngine = new ExecutionEngine(graphdb);
	}
	
	protected abstract T nodeToObject(Node node);
	
	
}
