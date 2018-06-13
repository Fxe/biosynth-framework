package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum MetabolicModelRelationshipType implements RelationshipType {
    @Deprecated
	has_specie,
	has_metabolite_species,
	has_metabolite,
	@Deprecated
	has_reaction,
	has_model_reaction,
	has_subsystem,
	in_subsystem,
	has_model_compartment,
	@Deprecated
	has_compartment, 
	in_compartment, 
	left_component, right_component, 
	has_crossreference_to, 
	is_a, is_version_of,
	has_gpr_gene,
	has_gpr,
}
