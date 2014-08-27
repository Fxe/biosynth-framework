package edu.uminho.biosynth.core.data.integration.neo4j;

import pt.uminho.sysbio.biosynth.integration.CentralMetabolitePropertyEntity;

public class MetabolitePropertyBuilder {
	
	private static final String NAME_LABEL = "CompoundName";
	private static final String FORMULA_LABEL = "MolecularFormula";
	private static final String INCHI_LABEL = "InChI";
	private static final String SMILES_LABEL = "SMILES";
	private static final String CHARGE_LABEL = "Charge";
	
	public static CentralMetabolitePropertyEntity buildFormula(String formula) {
		String uniqueKey = "formula";
		CentralMetabolitePropertyEntity propertyEntity = new CentralMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(FORMULA_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, formula);
		return propertyEntity;
	}
	
	public static CentralMetabolitePropertyEntity buildInchi(String inchi) {
		String uniqueKey = "inchi";
		CentralMetabolitePropertyEntity propertyEntity = new CentralMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(INCHI_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, inchi);
		return propertyEntity;
	}
	
	public static CentralMetabolitePropertyEntity buildSmiles(String smiles) {
		String uniqueKey = "smiles";
		CentralMetabolitePropertyEntity propertyEntity = new CentralMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(SMILES_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, smiles);
		return propertyEntity;
	}
	
	public static CentralMetabolitePropertyEntity buildCharge(Integer charge) {
		String uniqueKey = "charge";
		CentralMetabolitePropertyEntity propertyEntity = new CentralMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(CHARGE_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, charge);
		return propertyEntity;
	}
	
	public static CentralMetabolitePropertyEntity buildName(String name) {
		String uniqueKey = "name";
		CentralMetabolitePropertyEntity propertyEntity = new CentralMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(NAME_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, name);
		return propertyEntity;
	}
}
