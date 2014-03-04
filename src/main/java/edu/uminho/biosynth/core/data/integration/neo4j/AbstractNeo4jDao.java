package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;

public class AbstractNeo4jDao {
	
	protected GraphDatabaseService graphdb;
	
	public GraphDatabaseService getGraphdb() {
		return graphdb;
	}

	public void setGraphdb(GraphDatabaseService graphdb) {
		this.graphdb = graphdb;
	}
}
