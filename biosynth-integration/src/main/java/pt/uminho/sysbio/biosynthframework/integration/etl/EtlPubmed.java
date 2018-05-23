package pt.uminho.sysbio.biosynthframework.integration.etl;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynthframework.biodb.eutils.PubmedEntity;
import pt.uminho.sysbio.biosynthframework.io.EutilsPubmedDao;
import pt.uminho.sysbio.biosynthframework.io.neo4j.Neo4jLiteratureDao;

public class EtlPubmed extends AbstractNeo4jEtl {

  private Neo4jLiteratureDao dao;
  private final EutilsPubmedDao daoSrc;
  
  public EtlPubmed(GraphDatabaseService service) {
    super(service);
    this.dao = new Neo4jLiteratureDao(service);
    this.daoSrc = new EutilsPubmedDao();
  }

  @Override
  public void etl(String entry) {
    PubmedEntity e = daoSrc.getByEntry(entry);
    
    if (e != null) {
      dao.save(e);
    }
  }

}
