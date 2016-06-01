package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.util.Map;

import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;

public class BiodbNode extends AbstractBiosynthEntity {
  
  private static final long serialVersionUID = 1L;
  
  private final Node node;
  
  public BiodbNode(Node node) {
    this.node = node;
  }
  
  public Node getNode() {
    return node;
  }

  @Override
  public Long getId() {
    return node.getId();
  }
  
  @Override
  public String getEntry() {
    return (String) node.getProperty("entry");
  };
  
  @Override
  public String getName() {
    return (String) node.getProperty("name");
  }
  
  public Map<String, Object> getPropertiesMap() {
    return Neo4jUtils.getPropertiesMap(node);
  }
  
  @Override
  public String toString() {
    return String.format("<%s>", this.getPropertiesMap());
  }
}
