package pt.uminho.sysbio.biosynth.integration.etl;

import org.neo4j.graphdb.Label;

public enum ReactionQualityLabel implements Label {
	OK,
	
	EXACT_MATCH, // a A + b B op c C <=> c C' op a A + b B
	ORIENTATION_MISMATCH,
	STOICHIOMETRY_MISMATCH,
	
	PROTON_MISMATCH,
	
	METABOLITE_MISMATCH,
	
	ERROR,
}
