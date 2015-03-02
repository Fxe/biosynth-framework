package pt.uminho.sysbio.biosynth.integration.io.dao;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNeo4jDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jDao.class);
	
	protected final GraphDatabaseService graphDatabaseService;
	protected final ExecutionEngine executionEngine;
	
	public AbstractNeo4jDao(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		LOGGER.trace("Initialize ExecutionEngine");
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
	}
	
	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}
	
	public ExecutionEngine getExecutionEngine() {
		return executionEngine;
	}
}
