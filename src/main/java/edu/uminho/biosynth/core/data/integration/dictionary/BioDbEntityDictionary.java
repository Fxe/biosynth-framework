package edu.uminho.biosynth.core.data.integration.dictionary;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.brenda.BrendaMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.chebi.ChEbiMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.mnx.MnxMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.pubchem.PubchemMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.reactome.ReactomeMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.seed.SeedMetaboliteEntity;

public class BioDbEntityDictionary {

	public static Class<?> getGenericMetaboliteFromString(String value) {
		Class<?> clazz;
		
		switch (value.toUpperCase()) {
			case "KEGG":
				clazz = KeggMetaboliteEntity.class;
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
				clazz = ChEbiMetaboliteEntity.class;
				break;
			case "CAS":
				clazz = java.lang.String.class;
				break;
			case "PUBCHEM":
				clazz = PubchemMetaboliteEntity.class;
				break;
			case "BRENDA":
				clazz = BrendaMetaboliteEntity.class;
				break;
			case "REACTOME":
				clazz = ReactomeMetaboliteEntity.class;
				break;
			default:
				clazz = null;
				break;
		}
		return clazz;
	}
}
