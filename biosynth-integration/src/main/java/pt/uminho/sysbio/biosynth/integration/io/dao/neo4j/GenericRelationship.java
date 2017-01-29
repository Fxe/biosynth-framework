package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum GenericRelationship implements RelationshipType {
  has_taxonomy,
}
