package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum ReactionRelationshipType implements RelationshipType {
	has_name, 
	has_crossreference_to, 
	instance_of, 
	included_in,
	has_ec_number, stoichiometry, left_component, right_component, in_pathway,
	has_orthology, InEnzymaticReaction,
	has_gene,
	sub_instance,
	has_reaction_pair,
	has_modelseed_role,
//	Left, Right,
}
