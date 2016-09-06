package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum IntegrationNodeLabel implements Label {
	IntegrationSet,
	IntegratedCluster, IntegratedMember,
	MetaboliteClusterMetaProperty, ReactionClusterMetaProperty,
	
	MetaboliteCluster, 
	ReactionCluster,
	
	ModelReactionCluster,
	ModelMetaboliteCluster,
	ModelSpecieCluster,
	//quite useless use
	MetaboliteMember, ReactionMember, ModelReactionMember, ModelSpecieMember
}
