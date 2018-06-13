package pt.uminho.sysbio.biosynth.integration.neo4j;

import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosExternalDataNode;

public class BiodbEntityNode extends BiosExternalDataNode {

  private static final Logger logger = LoggerFactory.getLogger(BiodbEntityNode.class);
  
  public BiodbEntityNode(Node node, String databasePath) {
    super(node, databasePath);
  }

  public String getEntry() {
    return (String) getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT);
  }
  
  public boolean isProxy() {
    Boolean proxy = (Boolean) getProperty(Neo4jDefinitions.PROXY_PROPERTY, null);
    if (proxy == null) {
      proxy = true;
      logger.warn("{} Proxy attribute not found. Default as TRUE", this.node);
    }
    return proxy;
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
