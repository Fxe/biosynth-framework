package pt.uminho.sysbio.biosynth.integration.io.dao;

import org.neo4j.graphdb.RelationshipType;

public enum Neo4jSignatureRelationship implements RelationshipType {
	has_signature,
	has_signature_set,
	left_signature, right_signature, 
	
	has_mdl_mol_file,
	has_inchi,
	has_inchi_key,
	has_molecular_signature,
	has_reaction_signature,
}
