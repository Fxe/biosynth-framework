package pt.uminho.sysbio.biosynth.integration.io.dao;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;

public abstract class AbstractNeo4jDao {

	protected GraphDatabaseService graphDatabaseService;
	protected ExecutionEngine executionEngine;
	
	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}
	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
	}
	
	public ExecutionEngine getExecutionEngine() {
		return executionEngine;
	}
	public void setExecutionEngine(ExecutionEngine executionEngine) {
		this.executionEngine = executionEngine;
	}
}
