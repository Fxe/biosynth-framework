package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum MetaboliteMajorLabel implements Label {
  //KEGG universe
  LigandCompound, LigandDrug, LigandGlycan,
  //BioCyc
  MetaCyc, EcoCyc, HumanCyc, YeastCyc, AraCyc,
  HMDB, YMDB,
  BiGG, BiGG2, BiGGMetabolite, MetaNetX, Seed, ModelSeed, MaizeCyc,
  
  ChEMBL, ChEBI, 
  Reactome, BRENDA, BioPath, UniPathway, KNApSAcK, CAS, DrugBank,
  PubChemCompound, PubChemSubstance,
  LipidMAPS, ChemSpider,
  EawagBBDCompound,
  MET3D, NIKKAJI, PDB, NCI, LipidBank, JCGGDB, GlycomeDB, CCSD, Wikipedia,
  LigandBox, GlyTouCan, GlycoEpitope, 
  MetaboLights, Metabolomics, RefMet,
  NOTFOUND, PlantCyc,
  SwissLipids, ENVIPath,
  ;
  
}
