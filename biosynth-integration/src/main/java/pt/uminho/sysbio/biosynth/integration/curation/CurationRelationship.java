package pt.uminho.sysbio.biosynth.integration.curation;

import org.neo4j.graphdb.RelationshipType;

public enum CurationRelationship implements RelationshipType {
	OPERATES_ON,
	CurationReject,
	CurationEqualSet,
	HAS_CURATION_OPERATION, PERFORMED_CURATION_OPERATION, 
	NOT_EQUAL, EQUAL,
}
