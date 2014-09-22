package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum MetabolitePropertyLabel implements Label{
	MolecularFormula, Name, SMILES, InChI, Charge, CanSMILES, IsotopeFormula
}
