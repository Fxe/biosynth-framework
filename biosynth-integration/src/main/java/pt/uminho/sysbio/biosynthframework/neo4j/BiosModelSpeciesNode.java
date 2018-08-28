package pt.uminho.sysbio.biosynthframework.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbEntityNode;
import pt.uminho.sysbio.biosynth.integration.neo4j.BiodbMetaboliteNode;
import pt.uminho.sysbio.biosynthframework.SubcellularCompartment;
import pt.uminho.sysbio.biosynthframework.integration.etl.EtlModels;

public class BiosModelSpeciesNode extends BiodbEntityNode {

  private static final Logger logger = LoggerFactory.getLogger(BiosModelSpeciesNode.class);

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
  
  public Long deleteAnnotation(BiodbMetaboliteNode cpdNode) {
    Long deleted = null;
    for (Relationship r : this.getRelationships(
        Direction.OUTGOING, MetabolicModelRelationshipType.has_crossreference_to)) {
      if (r.getOtherNode(this.node).getId() == cpdNode.getId()) {
        deleted = r.getId();
        r.delete();
      }
    }
    
    return deleted;
  }
  
  public Set<BiodbMetaboliteNode> getReferences() {
    Set<BiodbMetaboliteNode> references = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      Node other = r.getOtherNode(node);
      references.add(new BiodbMetaboliteNode(other, databasePath));
    }
    return references;
  }
  
  public Set<BiodbMetaboliteNode> getReferences(MetaboliteMajorLabel database) {
    Set<BiodbMetaboliteNode> references = new HashSet<>();
    for (Relationship r : this.getRelationships(MetabolicModelRelationshipType.has_crossreference_to)) {
      Node other = r.getOtherNode(node);
      if (other.hasLabel(database)) {
        references.add(new BiodbMetaboliteNode(other, databasePath));        
      }
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
  
  public Integer getAnnotationScore(BiodbMetaboliteNode cpdNode) {
    Integer score = -1;
    
    Map<String, Integer> users = this.getAnnotationUsers(cpdNode);
    for (Integer v : users.values()) {
      if (score < v) {
        score = v;
      }
    }
    
    return score;
  }
  
  public BiosModelCompartmentNode getCompartment() {
    Relationship r = this.getSingleRelationship(MetabolicModelRelationshipType.in_compartment, Direction.BOTH);
    if (r == null) {
      return null;
    }
    return new BiosModelCompartmentNode(r.getOtherNode(this), databasePath);
  }
  
  public boolean setCompartment(BiosModelCompartmentNode cmpNode) {
    Relationship r = this.getSingleRelationship(MetabolicModelRelationshipType.in_compartment, Direction.BOTH);
    if (r != null) {
      Node other = r.getOtherNode(this);
      if (other.getId() == cmpNode.getId()) {
        return false;
      }
      logger.warn("[RMLK] [{}] -[{}]-> [{}]", this, MetabolicModelRelationshipType.in_compartment, other);
      r.delete();
    }
    if (!Neo4jUtils.exitsRelationshipBetween(this, cmpNode, Direction.BOTH)) {
      logger.info("[LINK] [{}] -[{}]-> [{}]", this, MetabolicModelRelationshipType.in_compartment, cmpNode);
      this.createRelationshipTo(cmpNode, MetabolicModelRelationshipType.in_compartment);
    }
    
    return true;
  }
  
  public SubcellularCompartment getSubcellularCompartment() {
    SubcellularCompartment scmp = null;
    BiosModelCompartmentNode cmp = this.getCompartment();
    BiosUniversalCompartmentNode ucmpNode = null;
    if (cmp != null && (ucmpNode = cmp.getUniversalCompartment()) != null) {
      scmp = ucmpNode.getCompartment();
    }
    return scmp;
  }

  public String getCompartmentSid() {
    Relationship r = this.getSingleRelationship(MetabolicModelRelationshipType.in_compartment, Direction.BOTH);
    if (r == null) {
      return null;
    }
    return (String) r.getOtherNode(this).getProperty("id", null);
  }
  
  public BiosUniversalMetaboliteNode getUniversalMetabolite() {
    return getUniversalMetabolite(5);
  }
  
  public BiosUniversalMetaboliteNode getUniversalMetabolite(int score) {
    Map<Long, BiosUniversalMetaboliteNode> match = new HashMap<>();
    for (BiodbMetaboliteNode cpdNode : this.getReferences()) {
      if (this.getAnnotationScore(cpdNode) >= score) {
        BiosUniversalMetaboliteNode ucpdNode = cpdNode.getUniversalMetabolite();
        if (ucpdNode != null) {
          match.put(ucpdNode.getId(), ucpdNode);          
        }
      }
    }
    
    if (match.size() == 1) {
      return match.values().iterator().next();
    }
    return null;
  }
  
  @Override
  public String toString() {
    return String.format("[%d]%s", getId(), getSid());
  }
}
