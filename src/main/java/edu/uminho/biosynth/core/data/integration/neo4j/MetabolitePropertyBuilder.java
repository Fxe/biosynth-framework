package edu.uminho.biosynth.core.data.integration.neo4j;

public class MetabolitePropertyBuilder {
	
	private static final String NAME_LABEL = "CompoundName";
	private static final String FORMULA_LABEL = "MolecularFormula";
	private static final String INCHI_LABEL = "InChI";
	private static final String SMILES_LABEL = "SMILES";
	private static final String CHARGE_LABEL = "Charge";
	
	public static CentralDataMetabolitePropertyEntity buildFormula(String formula) {
		String uniqueKey = "formula";
		CentralDataMetabolitePropertyEntity propertyEntity = new CentralDataMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(FORMULA_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, formula);
		return propertyEntity;
	}
	
	public static CentralDataMetabolitePropertyEntity buildInchi(String inchi) {
		String uniqueKey = "inchi";
		CentralDataMetabolitePropertyEntity propertyEntity = new CentralDataMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(INCHI_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, inchi);
		return propertyEntity;
	}
	
	public static CentralDataMetabolitePropertyEntity buildSmiles(String smiles) {
		String uniqueKey = "smiles";
		CentralDataMetabolitePropertyEntity propertyEntity = new CentralDataMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(SMILES_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, smiles);
		return propertyEntity;
	}
	
	public static CentralDataMetabolitePropertyEntity buildCharge(Integer charge) {
		String uniqueKey = "charge";
		CentralDataMetabolitePropertyEntity propertyEntity = new CentralDataMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(CHARGE_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, charge);
		return propertyEntity;
	}
	
	public static CentralDataMetabolitePropertyEntity buildName(String name) {
		String uniqueKey = "name";
		CentralDataMetabolitePropertyEntity propertyEntity = new CentralDataMetabolitePropertyEntity();
		propertyEntity.setMajorLabel(NAME_LABEL);
		propertyEntity.setUniqueKey(uniqueKey);
		propertyEntity.getProperties().put(uniqueKey, name);
		return propertyEntity;
	}
}
