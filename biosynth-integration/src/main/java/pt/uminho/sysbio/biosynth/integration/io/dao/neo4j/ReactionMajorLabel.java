package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum ReactionMajorLabel implements Label {
	LigandReaction, 
	BRENDA, 
	Rhea, 
	BiGG, 
	@Deprecated
	BiGG2Reaction,
	BiGGReaction,
	Seed, Reactome, 
	MetaCyc, HumanCyc, EcoCyc,
	BioPath, UniPathway, 
	NOTFOUND,
	ModelSeedReaction,
	MetaNetXReaction,
	PIR,
}
