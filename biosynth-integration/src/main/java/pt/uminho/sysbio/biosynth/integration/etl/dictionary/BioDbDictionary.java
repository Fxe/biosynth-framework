package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;

public class BioDbDictionary {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BioDbDictionary.class);
	
	public static String translateDatabase(String db) {
		if (getDbDictionary().containsKey(db)) {
			return getDbDictionary().get(db);
		}
		
		LOGGER.error(String.format("[%s] not found in translation dictionary", db));
		return "NOTFOUND";
	}
	
//	public static String translateToLabel(String db) {
//		if (getDbLabel().containsKey(db)) {
//			return getDbLabel().get(db);
//		}
//		System.err.println(String.format("[%s] not found in translation dictionary", db));
//		return "NOTFOUND";
//	}
	
	/**
	 * WHY NOT LOWER CASE ! ?
	 * @return
	 */
	public static Map<String, String> getDbDictionary() {
		/**
		 * Some Rules:
		 *    Labels cannot start with Numbers or have symbols
		 *    Only characters and '_'
		 */
		Map<String, String> dictionary = new HashMap<> ();
		dictionary.put("NCI", "NCI");
		
		
		dictionary.put("Wikipedia", MetaboliteMajorLabel.Wikipedia.toString());
		dictionary.put("Wikipedia accession", MetaboliteMajorLabel.Wikipedia.toString());
		
		dictionary.put("LipidBank", MetaboliteMajorLabel.LipidBank.toString());
		
		dictionary.put("LIPID_MAPS", MetaboliteMajorLabel.LipidMAPS.toString());
		dictionary.put("lipidmaps", MetaboliteMajorLabel.LipidMAPS.toString());
		dictionary.put("LIPIDMAPS", MetaboliteMajorLabel.LipidMAPS.toString());
		dictionary.put("LIPID MAPS instance accession", MetaboliteMajorLabel.LipidMAPS.toString());
		
		dictionary.put("CHEMSPIDER", MetaboliteMajorLabel.ChemSpider.toString());
		dictionary.put("Chemspider accession", MetaboliteMajorLabel.ChemSpider.toString());
		dictionary.put("DrugBank accession", MetaboliteMajorLabel.DrugBank.toString());
		dictionary.put("DrugBank", MetaboliteMajorLabel.DrugBank.toString());
		dictionary.put("DRUGBANK", MetaboliteMajorLabel.DrugBank.toString());
		
		//PlantCyc
		dictionary.put("PLANTCYC:MAIZE", "PlantCyc:MaizeCyc");
		//BioCyc
		dictionary.put("KNAPSACK", MetaboliteMajorLabel.KNApSAcK.toString());
		dictionary.put("KNApSAcK", MetaboliteMajorLabel.KNApSAcK.toString());
		dictionary.put("KNApSAcK accession", MetaboliteMajorLabel.KNApSAcK.toString());
		dictionary.put("BIOCYC:ARA", "BioCyc:AraCyc");
		dictionary.put("METACYC", MetaboliteMajorLabel.MetaCyc.toString());
		dictionary.put("metacyc", MetaboliteMajorLabel.MetaCyc.toString());
		dictionary.put("MetaCyc accession", MetaboliteMajorLabel.MetaCyc.toString());
		
		dictionary.put("BIGG", MetaboliteMajorLabel.BiGG.toString());
		dictionary.put("bigg", MetaboliteMajorLabel.BiGG.toString());
		dictionary.put("LIGAND-CPD", MetaboliteMajorLabel.LigandCompound.toString());
//		dictionary.put("KEGG", MetaboliteMajorLabel.LigandCompound.toString());
		dictionary.put("KEGG COMPOUND accession", MetaboliteMajorLabel.LigandCompound.toString());
		dictionary.put("KEGG DRUG accession", MetaboliteMajorLabel.LigandDrug.toString());
		dictionary.put("KEGG GLYCAN accession", MetaboliteMajorLabel.LigandGlycan.toString());
		dictionary.put("KEGG-GLYCAN", MetaboliteMajorLabel.LigandGlycan.toString());
		dictionary.put("LigandBox", MetaboliteMajorLabel.LigandBox.toString());
		//KEGG
		dictionary.put("JCGGDB", MetaboliteMajorLabel.JCGGDB.toString());
		dictionary.put("GlycomeDB", MetaboliteMajorLabel.GlycomeDB.toString());
		dictionary.put("CCSD", MetaboliteMajorLabel.CCSD.toString());
		
		dictionary.put("PUBCHEM", MetaboliteMajorLabel.PubChemCompound.toString());
		dictionary.put("Pubchem accession", MetaboliteMajorLabel.PubChemCompound.toString());
		dictionary.put("PubChem", MetaboliteMajorLabel.PubChemCompound.toString());
		dictionary.put("PUBCHEM-SID", MetaboliteMajorLabel.PubChemSubstance.toString());
		
		dictionary.put("PDB-CCD", "PDB");
		dictionary.put("chebi", MetaboliteMajorLabel.ChEBI.toString());
		dictionary.put("ChEBI", MetaboliteMajorLabel.ChEBI.toString());
		dictionary.put("CHEBI", MetaboliteMajorLabel.ChEBI.toString());
		dictionary.put("CAS", MetaboliteMajorLabel.CAS.toString());
		dictionary.put("CAS Registry Number", MetaboliteMajorLabel.CAS.toString());
		dictionary.put("NIKKAJI", MetaboliteMajorLabel.NIKKAJI.toString());
		dictionary.put("3DMET", MetaboliteMajorLabel.MET3D.toString());
		dictionary.put("seed", MetaboliteMajorLabel.Seed.toString());
		
		dictionary.put("HMDB", MetaboliteMajorLabel.HMDB.toString());
		dictionary.put("hmdb", MetaboliteMajorLabel.HMDB.toString());
		dictionary.put("YMDB accession", MetaboliteMajorLabel.YMDB.toString());
		
		dictionary.put("HMDB accession", MetaboliteMajorLabel.HMDB.toString());
		
		dictionary.put("reactome", MetaboliteMajorLabel.Reactome.toString());
		
		dictionary.put("BRENDA-COMPOUND", MetaboliteMajorLabel.BRENDA.toString());
		dictionary.put("brenda", MetaboliteMajorLabel.BRENDA.toString());
		
		dictionary.put("RHEA", ReactionMajorLabel.Rhea.toString());
		dictionary.put("rhea", ReactionMajorLabel.Rhea.toString());
		dictionary.put("LIGAND-RXN", ReactionMajorLabel.LigandReaction.toString());

		dictionary.put("biopath", ReactionMajorLabel.BioPath.toString());
		
		dictionary.put("upa", ReactionMajorLabel.UniPathway.toString());
		dictionary.put("Reactome", ReactionMajorLabel.Reactome.toString());
		dictionary.put("reactome", ReactionMajorLabel.Reactome.toString());
		dictionary.put("UM-BBD-CPD", MetaboliteMajorLabel.EawagBBDCompound.toString());
		
		dictionary.put("UNIPROT", GlobalLabel.UniProt.toString());
		return dictionary;
	}
	
