package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum CompoundRelationshipType implements RelationshipType{
	HasFormula, HasName, HasCrossreferenceTo, InstanceOf, HasInChI, HasCharge, HasSMILES, Isomorphic
}
