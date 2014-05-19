package edu.uminho.biosynth.core.data.integration.neo4j;

public enum ReactionRelationshipType {
	HasName, HasCrossreferenceTo, InstanceOf, HasECNumber, Stoichiometry, Left, Right, InPathway,
	InOrthology, InEnzymaticReaction
}
