package pt.uminho.sysbio.biosynth.integration.neo4j;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosExternalDataNode;

public class BiodbEntityNode extends BiosExternalDataNode {

  public BiodbEntityNode(Node node, String databasePath) {
    super(node, databasePath);
  }

  public String getEntry() {
    return (String) getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT);
  }
  
  public boolean isProxy() {
    return (boolean) getProperty(Neo4jDefinitions.PROXY_PROPERTY);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
