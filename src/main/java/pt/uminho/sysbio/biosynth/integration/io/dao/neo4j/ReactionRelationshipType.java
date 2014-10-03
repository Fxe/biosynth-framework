package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum ReactionRelationshipType implements RelationshipType {
	HasName, HasCrossreferenceTo, InstanceOf, HasECNumber, Stoichiometry, Left, Right, InPathway,
	InOrthology, InEnzymaticReaction
}
