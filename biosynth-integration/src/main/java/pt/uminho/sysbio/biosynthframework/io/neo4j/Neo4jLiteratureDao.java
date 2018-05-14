package pt.uminho.sysbio.biosynthframework.io.neo4j;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jUtils;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.PubmedEntity;
import pt.uminho.sysbio.biosynthframework.neo4j.LiteratureDatabase;
import pt.uminho.sysbio.biosynthframework.util.DataUtils;

public class Neo4jLiteratureDao extends AbstractNeo4jBiosDao<PubmedEntity> {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jLiteratureDao.class);
  
  public Neo4jLiteratureDao(GraphDatabaseService graphDatabaseService) {
    super(graphDatabaseService, LiteratureDatabase.PubMed);
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
