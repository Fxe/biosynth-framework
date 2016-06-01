package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum GlobalLabel implements Label {
	Literature,
	BiGG,
	KEGG, BioCyc,
	Orthology,
	KeggPathway, MetaCycPathway,
	KeggOrthology,
	EnzymeCommission,
	MetabolicModel,
	SuperMetabolite, Metabolite, MetaboliteProperty,
	Reaction, ReactionProperty,
	MetabolicPathway, 
	SubcellularCompartment,
	
	//Things that I have no clue where to put !
	EntrezTaxonomy,
	UniProt, Gene, EnzymePortal, BrendaEnzyme, 
	PROSITE, PDB, PFAM, PROTEINMODELPORTAL,
	KeggReactionPair, Hypothetical,
	
	LogicalOperator,
	Protein, ProteinProperty
}
