package pt.uminho.sysbio.biosynth.integration.neo4j;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosExternalDataNode;
import pt.uminho.sysbio.biosynthframework.neo4j.OntologyDatabase;

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
  
  public Node getSystemsBiologyOntology() {
    Relationship r = this.getSingleRelationship(GenericRelationship.has_sbo_term, Direction.OUTGOING);
    
    if (r != null) {
      Node sbo = r.getOtherNode(node);
      return sbo;
    }
    
    return null;
  }
  
  public Long setSystemsBiologyOntology(Node sboNode) {
    Relationship r = null;
    
    if (sboNode.hasLabel(OntologyDatabase.SBO)) {
      Node prevSboNode = getSystemsBiologyOntology();
      
      if (prevSboNode != null) {
        if (prevSboNode.getId() == sboNode.getId()) {
          return null;
        } else {
          logger.info("DELETE PREV {} {}", r, prevSboNode.getAllProperties());
          r = this.getSingleRelationship(GenericRelationship.has_sbo_term, Direction.OUTGOING);
          r.delete();
        }
      }
      
      r = this.node.createRelationshipTo(sboNode, GenericRelationship.has_sbo_term);
      Neo4jUtils.setTimestamps(r);
    }
    
    if (r == null) {
      return null;
    }
    return r.getId();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
