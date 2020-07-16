package pt.uminho.sysbio.biosynthframework.io.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public abstract class AbstractNeo4jBiosDao<T extends AbstractBiosynthEntity> implements BiosDao<T> {
  
  protected final BiodbGraphDatabaseService service;
  private final Label database;
  
  public AbstractNeo4jBiosDao(GraphDatabaseService graphDatabaseService, Label database) {
    this.service = new BiodbGraphDatabaseService(graphDatabaseService);
    this.database = database;
  }
  
  public abstract T convert(Node node);
  public abstract Map<String, Object> getProperties(T o);
  
  private Map<String, Object> clean(Map<String, Object> p) {
    Map<String, Object> result = new HashMap<>();
    
    for (String k : p.keySet()) {
      Object v = p.get(k);
      if (v instanceof String) {
        if (DataUtils.empty(v)) {
          v = null;
        } else {
          v = v.toString().trim();
        }
      }
      
      if (v != null) {
        result.put(k, v);
      }
    }
    
    return result;
  }
  
  @Override
  public T getByEntry(String e) {
    Node node = service.getNodeByEntryAndLabel(e, database);
    if (node != null) {
      return convert(node);
    }
    return null;
  }

  @Override
  public T getById(long id) {
    Node node = service.getNodeById(id);
    if (node != null) {
      return convert(node);
    }
    return null;
  }
  
  @Override
  public Long save(T o) {
    Node node = null;

    try {
      node = service.getOrCreateNode(database, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, o.getEntry());
      Map<String, Object> properties = getProperties(o);
      properties = clean(properties);
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.MAJOR_LABEL_PROPERTY, database.toString());
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
      Neo4jUtils.setUpdatedTimestamp(node);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    if (node == null) {
      return null;
    }
    
    return node.getId();
  }
  
  public Set<Node> getAllNodes() {
    Set<Node> nodes = service.listNodes(database);
    return nodes;
  }

  @Override
  public Set<Long> getAllIds() {
    Set<Node> nodes = getAllNodes();
    Set<Long> ids = new HashSet<>();
    for (Node n : nodes) {
      ids.add(n.getId());
    }
    return ids;
  }
  
  @Override
  public Set<String> getAllEntries() {
    Set<Node> nodes = getAllNodes();
    Set<String> entries = new HashSet<>();
    for (Node n : nodes) {
      entries.add((String) n.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT));
    }
    return entries;
  }
}
