package pt.uminho.sysbio.biosynth.integration.curation;

import org.neo4j.graphdb.RelationshipType;

public enum CurationRelationship implements RelationshipType {
	CurationOperation,
	CurationAccept,
	CurationReject,
	CurationEqualSet,
	CurationNonEqualSet, Curation,
}
