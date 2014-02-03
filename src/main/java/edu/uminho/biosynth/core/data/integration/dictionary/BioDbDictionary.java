package edu.uminho.biosynth.core.data.integration.dictionary;

import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;

public class BioDbDictionary {
	public static Map<String, String> getDbDictionary() {
		Map<String, String> dictionary = new HashMap<> ();
		
		//BioCyc
		dictionary.put("METACYC", "BIOCYC");
		
		//KEGG
		dictionary.put("LIGAND-CPD", "KEGG");
		
		return dictionary;
	}
	
	public static String getDbReferenceKey(Class<?> entityType) {
		if (entityType.getClass().equals(BiggMetaboliteEntity.class)) {
			return "BIGG";
		}
		
		return null;
	}
}
