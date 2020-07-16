package pt.uminho.sysbio.biosynth.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolitePropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionMajorLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionPropertyLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.ReactionRelationshipType;

public class SomeNodeFactory {

  private String entry;
  private String majorLabel;
  private String uniqueConstraintProperty = null;
  private Set<String> labels = new HashSet<> ();
  private List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> connectedEntities = new ArrayList<> ();
  private Map<String, Object> properties = new HashMap<> ();
  private Map<String, Object> eproperties = new HashMap<> ();

  public SomeNodeFactory withLinkTo(AbstractGraphNodeEntity node, AbstractGraphEdgeEntity edge) {
    if (node != null && edge != null) {
      Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p = new ImmutablePair<>(edge, node);
      connectedEntities.add(p);
    }
    return this;
  }
  public SomeNodeFactory withLinkTo(AbstractGraphNodeEntity node, 
                                    RelationshipType relationshipType, 
                                    Map<String, Object> relationshipProperties) {
    
    if (node != null && relationshipType != null) {
      Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p = 
          new ImmutablePair<>(new SomeNodeFactory().withProperties(properties)
                                                   .buildEdge(relationshipType), 
                              node);
      connectedEntities.add(p);
    }
    return this;
  }
  
  public SomeNodeFactory withProperties(Map<String, Object> properties) {
    if (properties != null) {
      this.properties = properties;
    }
    return this;
  }
  public SomeNodeFactory withProperty(String key, Object value) {
    this.properties.put(key, value);
    return this;
  }
  public SomeNodeFactory withExternalProperty(String key, Object value) {
    this.eproperties.put(key, value);
    return this;
  }
  public SomeNodeFactory withEntry(String entry) {
    this.uniqueConstraintProperty = "entry";
    this.entry = entry;
    return this;
  }
  public SomeNodeFactory withLabel(String label) {
    labels.add(label);
    return this;
  }
  public SomeNodeFactory withLabel(Label label) {
    labels.add(label.toString());
    return this;
  }
  public SomeNodeFactory withMajorLabel(Label label) {
    this.majorLabel = label.toString();
    return this;
  }
  public SomeNodeFactory withMajorLabel(String label) {
    this.majorLabel = label;
    return this;
  }

  /**
   * Setups any proxy entity
   * @param entity
   */
  private void setupGraphGenericProxyEntity(AbstractGraphNodeEntity entity) {
    setupGraphBaseEntity(entity);
    entity.getProperties().put(Neo4jDefinitions.PROXY_PROPERTY, true);
  }

