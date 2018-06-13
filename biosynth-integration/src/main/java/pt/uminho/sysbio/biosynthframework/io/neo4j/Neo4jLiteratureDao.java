package pt.uminho.sysbio.biosynthframework.io.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GenericRelationship;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.PubmedEntity;
import pt.uminho.sysbio.biosynthframework.neo4j.LiteratureDatabase;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class Neo4jLiteratureDao extends AbstractNeo4jBiosDao<PubmedEntity> {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jLiteratureDao.class);
  
  public Neo4jLiteratureDao(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService, LiteratureDatabase.PubMed);
  }
  
  public List<SupplementaryMaterialEntity> getLiteratureSupplementaryMaterials(PubmedEntity pubmed) {
    Node litNode = service.getNodeById(pubmed.getId());
    if (litNode == null) {
      return null;
    }
    
    List<SupplementaryMaterialEntity> result = new ArrayList<>();
    for (Node supNode : Neo4jUtils.collectNodeRelationshipNodes(litNode, GenericRelationship.has_supplementary_file)) {
      SupplementaryMaterialEntity sup = convertSup(supNode);
      
      for (Node inner : Neo4jUtils.collectNodeRelationshipNodes(supNode, GenericRelationship.has_file)) {
        SupplementaryMaterialEntity innerSup = convertSup(inner);
        sup.files.add(innerSup);
      }
      result.add(sup);
    }
    
    return result;
  }
  
  public Long save(PubmedEntity pubmed, SupplementaryMaterialEntity o) {
    Node litNode = service.getNodeById(pubmed.getId());
    if (litNode == null) {
      return null;
    }
    
    String entry = String.format("%s/%s", pubmed.getEntry(), o.getFile().getName());
    Node node = service.getOrCreateNode(LiteratureDatabase.SupplementaryMaterial, 
                                        Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
                                        entry);
    
    node.setProperty("url", o.getUrl().toString());
    node.setProperty("size", o.getSize());
    node.setProperty("file", o.getFile().getName());
    node.setProperty("md5", o.getMd5());
    
    if (!Neo4jUtils.exitsRelationshipBetween(litNode, node, Direction.BOTH)) {
      Relationship r = litNode.createRelationshipTo(node, GenericRelationship.has_supplementary_file);
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
    }
    
    return node.getId();
  }
  
  public Long save(SupplementaryMaterialEntity zip, SupplementaryMaterialEntity o) {
    Node supMasterNode = service.getNodeById(zip.getId());
    if (supMasterNode == null) {
      return null;
    }
    
    String entry = String.format("%s/%s", supMasterNode.getProperty("entry"), o.getName());
    Node node = service.getOrCreateNode(LiteratureDatabase.SupplementaryMaterial, 
                                        Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
                                        entry);
    
    node.setProperty("size", o.getSize());
    node.setProperty("file", o.getName());
    node.setProperty("md5", o.getMd5());
    node.setProperty("parent_file", zip.getId());
    
    if (!Neo4jUtils.exitsRelationshipBetween(supMasterNode, node, Direction.BOTH)) {
      Relationship r = supMasterNode.createRelationshipTo(node, GenericRelationship.has_file);
      Neo4jUtils.setCreatedTimestamp(r);
      Neo4jUtils.setUpdatedTimestamp(r);
    }
    
    return node.getId();
  }
  
  @Override
  public Long save(PubmedEntity o) {
    Node node = null;

    try {
      node = service.getOrCreateNode(LiteratureDatabase.PubMed, 
          Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, 
          o.getEntry());
      Map<String, Object> properties = getProperties(o);
      Neo4jUtils.setPropertiesMap(properties, node);
      node.setProperty(Neo4jDefinitions.PROXY_PROPERTY, false);
      Neo4jUtils.setUpdatedTimestamp(node);
      
      logger.trace("saved");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    if (node == null) {
      return null;
    }
    
    return node.getId();
  }
  
  public SupplementaryMaterialEntity convertSup(Node node) {
    long id = node.getId();
    String entry = (String) node.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT);
    String url = (String) node.getProperty("url", null);
    Long size = (Long) node.getProperty("size", null);
    String file = (String) node.getProperty("file", null);
    String md5 = (String) node.getProperty("md5", null);
    String type = (String) node.getProperty("type", null);
    String tags = (String) node.getProperty("tags", null);

    SupplementaryMaterialEntity entity = new SupplementaryMaterialEntity();
    entity.setId(id);
    entity.setEntry(entry);
    entity.setUrl(url);
    entity.setSize(size);
    entity.setMd5(md5);
    entity.setName(file);
    entity.setTags(tags);
    entity.setType(type);
    return entity;
  }
  
  @Override
  public PubmedEntity convert(Node node) {
    long id = node.getId();
    String entry = (String) node.getProperty(Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT);
    String journal = (String) node.getProperty("journal", null);
    String journalAbbr = (String) node.getProperty("journal_abbreviation", null);
    String title = (String) node.getProperty("title", null);
    String source = (String) node.getProperty("source", null);
    String doi = (String) node.getProperty("doi", null);
    PubmedEntity entity = new PubmedEntity();
    entity.setId(id);
    entity.setEntry(entry);
    entity.setJournal(journal);
    entity.setJournalAbbreviation(journalAbbr);
    entity.setTitle(title);
    entity.setSource(source);
    entity.setDoi(doi);
    return entity;
  }
  
  @Override
  public Map<String, Object> getProperties(PubmedEntity o) {
    Map<String, Object> properties = new HashMap<> ();
    if (!DataUtils.empty(o.getJournal())) {
      properties.put("journal", o.getJournal());
    }
    if (!DataUtils.empty(o.getJournalAbbreviation())) {
      properties.put("journal_abbreviation", o.getJournalAbbreviation());
    }
    if (!DataUtils.empty(o.getSource())) {
      properties.put("source", o.getSource());
    }
    if (!DataUtils.empty(o.getDoi())) {
      properties.put("doi", o.getDoi());
    }
    if (!DataUtils.empty(o.getTitle())) {
      properties.put("title", o.getTitle());
    }
    return properties;
  }

  @Override
  public boolean delete(PubmedEntity o) {
    throw new RuntimeException("not supported");
  }

}
