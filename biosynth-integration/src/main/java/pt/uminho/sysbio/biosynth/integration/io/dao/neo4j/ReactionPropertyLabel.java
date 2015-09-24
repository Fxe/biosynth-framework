package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum ReactionPropertyLabel implements Label {
	Reaction,
	ECNumber, Name, EnzymeCommission,
	Pathway, Orthology, EnzymaticReaction
}
