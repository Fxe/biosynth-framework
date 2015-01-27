package pt.uminho.sysbio.biosynth.integration.curation;

import org.neo4j.graphdb.RelationshipType;

public enum CurationRelationship implements RelationshipType {
	OPERATES_ON,
	ACCEPT,
	CurationReject,
	CurationEqualSet,
	NOT_EQUAL, HAS_CURATION_OPERATION, PERFORMED_CURATION_OPERATION, SPLIT, EXCLUDE, UNION
}
