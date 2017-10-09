package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum GlobalLabel implements Label {
	Literature,
	BiGG,
	KEGG, BioCyc,
	Orthology,
	KeggPathway, MetaCycPathway,
	ModelSeedRole, ModelSeedSubsystem,
	KeggOrthology,
	KeggEnviron,
	EnzymeCommission,
	MetabolicModel,
	SuperMetabolite, Metabolite, MetaboliteProperty,
	Reaction, ReactionProperty,
	MetabolicPathway, 
	SubcellularCompartment,
	
	//Things that I have no clue where to put !
	KeggOrganism, KeggGene,
	EntrezTaxonomy, NCBIGene, NCBINucleotide, NCBIProtein,
	UniProtAccession,
	UniProtProtein,
	UniProt, Gene, EnzymePortal, BrendaEnzyme, SGD, Phenotype, ExPASy,
	PROSITE, PDB, PFAM, PROTEINMODELPORTAL,
	KeggReactionPair, Hypothetical,
	
	LogicalOperator,
	Protein, ProteinProperty,
	
}
