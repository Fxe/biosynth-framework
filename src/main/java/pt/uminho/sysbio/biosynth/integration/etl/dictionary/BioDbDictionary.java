package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CompoundNodeLabel;

public class BioDbDictionary {
	
	public static String translateDatabase(String db) {
		if (getDbDictionary().containsKey(db)) {
			return getDbDictionary().get(db);
		}
		
		System.err.println(String.format("[%s] not found in translation dictionary", db));
		return "NOTFOUND";
	}
	
	public static String translateToLabel(String db) {
		if (getDbLabel().containsKey(db)) {
			return getDbLabel().get(db);
		}
		System.err.println(String.format("[%s] not found in translation dictionary", db));
		return "NOTFOUND";
	}
	
	public static Map<String, String> getDbDictionary() {
		/**
		 * Some Rules:
		 *    Labels cannot start with Numbers or have symbols
		 *    Only characters and '_'
		 */
		Map<String, String> dictionary = new HashMap<> ();
		dictionary.put("NCI", "NCI");
		
		dictionary.put("Wikipedia", "Wikipedia");
		
		dictionary.put("LipidBank", "LipidBank");
		
		dictionary.put("lipidmaps", MetaboliteMajorLabel.LipidMAPS.toString());
		dictionary.put("LIPIDMAPS", MetaboliteMajorLabel.LipidMAPS.toString());
		dictionary.put("LIPID MAPS instance accession", MetaboliteMajorLabel.LipidMAPS.toString());
		
		dictionary.put("CHEMSPIDER", MetaboliteMajorLabel.ChemSpider.toString());
		dictionary.put("Chemspider accession", MetaboliteMajorLabel.ChemSpider.toString());
		dictionary.put("DrugBank accession", MetaboliteMajorLabel.DrugBank.toString());
		dictionary.put("biopath", "BioPath");
		dictionary.put("upa", "UniPathway");
		//PlantCyc
		dictionary.put("PLANTCYC:MAIZE", "PlantCyc:MaizeCyc");
		//BioCyc
		dictionary.put("KNAPSACK", MetaboliteMajorLabel.KNApSAcK.toString());
		dictionary.put("KNApSAcK", MetaboliteMajorLabel.KNApSAcK.toString());
		dictionary.put("BIOCYC:ARA", "BioCyc:AraCyc");
		dictionary.put("METACYC", MetaboliteMajorLabel.MetaCyc.toString());
		dictionary.put("metacyc", MetaboliteMajorLabel.MetaCyc.toString());
		dictionary.put("MetaCyc accession", MetaboliteMajorLabel.MetaCyc.toString());
		
		dictionary.put("BIGG", MetaboliteMajorLabel.BiGG.toString());
		dictionary.put("bigg", MetaboliteMajorLabel.BiGG.toString());
		dictionary.put("LIGAND-CPD", MetaboliteMajorLabel.LigandCompound.toString());
//		dictionary.put("KEGG", MetaboliteMajorLabel.KEGG.toString());
//		dictionary.put("kegg", MetaboliteMajorLabel.KEGG.toString());
		dictionary.put("KEGG", MetaboliteMajorLabel.LigandCompound.toString());
		dictionary.put("KEGG COMPOUND accession", MetaboliteMajorLabel.LigandCompound.toString());
		dictionary.put("KEGG DRUG accession", MetaboliteMajorLabel.LigandDrug.toString());
		dictionary.put("KEGG GLYCAN accession", MetaboliteMajorLabel.LigandGlycan.toString());
		//KEGG
		dictionary.put("JCGGDB", "JCGGDB");
		dictionary.put("GlycomeDB", "GlycomeDB");
		dictionary.put("CCSD", "CCSD");
		
		dictionary.put("PUBCHEM", MetaboliteMajorLabel.PubChemCompound.toString());
		dictionary.put("Pubchem accession", MetaboliteMajorLabel.PubChemCompound.toString());
		dictionary.put("PubChem", MetaboliteMajorLabel.PubChemCompound.toString());
		
		dictionary.put("PDB-CCD", "PDB");
		dictionary.put("chebi", MetaboliteMajorLabel.ChEBI.toString());
		dictionary.put("ChEBI", MetaboliteMajorLabel.ChEBI.toString());
		dictionary.put("CHEBI", MetaboliteMajorLabel.ChEBI.toString());
		dictionary.put("CAS", MetaboliteMajorLabel.CAS.toString());
		dictionary.put("CAS Registry Number", MetaboliteMajorLabel.CAS.toString());
		dictionary.put("NIKKAJI", "NIKKAJI");
		dictionary.put("3DMET", "MET3D");
		dictionary.put("seed", MetaboliteMajorLabel.Seed.toString());
		
		dictionary.put("hmdb", MetaboliteMajorLabel.HMDB.toString());
		dictionary.put("HMDB accession", MetaboliteMajorLabel.HMDB.toString());
		
		dictionary.put("reactome", MetaboliteMajorLabel.Reactome.toString());
		
		dictionary.put("BRENDA-COMPOUND", MetaboliteMajorLabel.BRENDA.toString());
		dictionary.put("brenda", MetaboliteMajorLabel.BRENDA.toString());
		
		dictionary.put("RHEA", ReactionMajorLabel.Rhea.toString());
		dictionary.put("LIGAND-RXN", ReactionMajorLabel.LigandReaction.toString());

		dictionary.put("UM-BBD-CPD", MetaboliteMajorLabel.EawagBBDCompound.toString());
		return dictionary;
	}
	
