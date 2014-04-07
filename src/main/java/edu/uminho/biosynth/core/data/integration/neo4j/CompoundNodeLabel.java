package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.graphdb.Label;

public enum CompoundNodeLabel implements Label {
	Compound,
	LigandCompound, LigandDrug, LigandGlycan, 
	KEGG, BioCyc, MetaCyc, BiGG, MetaNetX, Seed, MaizeCyc, PlantCyc, AraCyc, ChEBI, HMDB,
	Reactome, BRENDA, BioPath, UniPathway, KNApSAcK, CAS, DrugBank
	;

}
