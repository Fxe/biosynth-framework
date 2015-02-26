package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum MetabolicModelRelationshipType implements RelationshipType {
	has_specie,
	has_reaction,
	has_compartment, in_compartment, 
	left_component, right_component
}
