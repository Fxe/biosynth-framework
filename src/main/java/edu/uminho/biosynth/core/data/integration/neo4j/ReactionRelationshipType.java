package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum ReactionRelationshipType implements RelationshipType {
	HasName, HasCrossreferenceTo, InstanceOf, HasECNumber, Stoichiometry, Left, Right, InPathway,
	InOrthology, InEnzymaticReaction
}
