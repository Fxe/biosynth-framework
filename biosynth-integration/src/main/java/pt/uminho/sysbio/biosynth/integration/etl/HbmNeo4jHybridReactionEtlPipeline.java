package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.Reaction;

public class HbmNeo4jHybridReactionEtlPipeline<SRC extends Reaction, DST extends Reaction>
implements EtlPipeline<SRC, DST> {

  private static final Logger logger = LoggerFactory.getLogger(HbmNeo4jHybridReactionEtlPipeline.class);

  private SessionFactory sessionFactory;
  private GraphDatabaseService graphDatabaseService;

  private EtlDataCleansing<DST> dataCleasingSubsystem;
  private EtlExtract<SRC> extractSubsystem;
  private EtlTransform<SRC, DST> transformSubsystem;
  private EtlLoad<DST> loadSubsystem;

  private int batchSize = 100;
  private boolean skipLoad = false;

  @Autowired
  public HbmNeo4jHybridReactionEtlPipeline(SessionFactory sessionFactory, 
                                           GraphDatabaseService graphDatabaseService) {
    this.sessionFactory = sessionFactory;
    this.graphDatabaseService = graphDatabaseService;
  }

  public SessionFactory getSessionFactory() { return sessionFactory;}
  public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}

  public GraphDatabaseService getGraphDatabaseService() { return graphDatabaseService;}
  public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) { this.graphDatabaseService = graphDatabaseService;}

  public int getBatchSize() { return batchSize;}
  public void setBatchSize(int batchSize) { this.batchSize = batchSize;}

  public boolean isSkipLoad() { return skipLoad;}
  public void setSkipLoad(boolean skipLoad) { this.skipLoad = skipLoad;}

  @Override
  public void setEtlDataCleasingSubsystem(
      EtlDataCleansing<DST> dataCleasingSubsystem) {
    this.dataCleasingSubsystem = dataCleasingSubsystem;
  }

  @Override
  public void setExtractSubsystem(EtlExtract<SRC> extractSubsystem) {
    this.extractSubsystem = extractSubsystem;
  }

  @Override
  public void setTransformSubsystem(EtlTransform<SRC, DST> transformSubsystem) {
    this.transformSubsystem = transformSubsystem;
  }

  @Override
  public void setLoadSubsystem(EtlLoad<DST> loadSubsystem) {
    this.loadSubsystem = loadSubsystem;
  }

  @Override
  public void etl(Serializable id) {
    //SRC = ETL EXTRACT(Entry)
    SRC src = extractSubsystem.extract(id);
    if (src == null) {
      logger.warn(String.format("Unable to extract entity %s", id));
      return;
    }

    //DST = ETL TRANSFORM(SRC)
    DST dst = transformSubsystem.etlTransform(src);

    if (this.dataCleasingSubsystem != null) {
      this.dataCleasingSubsystem.etlCleanse(dst);
    }

    //ETL LOAD(DST)
    if (!skipLoad && loadSubsystem != null) {
      loadSubsystem.etlLoad(dst);
    }
  }

  @Override
  public void etl() {
    if (this.dataCleasingSubsystem == null) {
      logger.warn("No data cleasing system attached");
    }

    org.hibernate.Transaction hbmTx = null;
    org.neo4j.graphdb.Transaction neoTx = null;
    if (sessionFactory != null) {
      hbmTx = sessionFactory.getCurrentSession().beginTransaction();
    }
    if (graphDatabaseService != null) {
      neoTx = graphDatabaseService.beginTx();
    }

    int i = 0;
    for (Serializable entry : extractSubsystem.getAllKeys()) {
      try {
        etl(entry);
      } catch (Exception e) {
        logger.error("{} - {}", entry, e.getMessage());
      }

      i++;
      if ((i % batchSize) == 0) {
        logger.debug(String.format("Commit ! %d", i));
        if (graphDatabaseService != null) {
          neoTx.success();
          neoTx.close();
          neoTx = graphDatabaseService.beginTx();
        }

        if (sessionFactory != null) {
          hbmTx.rollback();
          hbmTx = sessionFactory.getCurrentSession().beginTransaction();
        }
      }
    }

    if (sessionFactory != null) {
      hbmTx.rollback();
    }
    
    if (graphDatabaseService != null) {
      neoTx.success();
      neoTx.close();
    }
  }
}
