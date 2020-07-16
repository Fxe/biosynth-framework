package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

@Deprecated
public enum LiteratureMajorLabel implements Label {
	Patent, CiteXplore, PubMed,
}
