package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum Neo4jLayoutRelationship implements RelationshipType {
  has_layout_node, has_edge,
  is_a, is_a_model_entity
}
