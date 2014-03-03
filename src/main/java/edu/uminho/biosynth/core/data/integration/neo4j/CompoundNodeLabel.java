package edu.uminho.biosynth.core.data.integration.neo4j;

import org.neo4j.graphdb.Label;

public enum CompoundNodeLabel implements Label {
	KEGG, BioCyc, MetaCyc, BiGG, MetaNetX
}
