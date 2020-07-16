package pt.uminho.sysbio.biosynthframework.neo4j;

import org.neo4j.graphdb.Label;

public enum GenomeDatabase implements Label {
  JGIGenome,
  RefSeqGenome,
  EnsemblGenome,
  GenBankGenome,
  KeggGenome,
}
