package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;

public class AbstractNeo4jGraphDao<E extends AbstractGraphNodeEntity> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractNeo4jGraphDao.class);

  protected GraphDatabaseService graphDatabaseService;
  protected ExecutionEngine executionEngine;

  public AbstractNeo4jGraphDao(GraphDatabaseService graphDatabaseService) {
    this.graphDatabaseService = graphDatabaseService;
    this.executionEngine = new ExecutionEngine(graphDatabaseService);
  }

  //	protected<T extends AbstractGraphNodeEntity> AbstractGraphNodeEntity getGraphNodeEntity(long id, Class<T> clazz) {
  //		AbstractGraphNodeEntity nodeEntity = null;
  //		Node node = null;
  //		try {
  //			nodeEntity = clazz.newInstance();
  //			Neo4jMapper.nodeToAbstractGraphNodeEntity(nodeEntity, node);
  //		} catch (InstantiationException | IllegalAccessException e1) {
  //			// TODO Auto-generated catch block
  //			e1.printStackTrace();
  //		}
  //		return nodeEntity;
  //	}

  protected void saveGraphEntity(AbstractGraphNodeEntity entity) {
    Node node = null;

    if (entity.getId() != null) {
      logger.trace("Lookup previous id[{}] ...", entity.getId());
      node = graphDatabaseService.getNodeById(entity.getId());
    } else if (entity.getUniqueKey() != null && 
        entity.getProperty(entity.getUniqueKey(), null) != null && 
        entity.getMajorLabel() != null){
      logger.trace("Lookup previous [{}:{}] non zero looking for existing node", entity.getMajorLabel(), entity.getProperty(entity.getUniqueKey(), null));
      Object uniqueContraintValue = entity.getProperty(entity.getUniqueKey(), null);
      node = Neo4jUtils.getUniqueResult(graphDatabaseService.findNodesByLabelAndProperty(DynamicLabel.label(entity.getMajorLabel()), entity.getUniqueKey(), uniqueContraintValue));
    }


    if (node == null) {
      logger.trace("Previous node not found generating new node...");
      node = graphDatabaseService.createNode();
    }
    
    logger.trace("Selected node: " + node);
    entity.setId(node.getId());

    logger.debug("Saving properties ...");
    try {
      if (node.hasProperty("proxy") 
          && node.getProperty("proxy").equals(false)
          && entity.getProperty("proxy", null) != null
          && entity.getProperty("proxy", null).equals(true)) {
        entity.getProperties().put("proxy", false);
      }
      Neo4jUtils.setPropertiesMap(entity.getProperties(), node);
    } catch (RuntimeException e) {
      logger.error(entity.getEntry() + " "  + entity.getLabels() + " " + "Property error " + entity.getProperties() + " - " + e.getMessage());
      throw e;
    }

    logger.debug("Saving node labels ...");
    for (String label : entity.getLabels()) {
      logger.trace("Add label: " + label);
      node.addLabel(DynamicLabel.label(label));
    }
    if (entity.getMajorLabel() != null) {
      logger.trace("Add label: " + entity.getMajorLabel());
      node.addLabel(DynamicLabel.label(entity.getMajorLabel()));
      logger.trace(String.format("Assign property - %s:%s", Neo4jDefinitions.MAJOR_LABEL_PROPERTY, entity.getMajorLabel()));
      node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, entity.getMajorLabel());
    }


    logger.debug("Saving node connected entities ...");
    for (String relationshipType : entity.getConnectedEntities().keySet()) {
      logger.debug("Processing " + relationshipType + " ...");
      List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> pairs = entity.getConnectedEntities().get(relationshipType);
      for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> pair : pairs) {
        AbstractGraphEdgeEntity edgeEntity = pair.getLeft();
        AbstractGraphNodeEntity nodeEntity = pair.getRight();

        this.saveGraphEntity(nodeEntity);

        Node otherNode = graphDatabaseService.getNodeById(nodeEntity.getId());
        //				String relationshipType = edgeEntity.getLabels().iterator().next();
        //			if (node.getR)
        Relationship relationship = node.createRelationshipTo(otherNode, DynamicRelationshipType.withName(relationshipType));
        logger.trace(String.format("Connected %s:%s -[:%s]-> %s:%s", 
            node, Neo4jUtils.getLabels(node), relationshipType, 
            otherNode, Neo4jUtils.getLabels(otherNode)));
        Neo4jUtils.setPropertiesMap(edgeEntity.getProperties(), relationship);
      }
    }

  }
}
