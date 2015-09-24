package pt.uminho.sysbio.biosynth.integration.etl.dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynthframework.Metabolite;
import pt.uminho.sysbio.biosynthframework.biodb.chebi.ChebiMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;

public class BiobaseMetaboliteEtlDictionary<M extends Metabolite> implements EtlDictionary<String, String, String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BiobaseMetaboliteEtlDictionary.class);
	
	private final Class<M> clazz;
	
	public BiobaseMetaboliteEtlDictionary(Class<M> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public String translate(String lookup, String reference) {
		String result = null;
		
		if (lookup.toLowerCase().trim().equals("kegg")) {
			return translateKegg(lookup, reference);
		}
		
		if ((clazz.equals(KeggCompoundMetaboliteEntity.class) || 
				 clazz.equals(KeggDrugMetaboliteEntity.class) || 
					clazz.equals(ChebiMetaboliteEntity.class)) && 
					lookup.equals("PubChem")) {
			result = MetaboliteMajorLabel.PubChemSubstance.toString();
		} else {
			result = BioDbDictionary.translateDatabase(lookup);
		}
		
		LOGGER.debug(String.format("Translated %s -> %s using modifier %s", lookup, result, clazz));
		return result;
	}
	
	public String translateKegg(String lookup, String entry) {
		String result = null;
		
		if (entry.length() == 6 && lookup.toLowerCase().equals("kegg")) {
			if (entry.startsWith("C")) result = MetaboliteMajorLabel.LigandCompound.toString();
			if (entry.startsWith("D")) result = MetaboliteMajorLabel.LigandDrug.toString();
			if (entry.startsWith("G")) result = MetaboliteMajorLabel.LigandGlycan.toString();
			if (entry.startsWith("R")) result = ReactionMajorLabel.LigandReaction.toString();
		} else {
			if ((clazz.equals(KeggCompoundMetaboliteEntity.class) || 
				 clazz.equals(KeggDrugMetaboliteEntity.class) || 
					clazz.equals(ChebiMetaboliteEntity.class)) && 
					lookup.equals("PubChem")) {
				result = MetaboliteMajorLabel.PubChemSubstance.toString();
			} else {
				result = BioDbDictionary.translateDatabase(lookup);
			}
		}
		
		if (result == null) {
			System.out.println("Unable to translate " + lookup + ":" + entry);
			result = MetaboliteMajorLabel.NOTFOUND.toString();
		}
		
		LOGGER.debug(String.format("Translated %s -> %s using modifier %s", lookup, result, clazz));
		return result;
	}
}
