package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbReactionNode;

public class BiosModelReactionNode extends BiodbEntityNode {

  public BiosModelReactionNode(Node node, String databasePath) {
    super(node, databasePath);
  }
  
  public static Map<String, Integer> parse(String str) {
    Map<String, Integer> authors = new TreeMap<>();
    if (str != null && !str.isEmpty()) {
      for (String a : str.split(";")) {
        String author = a.split(":")[0];
        Integer score = Integer.parseInt(a.split(":")[1]);
        authors.put(author, score);
      }
    }
    return authors;
  }
  
  public static String toString(Map<String, Integer> authors) {
    return Joiner.on(';').withKeyValueSeparator(':').join(authors);
  }
  
  public String getSid() {
    return (String) this.getProperty("id", null);
  }
  
  public BiodbEntityNode getGpr() {
    Relationship r = this.getSingleRelationship(MetabolicModelRelationshipType.has_gpr, Direction.BOTH);
    if (r != null) {
      return new BiodbEntityNode(r.getOtherNode(node), databasePath);
    }
    
    return null;
  }
  
  public double getStoichiometry(Relationship relationship, double defaultValue) {
    Object o = relationship.getProperty("stoichiometry", null);
    if (o == null) {
      return defaultValue;
    } else if (o instanceof String) {
      return Double.parseDouble(o.toString());
    } else {
      return (double) o;
    }
  }
  
  public Map<Long, Double> getStoichiometry() {
    return getStoichiometry(1);
  }
  
  public Map<Long, Double> getStoichiometry(double defaultValue) {
    Map<Long, Double> stoichiometryMap = new HashMap<>();
    for (Relationship l : this.getRelationships(MetabolicModelRelationshipType.left_component)) {
      Node other = l.getOtherNode(this);
      double value = getStoichiometry(l, defaultValue);
      stoichiometryMap.put(other.getId(), -1 * value);
    }
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.right_component)) {
      Node other = r.getOtherNode(this);
      double value = getStoichiometry(r, defaultValue);
      stoichiometryMap.put(other.getId(), value);
    }
    
    return stoichiometryMap;
  }
  
  public Set<BiodbReactionNode> getReferences() {
    Set<BiodbReactionNode> references = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      Node other = r.getOtherNode(node);
      references.add(new BiodbReactionNode(other, databasePath));
    }
    return references;
  }

  public Map<String, Integer> getAnnotationUsers(BiodbReactionNode rxnNode) {
    Map<String, Integer> users = new HashMap<>();
    Relationship referenceLink = null;
    for (Relationship r : this.getRelationships(
        Direction.OUTGOING, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r.getOtherNode(this.node).getId() == rxnNode.getId()) {
        referenceLink = r;
      }
    }
    
    if (referenceLink != null) {
      String authorsStr = (String) referenceLink.getProperty("authors", "");
      Map<String, Integer> authors = parse(authorsStr);
      users.putAll(authors);
    }
    
    return users;
  }
  
  public Long addAnnotation(BiodbReactionNode rxnNode, int score, String author) {
    Relationship referenceLink = null;
    for (Relationship r : this.getRelationships(
        Direction.OUTGOING, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r.getOtherNode(this.node).getId() == rxnNode.getId()) {
        referenceLink = r;
      }
    }
    
    if (referenceLink == null) {
      referenceLink = this.createRelationshipTo(
          rxnNode, MetabolicModelRelationshipType.has_crossreference_to);
      Neo4jUtils.setCreatedTimestamp(referenceLink);
    }
    Neo4jUtils.setUpdatedTimestamp(referenceLink);
    
    String authorsStr = (String) referenceLink.getProperty("authors", "");
    Map<String, Integer> authors = parse(authorsStr);
    authors.put(author, score);
    
    referenceLink.setProperty("authors", toString(authors));
    
    return referenceLink.getId();
  }
  //stoich etc
}
