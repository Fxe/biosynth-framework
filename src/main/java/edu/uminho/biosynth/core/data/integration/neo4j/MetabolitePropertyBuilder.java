package edu.uminho.biosynth.core.data.integration.neo4j;

import pt.uminho.sysbio.biosynth.integration.GraphPropertyEntity;

public class MetabolitePropertyBuilder {
	
	private static final String NAME_LABEL = "CompoundName";
	private static final String FORMULA_LABEL = "MolecularFormula";
	private static final String INCHI_LABEL = "InChI";
	private static final String SMILES_LABEL = "SMILES";
	private static final String CHARGE_LABEL = "Charge";
	
	public static GraphPropertyEntity buildFormula(String formula) {
		String uniqueKey = "formula";
		GraphPropertyEntity propertyEntity = new GraphPropertyEntity(uniqueKey, formula);
		propertyEntity.setMajorLabel(FORMULA_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		return propertyEntity;
	}
	
	public static GraphPropertyEntity buildInchi(String inchi) {
		String uniqueKey = "inchi";
		GraphPropertyEntity propertyEntity = new GraphPropertyEntity(uniqueKey, inchi);
		propertyEntity.setMajorLabel(INCHI_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		return propertyEntity;
	}
	
	public static GraphPropertyEntity buildSmiles(String smiles) {
		String uniqueKey = "smiles";
		GraphPropertyEntity propertyEntity = new GraphPropertyEntity(uniqueKey, smiles);
		propertyEntity.setMajorLabel(SMILES_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		return propertyEntity;
	}
	
	public static GraphPropertyEntity buildCharge(Integer charge) {
		String uniqueKey = "charge";
		GraphPropertyEntity propertyEntity = new GraphPropertyEntity(uniqueKey, charge);
		propertyEntity.setMajorLabel(CHARGE_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		return propertyEntity;
	}
	
	public static GraphPropertyEntity buildName(String name) {
		String uniqueKey = "name";
		GraphPropertyEntity propertyEntity = new GraphPropertyEntity(uniqueKey, name);
		propertyEntity.setMajorLabel(NAME_LABEL);
		return propertyEntity;
	}
}
