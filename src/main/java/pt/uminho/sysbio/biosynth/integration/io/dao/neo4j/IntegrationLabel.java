package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum IntegrationLabel implements Label {
	IntegrationSet,
	MetaboliteCluster, ReactionCluster,
	MetaboliteMember, ReactionMember
}
