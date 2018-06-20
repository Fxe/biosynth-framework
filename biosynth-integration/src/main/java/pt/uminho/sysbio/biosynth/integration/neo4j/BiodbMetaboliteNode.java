package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosVersionNode;

public class BiodbMetaboliteNode extends BiodbEntityNode {
  
  private static final Logger logger = LoggerFactory.getLogger(BiodbMetaboliteNode.class);
  
  public BiodbMetaboliteNode(Node node, String databasePath) {
    super(node, databasePath);
    if (!node.hasLabel(GlobalLabel.Metabolite)) {
      throw new IllegalArgumentException("invalid node: missing " + GlobalLabel.Metabolite + " " + 
                                         node + " " + Neo4jUtils.getLabels(node) + " " + node.getAllProperties());
    }
  }
  
  public MetaboliteMajorLabel getDatabase() {
    return MetaboliteMajorLabel.valueOf((String) getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
  }
  
  public BiosVersionNode getPreviousVersion() {
    Relationship relationship = 
        this.getSingleRelationship(GenericRelationship.has_version, Direction.OUTGOING);
    if (relationship != null) {
      Node versionNode = relationship.getOtherNode(this);
      return new BiosVersionNode(versionNode, databasePath);
    }
    return null;
  }
  
  public String getVersion() {
    return (String) this.getProperty(Neo4jDefinitions.ENTITY_VERSION, null);
  }
  
  public BiodbPropertyNode getMetaboliteProperty(MetabolitePropertyLabel property) {
    Set<BiodbPropertyNode> result = new HashSet<> ();
    for (Node node : Neo4jUtils.collectNodeRelationshipNodes(node, property)) {
      result.add(new BiodbPropertyNode(node, databasePath));
    }
    
    if (result.size() > 1) {
      logger.warn("Metabolite [{}] has more than 1 [{}] (found {}). ", this.getEntry(), property, result.size());
    }
    
    if (result.isEmpty()) {
      return null;
    }
    
    return result.iterator().next();
  }
  
  public Set<BiodbPropertyNode> getMetaboliteProperties(MetabolitePropertyLabel property) {
    Set<BiodbPropertyNode> result = new HashSet<> ();
    for (Node node : Neo4jUtils.collectNodeRelationshipNodes(node, property)) {
      result.add(new BiodbPropertyNode(node, databasePath));
    }
    return result;
  }
  
  public static Map<Long, String> getOwnerAny(Node prop) {
    Map<Long, String> result = new HashMap<> ();
    for (Relationship r : prop.getRelationships(Direction.INCOMING)) {
      Node prop2 = r.getOtherNode(prop);
      if (prop2.hasLabel(GlobalLabel.MetaboliteProperty) && 
          r.getProperty("source").equals("inferred")) {
        result.put(prop2.getId(), prop2.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, "error").toString());
      }
    }
    
    return result;
  }
  
  public Map<Long, String> getOwner(Node prop) {
    Map<Long, String> o = getOwnerAny(prop);
    Map<Long, String> result = new HashMap<> ();
    
    for (long id : o.keySet()) {
      Node i = getGraphDatabase().getNodeById(id);
      if (Neo4jUtils.exitsRelationshipBetween(node, i, Direction.BOTH)) {
        result.put(id, o.get(id));
      }
    }
    
    return result;
  }
  
  @Override
  public String toString() {
    return super.toString();
  }
}
