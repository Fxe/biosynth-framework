package pt.uminho.sysbio.biosynth.integration.etl;

public enum ReactionQualityLabel {
	OK,
	
	EXACT_MATCH, // a A + b B op c C <=> c C' op a A + b B
	ORIENTATION_MISMATCH,
	STOICHIOMETRY_MISMATCH,
	
	PROTON_MISMATCH,
	
	METABOLITE_MISMATCH,
	
	ERROR,
}
