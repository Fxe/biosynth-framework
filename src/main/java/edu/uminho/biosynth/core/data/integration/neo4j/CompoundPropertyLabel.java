package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.graphdb.Label;

public enum CompoundPropertyLabel implements Label{
	Formula, Name, SMILES, InChI, Charge, CanSMILES
}
