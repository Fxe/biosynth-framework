package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosVersionNode;

public class BiodbReactionNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiodbReactionNode.class);
  
  public static String STOICHIOMETRY = "stoichiometry";
  
  public BiodbReactionNode(Node node, String databasePath) {
    super(node, databasePath);
    if (!node.hasLabel(GlobalLabel.Reaction)) {
      throw new IllegalArgumentException("invalid node: missing " + GlobalLabel.Reaction);
    }
  }
  
  public ReactionMajorLabel getDatabase() {
    return ReactionMajorLabel.valueOf((String) getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
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
  
  /**
   * Basic reactions have defined stoichiometry 
   * and can be represented by the map 
   * Map<Long, String>
   * @return
   */
  public boolean isBasic() {
    Set<Long> a = new HashSet<> ();
    for (Relationship lr : node.getRelationships(
        ReactionRelationshipType.left_component, 
        ReactionRelationshipType.right_component)) {
      Node cpdNode = lr.getOtherNode(node);
      Double stoichiometry = getStoichiometry(lr);
      if (stoichiometry == null) {
        return false;
      }
      if (!a.add(cpdNode.getId())) {
        return false;
      }
    }
    return true;
  }
  
  public Double getStoichiometry(Relationship lr) {
    Double result = null;
    String value = null;
    if (lr.hasProperty("coefficient")) {
      Object o = lr.getProperty("coefficient");
      if (o instanceof String) {
        value = (String) lr.getProperty("coefficient");
      } else {
        result = (Double) lr.getProperty("coefficient");
      }
    } else {
      value = Double.toString((double) lr.getProperty(STOICHIOMETRY));
    }
    
    if (NumberUtils.isCreatable(value)) {
      result = Double.parseDouble(value);
    }
    
    return result;
  }
  
  public Map<Long, Double> getStoichiometry() {
    Map<Long, Double> stoich = new HashMap<> ();
    Map<Long, Double> l = getLeftStoichiometry();
    Map<Long, Double> r = getRightStoichiometry();
    
    for (long k : l.keySet()) {
      Double value = l.get(k);
      if (value != null) {
        value = -1 * Math.abs(value);
      }
      stoich.put(k, value);
    }
    for (long k : r.keySet()) {
      if (stoich.put(k, r.get(k)) != null) {
        logger.warn("[{}:{}] stoichiometry collision", node.getId(), getEntry());
      }
    }

    return stoich;
  }
  
  public Map<Long, Double> getLeftStoichiometry() {
    Map<Long, Double> left = new HashMap<> ();
    for (Relationship l : node.getRelationships(ReactionRelationshipType.left_component)) {
      Node cpdNode = l.getOtherNode(node);
      Double value = getStoichiometry(l);
      if (left.put(cpdNode.getId(), value) != null) {
        logger.warn("[{}:{}] stoichiometry collision [left]", node.getId(), getEntry());
      }
    }

    return left;
  }
  
  public Map<Long, Double> getRightStoichiometry() {
    Map<Long, Double> right = new HashMap<> ();
    for (Relationship r : node.getRelationships(ReactionRelationshipType.right_component)) {
      Node cpdNode = r.getOtherNode(node);
      Double value = getStoichiometry(r);
      if (right.put(cpdNode.getId(), value) != null) {
        logger.warn("[{}:{}] stoichiometry collision [right]", node.getId(), getEntry());
      }
    }

    return right;
  }
  
  @Override
  public String toString() {
    return String.format("[%d]%s@%s", getId(), getEntry(), getDatabase());
  }
}
