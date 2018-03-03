package pt.uminho.sysbio.biosynth.integration.neo4j;

import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;

public abstract class AbstractBiodbNode implements Node {
  
  protected final Node node;
  
  public Label getNamespace() {
    String ns = node.getProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, "undefined").toString();
    return DynamicLabel.label(ns);
  }
  
  public AbstractBiodbNode(Node node) {
    this.node = node;
  }
  
  @Override
  public GraphDatabaseService getGraphDatabase() {
    return node.getGraphDatabase();
  }

  @Override
  public boolean hasProperty(String key) {
    return node.hasProperty(key);
  }

  @Override
  public Object getProperty(String key) {
    return node.getProperty(key);
  }

  @Override
  public Object getProperty(String key, Object defaultValue) {
    return node.getProperty(key, defaultValue);
  }

  @Override
  public void setProperty(String key, Object value) {
    node.setProperty(key, value);
  }

  @Override
  public Object removeProperty(String key) {
    return node.removeProperty(key);
  }

  @Override
  public Iterable<String> getPropertyKeys() {
    return node.getPropertyKeys();
  }

  @Override
  public Map<String, Object> getProperties(String... keys) {
    return node.getProperties(keys);
  }

  @Override
  public Map<String, Object> getAllProperties() {
    return node.getAllProperties();
  }

  @Override
  public long getId() {
    return node.getId();
  }

  @Override
  public void delete() {
    node.delete();
  }

  @Override
  public Iterable<Relationship> getRelationships() {
    return node.getRelationships();
  }

  @Override
  public boolean hasRelationship() {
    return node.hasRelationship();
  }

  @Override
  public Iterable<Relationship> getRelationships(RelationshipType... types) {
    return node.getRelationships(types);
  }

  @Override
  public Iterable<Relationship> getRelationships(Direction direction, RelationshipType... types) {
    return node.getRelationships(direction, types);
  }

  @Override
  public boolean hasRelationship(RelationshipType... types) {
    return node.hasRelationship(types);
  }

  @Override
  public boolean hasRelationship(Direction direction, RelationshipType... types) {
    return node.hasRelationship(direction, types);
  }

  @Override
  public Iterable<Relationship> getRelationships(Direction dir) {
    return node.getRelationships(dir);
  }

  @Override
  public boolean hasRelationship(Direction dir) {
    return node.hasRelationship(dir);
  }

  @Override
  public Iterable<Relationship> getRelationships(RelationshipType type, Direction dir) {
    return node.getRelationships(type, dir);
  }

  @Override
  public boolean hasRelationship(RelationshipType type, Direction dir) {
    return node.hasRelationship(type, dir);
  }

  @Override
  public Relationship getSingleRelationship(RelationshipType type, Direction dir) {
    return node.getSingleRelationship(type, dir);
  }

  @Override
  public Relationship createRelationshipTo(Node otherNode, RelationshipType type) {
    return node.createRelationshipTo(otherNode, type);
  }

  @Override
  public Iterable<RelationshipType> getRelationshipTypes() {
    return node.getRelationshipTypes();
  }

  @Override
  public int getDegree() {
    return node.getDegree();
  }

  @Override
  public int getDegree(RelationshipType type) {
    return node.getDegree(type);
  }

  @Override
  public int getDegree(Direction direction) {
    return node.getDegree(direction);
  }

  @Override
  public int getDegree(RelationshipType type, Direction direction) {
    return node.getDegree(type, direction);
  }

  @Override
  public void addLabel(Label label) {
    node.addLabel(label);
  }

  @Override
  public void removeLabel(Label label) {
    node.removeLabel(label);
  }

  @Override
  public boolean hasLabel(Label label) {
    return node.hasLabel(label);
  }

  @Override
  public Iterable<Label> getLabels() {
    return node.getLabels();
  }
}
