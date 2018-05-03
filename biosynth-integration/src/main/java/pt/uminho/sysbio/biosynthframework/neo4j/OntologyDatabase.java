package pt.uminho.sysbio.biosynthframework.neo4j;

import org.neo4j.graphdb.Label;

public enum OntologyDatabase implements Label {
  GO,  //Gene Ontology Consortium
  SBO, //Systems Biology Ontology
  MIRIAM, //Identifiers.org
}
