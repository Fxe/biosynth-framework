package edu.uminho.biosynth.core.data.integration.dictionary;

import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.mnx.MnxMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.SeedMetaboliteEntity;

public class BioDbEntityDictionary {

	public static Class<?> getGenericMetaboliteFromString(String value) {
		Class<?> clazz;
		
		switch (value.toUpperCase()) {
			case "KEGG":
				clazz = KeggCompoundMetaboliteEntity.class;
				break;
			case "BIOCYC":
				clazz = BioCycMetaboliteEntity.class;
				break;
			case "METANETX":
				clazz = MnxMetaboliteEntity.class;
				break;
			case "BIGG":
				clazz = BiggMetaboliteEntity.class;
				break;
			case "SEED":
				clazz = SeedMetaboliteEntity.class;
				break;
			case "CHEBI":
				clazz = ChebiMetaboliteEntity.class;
				break;
			case "CAS":
				clazz = java.lang.String.class;
				break;
//			case "PUBCHEM":
//				clazz = PubchemMetaboliteEntity.class;
//				break;
//			case "BRENDA":
//				clazz = BrendaMetaboliteEntity.class;
//				break;
//			case "REACTOME":
//				clazz = ReactomeMetaboliteEntity.class;
//				break;
			default:
				clazz = null;
				break;
		}
		return clazz;
	}
}
