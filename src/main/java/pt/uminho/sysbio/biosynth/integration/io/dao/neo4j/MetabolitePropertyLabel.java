package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum MetabolitePropertyLabel implements Label {
	MolecularFormula, Name, SMILES, Charge, CanSMILES, IsotopeFormula,
	InChI, FIKHB, SIKHB
}
