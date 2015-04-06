package pt.uminho.sysbio.biosynth.integration.io.dao;

import org.neo4j.graphdb.RelationshipType;

public enum Neo4jSignatureRelationship implements RelationshipType {
	has_signature,
	has_signature_set,
}
