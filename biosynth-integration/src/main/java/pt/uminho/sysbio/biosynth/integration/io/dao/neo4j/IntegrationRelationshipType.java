package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum IntegrationRelationshipType implements RelationshipType {
	IntegratedMetaboliteCluster, IntegratedReactionCluster, //old stuff
	has_integrated_model_reaction_cluster,
	has_integrated_model_specie_cluster,
	has_integrated_model_metabolite_cluster,
	Integrates, 
	has_meta_information, //CTR -> META
	has_integrated_metabolite, //ITG->EID
	has_integrated_reaction,   //ITG->EID
	has_integrated_model_reaction,
	has_integrated_model_specie,
	has_integrated_model_metabolite
}
