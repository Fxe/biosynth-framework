package pt.uminho.sysbio.biosynthframework.neo4j;

import org.neo4j.graphdb.Label;

public enum LiteratureDatabase implements Label {
  Patent, CiteXplore, PubMed, SupplementaryMaterial
}
