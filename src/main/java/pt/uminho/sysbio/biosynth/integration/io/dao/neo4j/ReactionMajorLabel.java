package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum ReactionMajorLabel implements Label {
	LigandReaction,
	BiGG,
	Seed,
	MetaCyc
}
