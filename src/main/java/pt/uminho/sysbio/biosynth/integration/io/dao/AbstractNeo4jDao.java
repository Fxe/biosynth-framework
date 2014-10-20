package pt.uminho.sysbio.biosynth.integration.io.dao;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNeo4jDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNeo4jDao.class);
	
	protected final GraphDatabaseService graphDatabaseService;
	protected final ExecutionEngine executionEngine;
	
	public AbstractNeo4jDao(GraphDatabaseService graphDatabaseService) {
		this.graphDatabaseService = graphDatabaseService;
		this.executionEngine = new ExecutionEngine(graphDatabaseService);
		
		Transaction tx = graphDatabaseService.beginTx();
		LOGGER.info(String.format("%d", IteratorUtil.asList(GlobalGraphOperations.at(graphDatabaseService).getAllNodes()).size()));
		tx.failure();
		tx.close();
	}
	
	public GraphDatabaseService getGraphDatabaseService() {
		return graphDatabaseService;
	}
	
	public ExecutionEngine getExecutionEngine() {
		return executionEngine;
	}
//	public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) {
//		
//		Transaction tx = graphDatabaseService.beginTx();
//		LOGGER.info(String.format("%d", IteratorUtil.asList(GlobalGraphOperations.at(graphDatabaseService).getAllNodes()).size()));
//		tx.failure();
//		tx.close();
//		
//		this.graphDatabaseService = graphDatabaseService;
//		this.executionEngine = new ExecutionEngine(graphDatabaseService);
//	}
	
	
}
