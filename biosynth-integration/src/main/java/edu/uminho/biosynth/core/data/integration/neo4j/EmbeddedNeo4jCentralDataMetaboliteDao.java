package edu.uminho.biosynth.core.data.integration.neo4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteProxyEntity;
import pt.uminho.sysbio.biosynth.integration.GraphRelationshipEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteRelationshipType;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

/**
 * Embedded Neo4j DAO 
 * 
 * @author Filipe Liu
 *
 */
@Deprecated
public class EmbeddedNeo4jCentralDataMetaboliteDao implements MetaboliteDao<GraphMetaboliteEntity> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedNeo4jCentralDataMetaboliteDao.class);

  @Autowired
  private GraphDatabaseService graphDatabaseService;

  @Deprecated
  protected boolean exists(Label label, String key, String value) {
    //	  Neo4jUtils
    //		return !IteratorUtil.asCollection(graphDatabaseService
    //				.findNodesByLabelAndProperty(label, key, value)).isEmpty();
    return false;
  }

  @Deprecated
  protected Node getOrCreateNode(Label label, String key, String value) {
    //		Collection<Node> nodes = IteratorUtil.asCollection(graphDatabaseService
    //				.findNodesByLabelAndProperty(label, key, value));
    Collection<Node> nodes = new HashSet<> ();
    if (nodes.isEmpty()) {
      return graphDatabaseService.createNode();
    }

    if (nodes.size() > 1) {
      LOGGER.warn(String.format("Label %s with property %s:%s does not hold uniqueness", label, key, value));
    }

    return nodes.iterator().next();
  }

  /**
   * 
   * @param node the graph node to be updated.
   * @param properties the properties to be updated.
   */
  private void updateNode(Node node, Map<String, Object> properties) {
    for (String key : properties.keySet()) {
      node.setProperty(key, properties.get(key));
    }
  }

  private void updateRelationship(Relationship relationship, Map<String, Object> properties) {
    for (String key : properties.keySet()) {
      relationship.setProperty(key, properties.get(key));
    }
  }

  @Override
  public GraphMetaboliteEntity getMetaboliteById(Serializable id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GraphMetaboliteEntity getMetaboliteByEntry(String entry) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GraphMetaboliteEntity saveMetabolite(
      GraphMetaboliteEntity metabolite) {

    Label major = DynamicLabel.label(metabolite.getMajorLabel());
    Node node = this.getOrCreateNode(major, "entry", metabolite.getEntry());
    node.setProperty("proxy", false);

    node.setProperty("entry", metabolite.getEntry());
    for (String label : metabolite.getLabels()) {
      Label l = DynamicLabel.label(label);
      node.addLabel(l);
    }
    updateNode(node, metabolite.getProperties());

    metabolite.setId(node.getId());

    //		for (CentralMetabolitePropertyEntity propertyEntity : metabolite.getPropertyEntities()) {
    //			Label propertyMajor = DynamicLabel.label(propertyEntity.getMajorLabel());
    //			String uniqueKey = (String) propertyEntity.getUniqueKey();
    //			String uniqueValue = (String) propertyEntity.getProperties().get(uniqueKey);
    //			Node propertyNode = this.getOrCreateNode(propertyMajor, uniqueKey, uniqueValue);
    //			this.updateNode(propertyNode, propertyEntity.getProperties());
    //			propertyEntity.setId(propertyNode.getId());
    //			RelationshipType relationshipType = DynamicRelationshipType.withName(propertyEntity.getRelationshipMajorLabel());
    //			node.createRelationshipTo(propertyNode, relationshipType);
    //		}

    for (Pair<GraphMetaboliteProxyEntity, GraphRelationshipEntity> xrefPair : metabolite.getCrossreferences()) {
      GraphMetaboliteProxyEntity xref = xrefPair.getLeft();
      Label xrefMajor = DynamicLabel.label(xref.getMajorLabel());

      Node xrefNode = this.getOrCreateNode(xrefMajor, "entry", xref.getEntry());
      if ( !xrefNode.hasProperty("proxy")) xrefNode.setProperty("proxy", true);

      RelationshipType relationshipType = MetaboliteRelationshipType.has_crossreference_to;
      Relationship relationship = node.createRelationshipTo(xrefNode, relationshipType);
      this.updateRelationship(relationship, xref.getProperties());
    }

    return metabolite;
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Serializable save(GraphMetaboliteEntity entity) {
    // TODO Auto-generated method stub
    return null;
  }

}
