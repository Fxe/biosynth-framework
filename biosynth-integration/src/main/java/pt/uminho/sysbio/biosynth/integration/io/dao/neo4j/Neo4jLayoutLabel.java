package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import org.neo4j.graphdb.Label;

public enum Neo4jLayoutLabel implements Label {
  MetabolicLayout, LayoutNode, 
  MetaboliteReference, ReactionReference,
  
}