	public static Map<String, String> getDbLabel() {
		/**
		 * Some Rules:
		 *    Labels cannot start with Numbers or have symbols
		 *    Only characters and '_'
		 */
		Map<String, String> dictionary = new HashMap<> ();
		dictionary.put("biopath", CompoundNodeLabel.BioPath.toString());
		dictionary.put("upa", CompoundNodeLabel.UniPathway.toString());
		//PlantCyc
		dictionary.put("PLANTCYC:MAIZE", CompoundNodeLabel.MaizeCyc.toString());
		//BioCyc
		dictionary.put("KNApSAcK", CompoundNodeLabel.KNApSAcK.toString());
		dictionary.put("BIOCYC:ARA", CompoundNodeLabel.AraCyc.toString());
		dictionary.put("METACYC", CompoundNodeLabel.MetaCyc.toString());
		dictionary.put("metacyc", CompoundNodeLabel.MetaCyc.toString());
		dictionary.put("BIGG", CompoundNodeLabel.BiGG.toString());
		dictionary.put("bigg", CompoundNodeLabel.BiGG.toString());
		dictionary.put("LIGAND-CPD", CompoundNodeLabel.KEGG.toString());
		dictionary.put("KEGG", CompoundNodeLabel.KEGG.toString());
		dictionary.put("kegg", CompoundNodeLabel.KEGG.toString());
		//KEGG
		dictionary.put("JCGGDB", "JCGGDB");
		dictionary.put("GlycomeDB", "GlycomeDB");
		dictionary.put("CCSD", "CCSD");
		dictionary.put("PUBCHEM", "PubChem");
		dictionary.put("PubChem", "PubChem");
		dictionary.put("PDB-CCD", "PDB");
		dictionary.put("chebi", CompoundNodeLabel.ChEBI.toString());
		dictionary.put("ChEBI", CompoundNodeLabel.ChEBI.toString());
		dictionary.put("CHEBI", CompoundNodeLabel.ChEBI.toString());
		dictionary.put("CAS", CompoundNodeLabel.CAS.toString());
		dictionary.put("NIKKAJI", "NIKKAJI");
		dictionary.put("3DMET", "MET3D");
		dictionary.put("seed", CompoundNodeLabel.Seed.toString());
		
		dictionary.put("hmdb", CompoundNodeLabel.HMDB.toString());
		
		dictionary.put("reactome", CompoundNodeLabel.Reactome.toString());
		
		dictionary.put("brenda", CompoundNodeLabel.BRENDA.toString());
		return dictionary;
	}
	
	public static String getDbReferenceKey(Class<?> entityType) {
		if (entityType.getClass().equals(BiggMetaboliteEntity.class)) {
			return "BIGG";
		}
		
		return null;
	}
}
