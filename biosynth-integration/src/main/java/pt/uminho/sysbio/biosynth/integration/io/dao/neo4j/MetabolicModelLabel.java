package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum MetabolicModelLabel implements Label {
	MetaboliteSpecie,
	ModelReaction,
	ModelMetabolite,
	ModelSubsystem,
	ModelGene,
	ModelGPR,
	ModelCompartment,
}