  /**
   * Setups base entity
   * @param entity
   */
  private void setupGraphGenericEntity(AbstractGraphNodeEntity entity) {
    setupGraphBaseEntity(entity);
    entity.getProperties().put(Neo4jDefinitions.PROXY_PROPERTY, false);
    for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : this.connectedEntities) {
      entity.addConnectedEntity(p);
      //			entity.getConnectedEntities().add(p);
    }
  }

  private void setupGraphBaseEntity(AbstractGraphNodeEntity entity) {

    entity.setProperties(properties);
    entity.setEproperties(eproperties);
    if (this.uniqueConstraintProperty != null) entity.setUniqueKey(uniqueConstraintProperty);
    if (this.entry != null) entity.setEntry(this.entry);
    entity.setMajorLabel(this.majorLabel);
    entity.getLabels().add(this.majorLabel);
    for (String label : this.labels) {
      entity.getLabels().add(label);
    }

  }

  public AbstractGraphNodeEntity buildGenericNodeEntity() {
    AbstractGraphNodeEntity entity = new AbstractGraphNodeEntity();
    setupGraphBaseEntity(entity);
    return entity; 
  }

  public GraphMetaboliteEntity buildGraphMetaboliteProxyEntity(MetaboliteMajorLabel label, Collection<Label> otherLabels) {
    this.majorLabel = label.toString();
    this.labels.add(GlobalLabel.Metabolite.toString());
    GraphMetaboliteEntity entity = new GraphMetaboliteEntity();
    setupGraphGenericProxyEntity(entity);
    entity.getLabels().add(GlobalLabel.Metabolite.toString());
    if (otherLabels != null) {
      for (Label l : otherLabels) {
        entity.addLabel(l.toString());
      }
    }
    return entity;
  }

  public GraphReactionEntity buildGraphReactionProxyEntity(ReactionMajorLabel label) {
    this.majorLabel = label.toString();
    this.labels.add(GlobalLabel.Reaction.toString());
    GraphReactionEntity entity = new GraphReactionEntity();
    setupGraphGenericProxyEntity(entity);
    entity.getLabels().add(GlobalLabel.Reaction.toString());

    return entity;
  }

  public GraphMetaboliteEntity buildGraphMetaboliteEntity(MetaboliteMajorLabel label) {
    this.majorLabel = label.toString();
    this.labels.add(GlobalLabel.Metabolite.toString());
    GraphMetaboliteEntity entity = new GraphMetaboliteEntity();
    setupGraphGenericEntity(entity);
    return entity;
  }
  public GraphReactionEntity buildGraphReactionEntity(ReactionMajorLabel label) {
    this.majorLabel = label.toString();
    this.labels.add(GlobalLabel.Reaction.toString());
    GraphReactionEntity entity = new GraphReactionEntity();
    setupGraphGenericEntity(entity);
    return entity;
  }

  public GraphMetaboliteEntity buildGraphMetabolitePropertyEntity(MetabolitePropertyLabel label, Object property) {
    this.majorLabel = label.toString();
    this.uniqueConstraintProperty = Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT;
    this.properties.put(uniqueConstraintProperty, property);
    this.labels.add(GlobalLabel.MetaboliteProperty.toString());
    GraphMetaboliteEntity entity = new GraphMetaboliteEntity();
    setupGraphBaseEntity(entity);
    return entity;
  }
  public GraphReactionEntity buildGraphReactionPropertyEntity(ReactionPropertyLabel label, Object property) {
    this.majorLabel = label.toString();
    this.uniqueConstraintProperty = Neo4jDefinitions.PROPERTY_NODE_UNIQUE_CONSTRAINT;
    this.properties.put(uniqueConstraintProperty, property);
    this.labels.add(GlobalLabel.ReactionProperty.toString());
    GraphReactionEntity entity = new GraphReactionEntity();
    setupGraphBaseEntity(entity);
    return entity;
  }

  public AbstractGraphEdgeEntity buildEdge(RelationshipType type) {
    this.majorLabel = type.toString();
    AbstractGraphEdgeEntity entity = new AbstractGraphEdgeEntity();
    entity.setProperties(properties);
    entity.setEntry(this.entry);
    entity.getLabels().add(this.majorLabel);
    return entity;
  }

  public AbstractGraphEdgeEntity buildMetaboliteEdge(MetaboliteRelationshipType relationship) {
    return this.buildEdge(relationship);
  }

  public AbstractGraphEdgeEntity buildReactionEdge(ReactionRelationshipType relationship) {
    return this.buildEdge(relationship);
  }

  public SomeNodeFactory withLeftSoitchiometry(String metaboliteEntry, MetaboliteMajorLabel majorLabel, Double stoichiometry) {
    AbstractGraphEdgeEntity edge = new AbstractGraphEdgeEntity();
    edge.getLabels().add(ReactionRelationshipType.left_component.toString());
    edge.getProperties().put("stoichiometry", stoichiometry);
    GraphMetaboliteEntity node = new SomeNodeFactory()
        .withEntry(metaboliteEntry)
        .buildGraphMetaboliteProxyEntity(majorLabel, null);
    return withLinkTo(node, edge);
  }
  public SomeNodeFactory withRightSoitchiometry(String metaboliteEntry, MetaboliteMajorLabel majorLabel, Double stoichiometry) {
    AbstractGraphEdgeEntity edge = new AbstractGraphEdgeEntity();
    edge.getLabels().add(ReactionRelationshipType.right_component.toString());
    edge.getProperties().put("stoichiometry", stoichiometry);
    GraphMetaboliteEntity node = new SomeNodeFactory()
        .withEntry(metaboliteEntry)
        .buildGraphMetaboliteProxyEntity(majorLabel, null);
    return withLinkTo(node, edge);
  }
}
