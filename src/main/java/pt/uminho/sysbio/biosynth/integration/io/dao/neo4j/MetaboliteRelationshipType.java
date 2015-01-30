package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum MetaboliteRelationshipType implements RelationshipType{
	has_molecular_formula, 
	has_name, 
	has_crossreference_to, 
	instance_of, 
	has_inchi, 
	has_charge, 
	has_smiles, 
	Isomorphic,
	included_in,
	found_in,
	ChEBI_Parent, has_mdl_mol_file,
}
