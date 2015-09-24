package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;

@Deprecated
public abstract class AbstractNeo4jDao<T> {
	
//	private static Logger LOGGER =
	
	@Autowired
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
	
	protected void create(
			Node node, Label majorLabel, Object value, 
			RelationshipType relationshipType, Map<String, Object> relationshipProperties) {
		@SuppressWarnings("unused")
		boolean create = true;
		for (Node proxyNode : graphDatabaseService
				.findNodesByLabelAndProperty(majorLabel, "entry", value)) {
//			LOGGER.debug("Link To Node/Proxy " + proxyNode);
			create = false;
			
			//TODO: SET TO UPDATE
			Relationship relationship = node.createRelationshipTo(proxyNode, relationshipType);
			for (String key : relationshipProperties.keySet()) {
				relationship.setProperty(key, relationshipProperties.get(key));
			}
		}
	}
	
	
	protected abstract T nodeToObject(Node node);
}
