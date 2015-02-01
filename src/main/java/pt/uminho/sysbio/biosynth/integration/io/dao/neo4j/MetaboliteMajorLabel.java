package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum MetaboliteMajorLabel implements Label {
	//KEGG universe
	LigandCompound, LigandDrug, LigandGlycan,
	//BioCyc
	MetaCyc, EcoCyc, HumanCyc,
	
	BiGG, MetaNetX, Seed, MaizeCyc, AraCyc, ChEBI, HMDB,
	Reactome, BRENDA, BioPath, UniPathway, KNApSAcK, CAS, DrugBank,
	PubChemCompound, PubChemSubstance,
	LipidMAPS, ChemSpider,
	EawagBBDCompound,
	MET3D, NIKKAJI, PDB, NCI, LipidBank, JCGGDB, GlycomeDB, CCSD, Wikipedia,
	LigandBox,
	NOTFOUND
	;
}
