package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum MetaboliteRelationshipType implements RelationshipType{
	has_molecular_formula,	//Metabolite -[has_molecular_formula]-> Formula
	has_name,				//Metabolite -[has_name]-> Name
	has_crossreference_to,	//Metabolite -[has_crossreference_to]-> Metabolite

	has_inchi,				//Metabolite -[has_inchi]-> InChI
	has_inchikey,           //Metabolite -[has_inchikey]-> InChIKey
	has_charge,				//Metabolite -[has_charge]-> Charge
	has_smiles,				//Metabolite -[has_smiles]-> SMILES
	Isomorphic,				//Metabolite -[Isomorphic]-> ???
	has_secondary_accession,//Metabolite -[has_secondary_accession]-> Metabolite
	included_in,			//Metabolite -[included_in]-> MetabolicModel 1) metabolite can be found in metabolic models
	found_in,				//Metabolite -[found_in]-> SubcellularCompartment 1) metabolite can be found in SubcellularCompartment
	chebi_parent,			//Metabolite:ChEBI -[chebi_parent]-> Metabolite:ChEBI
	has_mdl_mol_file,		//Metabolite -[has_mdl_mol_file]-> ..
	in_pathway,				//Metabolite -[in_pathway]-> MetabolicPathway
	related_to,				//Metabolite -[related_to?]-> [Reaction|ec number|Gene|protein ?] e c number 1) this is a generic connection
	has_literature, 
	has_ontology,			//Metabolite -[has_ontology]-> Metabolite an ontology reference
	
	has_inchikey_fikhb,     //InChIKey -[has_inchikey_fikhb]-> FIKHB
	has_inchikey_sikhb,     //InChIKey -[has_inchikey_sikhb]-> SIKHB
	
	instance_of,            //Metabolite -[instance_of]-> Metabolite 1) to define metabolite hierarchy
	parent_of,
    subclass_of,
    
    /**
     * Model -[has_gpr_gene]-> ModelGene<br>
     * ModelReaction -[has_gpr_gene]-> ModelGene
     */
    has_gpr_gene,
}