//	public static Map<String, String> getDbLabel() {
//		/**
//		 * Some Rules:
//		 *    Labels cannot start with Numbers or have symbols
//		 *    Only characters and '_'
//		 */
//		Map<String, String> dictionary = new HashMap<> ();
//		dictionary.put("biopath", CompoundNodeLabel.BioPath.toString());
//		dictionary.put("upa", CompoundNodeLabel.UniPathway.toString());
//		//PlantCyc
//		dictionary.put("PLANTCYC:MAIZE", CompoundNodeLabel.MaizeCyc.toString());
//		//BioCyc
//		dictionary.put("KNApSAcK", CompoundNodeLabel.KNApSAcK.toString());
//		dictionary.put("BIOCYC:ARA", CompoundNodeLabel.AraCyc.toString());
//		dictionary.put("METACYC", CompoundNodeLabel.MetaCyc.toString());
//		dictionary.put("metacyc", CompoundNodeLabel.MetaCyc.toString());
//		dictionary.put("BIGG", CompoundNodeLabel.BiGG.toString());
//		dictionary.put("bigg", CompoundNodeLabel.BiGG.toString());
//		dictionary.put("LIGAND-CPD", CompoundNodeLabel.KEGG.toString());
//		dictionary.put("KEGG", CompoundNodeLabel.KEGG.toString());
//		dictionary.put("kegg", CompoundNodeLabel.KEGG.toString());
//		//KEGG
//		dictionary.put("JCGGDB", "JCGGDB");
//		dictionary.put("GlycomeDB", "GlycomeDB");
//		dictionary.put("CCSD", "CCSD");
//		dictionary.put("PUBCHEM", "PubChem");
//		dictionary.put("PubChem", "PubChem");
//		dictionary.put("PDB-CCD", "PDB");
//		dictionary.put("chebi", CompoundNodeLabel.ChEBI.toString());
//		dictionary.put("ChEBI", CompoundNodeLabel.ChEBI.toString());
//		dictionary.put("CHEBI", CompoundNodeLabel.ChEBI.toString());
//		dictionary.put("CAS", CompoundNodeLabel.CAS.toString());
//		dictionary.put("NIKKAJI", "NIKKAJI");
//		dictionary.put("3DMET", "MET3D");
//		dictionary.put("seed", CompoundNodeLabel.Seed.toString());
//		
//		dictionary.put("hmdb", CompoundNodeLabel.HMDB.toString());
//		
//		dictionary.put("reactome", CompoundNodeLabel.Reactome.toString());
//		
//		dictionary.put("brenda", CompoundNodeLabel.BRENDA.toString());
//		return dictionary;
//	}
	
	public static String getDbReferenceKey(Class<?> entityType) {
		if (entityType.getClass().equals(BiggMetaboliteEntity.class)) {
			return "BIGG";
		}
		
		return null;
	}
}
