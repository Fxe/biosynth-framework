package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum GlobalLabel implements Label {
	Model,
	SuperMetabolite, Metabolite, MetaboliteProperty,
	Reaction, ReactionProperty,
	MetabolicPathway, 
	BioCyc, Gene, EC
}
