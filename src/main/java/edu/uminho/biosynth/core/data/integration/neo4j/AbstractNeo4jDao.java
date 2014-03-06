package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public abstract class AbstractNeo4jDao<T> {
	
	protected GraphDatabaseService graphdb;
	protected ExecutionEngine engine;
	
	
	
	public AbstractNeo4jDao(GraphDatabaseService graphdb) {
		this.graphdb = graphdb;
		this.engine = new ExecutionEngine(graphdb);
	}
	
	public GraphDatabaseService getGraphdb() {
		return graphdb;
	}

	public void setGraphdb(GraphDatabaseService graphdb) {
		this.graphdb = graphdb;
	}
	
	protected abstract T nodeToObject(Node node);
	
	
}
