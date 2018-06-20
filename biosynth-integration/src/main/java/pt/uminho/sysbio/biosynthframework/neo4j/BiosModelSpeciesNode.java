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
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;

public class BiosModelSpeciesNode extends BiodbEntityNode {

  public BiosModelSpeciesNode(Node node, String databasePath) {
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
  
  public Map<String, Integer> getAnnotationUsers(BiodbMetaboliteNode cpdNode) {
    Map<String, Integer> users = new HashMap<>();
    Relationship referenceLink = null;
    for (Relationship r : this.getRelationships(
        Direction.OUTGOING, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r.getOtherNode(this.node).getId() == cpdNode.getId()) {
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
  
  public Long addAnnotation(BiodbMetaboliteNode cpdNode, int score, String author) {
    Relationship referenceLink = null;
    for (Relationship r : this.getRelationships(
        Direction.OUTGOING, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r.getOtherNode(this.node).getId() == cpdNode.getId()) {
        referenceLink = r;
      }
    }
    
    if (referenceLink == null) {
      referenceLink = this.createRelationshipTo(
          cpdNode, MetabolicModelRelationshipType.has_crossreference_to);
      Neo4jUtils.setCreatedTimestamp(referenceLink);
    }
    Neo4jUtils.setUpdatedTimestamp(referenceLink);
    
    String authorsStr = (String) referenceLink.getProperty("authors", "");
    Map<String, Integer> authors = parse(authorsStr);
    authors.put(author, score);
    
    referenceLink.setProperty("authors", toString(authors));
    
    return referenceLink.getId();
  }
  
  public Set<BiodbMetaboliteNode> getReferences() {
    Set<BiodbMetaboliteNode> references = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      Node other = r.getOtherNode(node);
      references.add(new BiodbMetaboliteNode(other, databasePath));
    }
    return references;
  }
  
  public Set<Long> clearAnnotation() {
    Set<Long> deleted = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      deleted.add(r.getId());
      r.delete();
    }
    return deleted;
  }
}
