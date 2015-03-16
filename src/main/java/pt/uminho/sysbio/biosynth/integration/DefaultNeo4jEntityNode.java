package pt.uminho.sysbio.biosynth.integration;

import org.neo4j.graphdb.Label;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;

public class DefaultNeo4jEntityNode extends AbstractGraphNodeEntity {

	private static final long serialVersionUID = 1L;

	public DefaultNeo4jEntityNode(Label constraintLabel, boolean proxy) {
		this.uniqueKey = Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT;
		this.setMajorLabel(constraintLabel.toString());
		this.properties.put(Neo4jDefinitions.PROXY_PROPERTY, proxy);
	}
	
	public boolean isProxy() {
		return (boolean) this.properties.get(Neo4jDefinitions.PROXY_PROPERTY);
	}
	
	public void setProxy(boolean proxy) {
		this.properties.put(Neo4jDefinitions.PROXY_PROPERTY, proxy);
	}
}
