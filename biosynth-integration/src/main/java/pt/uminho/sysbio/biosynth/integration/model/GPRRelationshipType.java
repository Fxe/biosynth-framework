package pt.uminho.sysbio.biosynth.integration.model;

import org.neo4j.graphdb.RelationshipType;

public enum GPRRelationshipType implements RelationshipType{
	//
	has_gpr,
	has_logical_operator,
	has_leaf,
	has_gpr_gene
}
