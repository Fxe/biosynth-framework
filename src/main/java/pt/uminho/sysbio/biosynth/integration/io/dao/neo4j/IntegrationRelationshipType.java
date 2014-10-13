package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum IntegrationRelationshipType implements RelationshipType {
	IntegratedMetaboliteCluster, IntegratedReactionCluster,
	Integrates
}
