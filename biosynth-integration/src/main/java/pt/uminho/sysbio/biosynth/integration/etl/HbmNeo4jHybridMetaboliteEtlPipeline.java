package pt.uminho.sysbio.biosynth.integration.etl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pt.uminho.sysbio.biosynthframework.Metabolite;

public class HbmNeo4jHybridMetaboliteEtlPipeline<SRC extends Metabolite, DST extends Metabolite>
implements EtlPipeline<SRC, DST> {

  private static final Logger logger = LoggerFactory.getLogger(HbmNeo4jHybridMetaboliteEtlPipeline.class);

  private SessionFactory sessionFactory;
  private GraphDatabaseService graphDatabaseService;

  //	private MetaboliteDao<SRC> daoSrc;

  private EtlDataCleansing<DST> dataCleasingSubsystem;
  private EtlExtract<SRC> etlExtract;
  private EtlTransform<SRC, DST> etlTransform;
  private EtlLoad<DST> etlLoad;
  public Set<String> exclude = new HashSet<> ();

  private int batchSize = 100;
  private boolean skipLoad = false;

  public HbmNeo4jHybridMetaboliteEtlPipeline() { }

  @Autowired
  public HbmNeo4jHybridMetaboliteEtlPipeline(SessionFactory sessionFactory, 
                                             GraphDatabaseService graphDatabaseService) {
    this.sessionFactory = sessionFactory;
    this.graphDatabaseService = graphDatabaseService;
  }

  public SessionFactory getSessionFactory() { return sessionFactory;}
  public void setSessionFactory(SessionFactory sessionFactory) { this.sessionFactory = sessionFactory;}

  public GraphDatabaseService getGraphDatabaseService() { return graphDatabaseService;}
  public void setGraphDatabaseService(GraphDatabaseService graphDatabaseService) { this.graphDatabaseService = graphDatabaseService;}

  //	public MetaboliteDao<SRC> getDaoSrc() { return daoSrc;}
  //	public void setDaoSrc(MetaboliteDao<SRC> daoSrc) { this.daoSrc = daoSrc;}

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
  public void setExtractSubsystem(EtlExtract<SRC> etlExtract) { this.etlExtract = etlExtract;}

  @Override
  public void setTransformSubsystem(EtlTransform<SRC, DST> etlTransform) { this.etlTransform = etlTransform;}

  @Override
  public void setLoadSubsystem(EtlLoad<DST> loadSubsystem) { this.etlLoad = loadSubsystem;}

  @Override
  public void etl(Serializable id) {
    //		System.out.println("DFGSGSDGSD");
    //		for (Node omg : GlobalGraphOperations.at(graphDatabaseService).getAllNodes()) {
    //			
    //			System.out.println(omg);
    //		}
    //SRC = ETL EXTRACT(Entry)
    SRC src = etlExtract.extract(id);
    
    if (src == null) {
      logger.warn("Unable to load {}", id);
      return;
    }

    //DST = ETL TRANSFORM(SRC)
    DST dst = etlTransform.etlTransform(src);

    //ETL CLEAN(DST)
    if (this.dataCleasingSubsystem != null) {
      dataCleasingSubsystem.etlCleanse(dst);
    }

    //ETL LOAD(DST)
    if (!skipLoad && etlLoad != null) {
      etlLoad.etlLoad(dst);
    }
  }

  @Override
  public void etl() {
    
    org.hibernate.Transaction hbmTx = null;
    org.neo4j.graphdb.Transaction neoTx = null;
    if (sessionFactory != null) {
      hbmTx = sessionFactory.getCurrentSession().beginTransaction();
    }
    if (graphDatabaseService != null) {
      neoTx = graphDatabaseService.beginTx();
    }
     

    int i = 0;
    for (Serializable entry : etlExtract.getAllKeys()) {
      try {
        if (!exclude.contains(entry.toString())) {
          etl(entry);
          i++;
        }
      } catch (Exception e) {
        logger.error("{} - {}", entry, e.getMessage());
      }

      
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

  public void etl(Collection<Serializable> ids) {

    org.hibernate.Transaction hbmTx = sessionFactory.getCurrentSession().beginTransaction();
    org.neo4j.graphdb.Transaction neoTx = graphDatabaseService.beginTx();

    int i = 0;
    for (Serializable id : ids) {
      try {
        if (!exclude.contains(id.toString())) {
          etl(id);
          i++;
        }
      } catch (Exception e) {
        logger.error("{} - {}", id, e.getMessage());
      }

      
      if ((i % batchSize) == 0) {
        logger.debug(String.format("Commit ! %d", i));
        neoTx.success();
        neoTx.close();
        neoTx = graphDatabaseService.beginTx();

        hbmTx.rollback();
        hbmTx = sessionFactory.getCurrentSession().beginTransaction();
      }
    }

    hbmTx.rollback();
    neoTx.success();
    neoTx.close();
  }

}