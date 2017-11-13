package pt.uminho.sysbio.biosynth.integration.neo4j;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;

public class BiodbEntityNode extends AbstractBiodbNode {

  public BiodbEntityNode(Node node) {
    super(node);
  }

  public String getEntry() {
    return (String) getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT);
  }
  
  public boolean isProxy() {
    return (boolean) getProperty(Neo4jDefinitions.PROXY_PROPERTY);
  }

}
