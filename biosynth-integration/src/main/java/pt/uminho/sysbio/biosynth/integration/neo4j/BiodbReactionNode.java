package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;

public class BiodbReactionNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiodbReactionNode.class);
  
  public BiodbReactionNode(Node node) {
    super(node);
    if (!node.hasLabel(GlobalLabel.Reaction)) {
      throw new IllegalArgumentException("invalid node: missing " + GlobalLabel.Reaction);
    }
  }
  
  public ReactionMajorLabel getDatabase() {
    return ReactionMajorLabel.valueOf((String) getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY));
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
      String coefficient = (String) lr.getProperty("coefficient");
      if (!NumberUtils.isCreatable(coefficient)) {
        return false;
      }
      if (!a.add(cpdNode.getId())) {
        return false;
      }
    }
    return true;
  }
  
  public Map<Long, Double> getStoichiometry() {
    Map<Long, Double> stoich = new HashMap<> ();
    Map<Long, Double> l = getLeftStoichiometry();
    Map<Long, Double> r = getRightStoichiometry();
    
    for (long k : l.keySet()) {
      Double value = l.get(k);
      if (value != null) {
        value = -1 * value;
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
      String coefficient = (String) l.getProperty("coefficient");
      Double value = null;
      if (NumberUtils.isCreatable(coefficient)) {
        value = Double.parseDouble(coefficient);
      }
//      double value = l.getProperty(Neo4jDefinitions.)
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
      String coefficient = (String) r.getProperty("coefficient");
      Double value = null;
      if (NumberUtils.isCreatable(coefficient)) {
        value = Double.parseDouble(coefficient);
      }
      if (right.put(cpdNode.getId(), value) != null) {
        logger.warn("[{}:{}] stoichiometry collision [right]", node.getId(), getEntry());
      }
    }

    return right;
  }
}
