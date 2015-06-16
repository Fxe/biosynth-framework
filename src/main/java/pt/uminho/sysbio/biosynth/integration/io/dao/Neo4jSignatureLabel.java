package pt.uminho.sysbio.biosynth.integration.io.dao;

import org.neo4j.graphdb.Label;

public enum Neo4jSignatureLabel implements Label {
	ChemicalStructure,
	InChI, InChIKey,
	MDLMolFile,
	MolecularSignature, ReactionSignature,
	Signature, SignatureSet,
	Hash32, Hash64, Hash256, Hash512
}
