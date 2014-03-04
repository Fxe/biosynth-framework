package edu.uminho.biosynth.core.data.integration.dictionary;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;

public class BioDbDictionary {
	
	public static String translateDatabase(String db) {
		if (getDbDictionary().containsKey(db)) {
			return getDbDictionary().get(db);
		}
		System.err.println(db + " not found in translation dictionary");
		return "NOTFOUND";
	}
	
	public static Map<String, String> getDbDictionary() {
		/**
		 * Some Rules:
		 *    Labels cannot start with Numbers or have symbols
		 *    Only characters and '_'
		 */
		Map<String, String> dictionary = new HashMap<> ();
		
		//BioCyc
		dictionary.put("METACYC", "BioCyc:MetaCyc");
		
		dictionary.put("LIGAND-CPD", "KEGG");
		dictionary.put("KEGG", "KEGG");
		//KEGG
		dictionary.put("JCGGDB", "JCGGDB");
		dictionary.put("GlycomeDB", "GlycomeDB");
		dictionary.put("CCSD", "CCSD");
		dictionary.put("PUBCHEM", "PubChem");
		dictionary.put("PubChem", "PubChem");
		dictionary.put("PDB-CCD", "PDB");
		dictionary.put("ChEBI", "ChEBI");
		dictionary.put("CHEBI", "ChEBI");
		dictionary.put("CAS", "CAS");
		dictionary.put("NIKKAJI", "NIKKAJI");
		dictionary.put("3DMET", "MET3D");
		
		return dictionary;
	}
	
	public static String getDbReferenceKey(Class<?> entityType) {
		if (entityType.getClass().equals(BiggMetaboliteEntity.class)) {
			return "BIGG";
		}
		
		return null;
	}
}
