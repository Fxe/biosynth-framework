package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum MetaboliteRelationshipType implements RelationshipType{
	HasMolecularFormula, 
	HasName, 
	HasCrossreferenceTo, 
	InstanceOf, 
	HasInChI, 
	HasCharge, 
	HasSMILES, 
	Isomorphic,
	FoundIn
}
