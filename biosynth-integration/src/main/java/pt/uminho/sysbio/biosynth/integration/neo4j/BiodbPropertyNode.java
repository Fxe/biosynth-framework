package pt.uminho.sysbio.biosynth.integration.neo4j;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;

public class BiodbPropertyNode extends AbstractBiodbNode {

  public BiodbPropertyNode(Node node) {
    super(node);
  }

  public String getValue() {
    return (String) getProperty(Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT);
  }
}
