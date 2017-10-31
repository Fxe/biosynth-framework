package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum MetabolitePropertyLabel implements Label {
	MolecularFormula, 
	Name, IUPACName, 
	SMILES, Charge, CanSMILES, IsotopeFormula, UniversalSMILES,
	MDLMolFile, MDLSdFile, ChemicalMarkupLanguage,
	InChI, InChIKey, FIKHB, SIKHB
	;
	
	public static MetabolitePropertyLabel toMetaboliteProperty(String str) {
		switch (str.toLowerCase()) {
			case "name": return MetabolitePropertyLabel.Name;
			case "molecularformula": 
			case "molecular formula": 
			case "formula": 
				return MetabolitePropertyLabel.MolecularFormula;
			case "charge": return MetabolitePropertyLabel.Charge;
			case "smiles": return MetabolitePropertyLabel.SMILES;
			case "inchi": return MetabolitePropertyLabel.InChI;
			case "cml":
			case "chemicalmarkuplanguage": 
				return MetabolitePropertyLabel.ChemicalMarkupLanguage;
			case "molfile":
			case "mol":
			case "mdlmol":
			case "mdlmolfile": 
				return MetabolitePropertyLabel.MDLMolFile;
			default: return null;
		}
	}
}
