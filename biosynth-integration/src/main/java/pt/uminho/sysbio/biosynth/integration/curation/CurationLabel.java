package pt.uminho.sysbio.biosynth.integration.curation;

import org.neo4j.graphdb.Label;

public enum CurationLabel implements Label{
	CurationSet,
	CurationMetabolite,
	CurationReaction,
	CurationProperty,
	CurationOperation,
	CurationUser,
	
	UniversalSpecie,
	
	UniversalMetabolite,
	UniversalReaction,
}
