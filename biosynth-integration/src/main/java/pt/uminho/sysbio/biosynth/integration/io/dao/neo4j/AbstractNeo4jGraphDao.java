package pt.uminho.sysbio.biosynth.integration.io.dao.neo4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.util.BiosIOUtils;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;
import pt.uminho.sysbio.biosynthframework.util.MapUtils;

public class AbstractNeo4jGraphDao<E extends AbstractGraphNodeEntity> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractNeo4jGraphDao.class);

  protected BiodbGraphDatabaseService graphDatabaseService;
  
  public static int MAX_KEY_SIZE = 4000;
  public String databasePath;

  public AbstractNeo4jGraphDao(GraphDatabaseService graphDatabaseService) {
    this.graphDatabaseService = new BiodbGraphDatabaseService(graphDatabaseService);
    if (graphDatabaseService instanceof BiodbGraphDatabaseService) {
      this.databasePath = ((BiodbGraphDatabaseService)graphDatabaseService).databasePath;
    }
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

  public boolean isUpdate(AbstractGraphNodeEntity entity) {
    Node node = find(entity);
    
    if (node != null && 
        node.hasProperty(Neo4jDefinitions.ENTITY_VERSION) &&
        !DataUtils.empty(entity.getVersion()) &&
        !entity.getVersion().equals(node.getProperty(Neo4jDefinitions.ENTITY_VERSION))) {
      return true;
    }
    
    return false;
  }
  
  public Long update(AbstractGraphNodeEntity entity) {
    Node prev = find(entity);
    Map<String, Object> beforeData = prev.getAllProperties();
    Set<String> special = new HashSet<>();
    special.add(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT);
    special.add(Neo4jDefinitions.MAJOR_LABEL_PROPERTY);
    special.add(Neo4jDefinitions.PROXY_PROPERTY);
    special.add(Neo4jDefinitions.CREATED_AT);
    special.add(Neo4jDefinitions.UPDATED_AT);

    Map<String, Object> data = entity.getProperties();
    Map<String, Object> keep = MapUtils.diff1(beforeData, data);
    
    //prev set as version
    //keep changed and lost
    //mk new
    for (Label l : prev.getLabels()) {
      prev.removeLabel(l);
    }
    prev.addLabel(GlobalLabel.VERSION);
    for (String key : prev.getPropertyKeys()) {
      if (!keep.containsKey(key) && !special.contains(key)) {
        prev.removeProperty(key);
      }
    }
    
    logger.debug("Version Node: {} {}", prev, Neo4jUtils.getLabels(prev));
    logger.debug("Version Node: {} {}", prev, prev.getAllProperties());
    
    Long latest = saveGraphEntity(entity);
    
    Node n = graphDatabaseService.getNodeById(latest);
    Set<Long> s1 = new HashSet<>();
    Set<Long> s2 = new HashSet<>();
    for (Relationship r : prev.getRelationships()) {
      Node other = r.getOtherNode(prev);
      s1.add(other.getId());
      logger.debug("[P] {} -> {}", r.getType().name(), other);
    }
    for (Relationship r : n.getRelationships()) {
      Node other = r.getOtherNode(n);
      s2.add(other.getId());
      logger.debug("[*] {} -> {}", r.getType().name(), other);
    }
    Set<Long> sboth = Sets.intersection(s1, s2);
    for (Relationship r : prev.getRelationships()) {
      Node other = r.getOtherNode(prev);
      if (sboth.contains(other.getId())) {
        logger.debug("D[P] {} -> {}", r.getType().name(), other);
//        r.delete();
      } else {
        logger.debug("K[P] {} -> {}", r.getType().name(), other);
      }
    }
    n.createRelationshipTo(prev, GenericRelationship.has_version);
    return latest;    
  }
  
  public Node find(AbstractGraphNodeEntity e) {
    Node node = null;
    if (e.getId() != null) {
      node = graphDatabaseService.getNodeById(e.getId());
    } else {
      String nsStr = e.getMajorLabel();
      Label ns = Neo4jUtils.buildNamespace(nsStr);
      Object value = e.getProperty(e.getUniqueKey(), null);
      node = graphDatabaseService.findNode(ns, e.getUniqueKey(), value);
    }
    
    return node;
  }
  
  protected Long saveGraphEntity(AbstractGraphNodeEntity entity) {
    if (isUpdate(entity)) {
      logger.debug("version: {} -> {}", entity.getEntry(), entity.getVersion());
      return update(entity);
    }
    
    String key = entity.getUniqueKey();
    Object kvalue = entity.getProperty(key, null);
    if (kvalue == null) {
      logger.warn("null index key: {} {}", key, entity.getLabels());
      return null;
    }
    
    if (kvalue.toString().length() > MAX_KEY_SIZE) {
      logger.warn("key size > {}: {}", MAX_KEY_SIZE, kvalue);
      
      entity.addLabel(GlobalLabel.SHA256_KEY.toString());
      String sha = BiosIOUtils.getSHA256(kvalue.toString());
      entity.addProperty("bios_key_value", kvalue);
      entity.addProperty(key, sha);
      kvalue = sha;
    }
    
    logger.debug("merge: {} -> {}", entity.getEntry(), entity.getVersion());
    
    Node node = find(entity);
//    if (entity.getId() != null) {
//      logger.trace("Lookup previous id[{}] ...", entity.getId());
//      node = graphDatabaseService.getNodeById(entity.getId());
//    } else if (entity.getUniqueKey() != null && 
//        
//        entity.getProperty(entity.getUniqueKey(), null) != null && 
//        entity.getMajorLabel() != null){
//      logger.trace("Lookup previous [{}:{}] non zero looking for existing node", entity.getMajorLabel(), entity.getProperty(entity.getUniqueKey(), null));
//      Object uniqueContraintValue = entity.getProperty(entity.getUniqueKey(), null);
//      node = Neo4jUtils.getUniqueResult(graphDatabaseService.findNodes(Neo4jUtils.buildNamespace(entity.getMajorLabel()), entity.getUniqueKey(), uniqueContraintValue));
//    }

    if (node == null) {
      logger.trace("Previous node not found generating new node...");
      node = graphDatabaseService.createNode();
      Neo4jUtils.setCreatedTimestamp(node);
      Neo4jUtils.setUpdatedTimestamp(node);
    }
    
    logger.trace("Selected node: " + node);
    entity.setId(node.getId());
    
    if (!entity.getEproperties().isEmpty()) {
      if (databasePath != null) {
        ObjectMapper om = new ObjectMapper();
        File dataFile = new File(this.databasePath + "/" + node.getId() + ".json");
        try (OutputStream os = new FileOutputStream(dataFile)) {
          om.writeValue(os, entity.getEproperties());
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        logger.warn("no database path is set to store external properties (must be assigned at DAO)");
      }
    }

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
    
    if (!DataUtils.empty(entity.getVersion())) {
      node.setProperty(Neo4jDefinitions.ENTITY_VERSION, entity.getVersion());
    }

    logger.debug("Saving node connected entities ...");
    for (String relationshipType : entity.getConnectedEntities().keySet()) {
      logger.debug("Processing " + relationshipType + " ...");
      List<Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity>> pairs = entity.getConnectedEntities().get(relationshipType);
      for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> pair : pairs) {
        AbstractGraphEdgeEntity edgeEntity = pair.getLeft();
        AbstractGraphNodeEntity nodeEntity = pair.getRight();

        Long id = this.saveGraphEntity(nodeEntity);
        if (id != null) {
          Node otherNode = graphDatabaseService.getNodeById(nodeEntity.getId());
          if (!Neo4jUtils.exitsRelationshipBetween(node, otherNode, Direction.BOTH, DynamicRelationshipType.withName(relationshipType))) {
            Relationship relationship = node.createRelationshipTo(otherNode, DynamicRelationshipType.withName(relationshipType));
            logger.trace(String.format("Connected %s:%s -[:%s]-> %s:%s", 
                node, Neo4jUtils.getLabels(node), relationshipType, 
                otherNode, Neo4jUtils.getLabels(otherNode)));
            Neo4jUtils.setPropertiesMap(edgeEntity.getProperties(), relationship);
            Neo4jUtils.setCreatedTimestamp(relationship);
            Neo4jUtils.setUpdatedTimestamp(relationship);
          }
        } else {
          logger.warn("unable to save node: {}", nodeEntity.getLabels());
        }
      }
    }
    return node.getId();
  }
}
