package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum MetaboliteMajorLabel implements Label {
	LigandCompound, LigandDrug, LigandGlycan, 
	MetaCyc, 
	BiGG, MetaNetX, Seed, MaizeCyc, AraCyc, ChEBI, HMDB,
	Reactome, BRENDA, BioPath, UniPathway, KNApSAcK, CAS, DrugBank,
	PubChemCompound, PubChemSubstance,
	LipidMAPS, ChemSpider,
	NOTFOUND
	;
}
