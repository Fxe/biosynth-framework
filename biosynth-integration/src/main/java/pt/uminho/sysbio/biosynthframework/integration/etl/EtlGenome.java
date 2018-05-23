package pt.uminho.sysbio.biosynthframework.integration.etl;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsAssemblyObject;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.RefseqGenomeEntity;
import pt.uminho.sysbio.biosynthframework.io.biodb.ncbi.EutilsNcbiAssemblyDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.neo4j.Neo4jNcbiRefseqGenomeDaoImpl;

public class EtlGenome extends AbstractNeo4jEtl {

  public EtlGenome(GraphDatabaseService service) {
    super(service);
  }

  @Override
  public void etl(String entry) {
    Set<String> failboat = new HashSet<>();
    Neo4jNcbiRefseqGenomeDaoImpl refseqDao = new Neo4jNcbiRefseqGenomeDaoImpl(service);
    EutilsNcbiAssemblyDaoImpl dao = new EutilsNcbiAssemblyDaoImpl();
    EutilsAssemblyObject o = dao.getByEntry(entry);
    if (o != null) {
      RefseqGenomeEntity genome = Neo4jNcbiRefseqGenomeDaoImpl.convert(o);
      refseqDao.save(genome);
    } else {
      failboat.add(entry);
    }
  }
}
