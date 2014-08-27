package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.graphdb.Label;

public enum MetaboliteMajorLabel implements Label {
	LigandCompound, LigandDrug, LigandGlycan, 
	MetaCyc, BiGG, MetaNetX, Seed, MaizeCyc, AraCyc, ChEBI, HMDB,
	Reactome, BRENDA, BioPath, UniPathway, KNApSAcK, CAS, DrugBank,
	PubChem, LipidMAPS, ChemSpider
	;
}
