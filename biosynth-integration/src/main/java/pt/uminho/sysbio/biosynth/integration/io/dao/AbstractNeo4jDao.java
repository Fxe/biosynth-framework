package pt.uminho.sysbio.biosynth.integration.io.dao;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.BiodbGraphDatabaseService;

public abstract class AbstractNeo4jDao {

  private static final Logger logger = LoggerFactory.getLogger(AbstractNeo4jDao.class);

  protected final BiodbGraphDatabaseService graphDatabaseService;

  public AbstractNeo4jDao(GraphDatabaseService graphDatabaseService) {
    logger.debug("initialized");
    this.graphDatabaseService = new BiodbGraphDatabaseService(graphDatabaseService);
  }

  public GraphDatabaseService getGraphDatabaseService() {
    return graphDatabaseService;
  }

  protected Node getNode(AbstractBiosynthEntity entity) {
    return graphDatabaseService.getNodeById(entity.getId());
  }
}
