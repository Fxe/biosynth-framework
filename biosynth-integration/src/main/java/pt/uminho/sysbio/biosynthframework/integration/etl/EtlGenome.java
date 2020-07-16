package pt.uminho.sysbio.biosynthframework.integration.etl;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.GlobalLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetabolicModelLabel;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.Neo4jDefinitions;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsAssemblyObject;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.RefseqGenomeEntity;
import pt.uminho.sysbio.biosynthframework.io.biodb.ncbi.EutilsNcbiAssemblyDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.neo4j.Neo4jNcbiRefseqGenomeDaoImpl;
import pt.uminho.sysbio.biosynthframework.neo4j.BiosGenomeNode;
import pt.uminho.sysbio.biosynthframework.neo4j.GenomeDatabase;

public class EtlGenome extends AbstractNeo4jEtl {

  private static final Logger logger = LoggerFactory.getLogger(EtlGenome.class);
  
  public EtlGenome(GraphDatabaseService service) {
    super(service);
  }
  
  public void integrateGenomesWithTaxonomy() {
//    Neo4jBiosAnnotationService service = new Neo4jBiosAnnotationService(service);
//    Neo4jKeggGenomeDao dao = new Neo4jKeggGenomeDao(service);
//    
//    for (long id : dao.getAllIds()) {
//      KeggGenomeEntity gn = dao.getById(id);
//      String tax = gn.getTaxonomy();
//      if (tax != null) {
//        if (tax.contains("TAX:")) {
//          tax = tax.substring(4);
//        }
//        tax = "txid" + tax;
//        
//        Node taxNode = graphDatabaseService.findNode(GlobalLabel.NcbiTaxonomy, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, tax);
//        Node gnNode = graphDatabaseService.getNodeById(id);
//        if (taxNode != null) {
//          String sn = (String) taxNode.getProperty("scientific_name", null);
//          System.out.println(sn);
//          System.out.println(gn.getTaxonomy());
//          System.out.println(gn.getDefinition());
//          service.setTaxonomy(gnNode, taxNode);
//        }
//      }
//    }
    
    for (BiosGenomeNode g : service.listGenomes(GenomeDatabase.RefSeqGenome)) {
      String tax = (String) g.getProperty("txid", null);
      Node taxNode = service.findNode(GlobalLabel.NcbiTaxonomy, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, tax);
      if (taxNode != null) {
//        String sn = (String) taxNode.getProperty("scientific_name", null);
//        System.out.println(sn);
//        System.out.println(g.getProperty("organism", null));
//        System.out.println(g.getProperty("ftpPath", null));
        g.setTaxonomy(taxNode);
      }
    }
    
//    dataTx.success();
//    dataTx.close();
  }
  
  public void integrateGenomeWithTaxonomy(BiosGenomeNode g) {
    String tax = (String) g.getProperty("txid", null);
    Node taxNode = service.findNode(GlobalLabel.NcbiTaxonomy, Neo4jDefinitions.ENTITY_NODE_UNIQUE_CONSTRAINT, tax);
    if (taxNode != null) {
      g.setTaxonomy(taxNode);
    }
  }

  @Override
  public void etl(String entry) {
    
    Neo4jNcbiRefseqGenomeDaoImpl refseqDao = new Neo4jNcbiRefseqGenomeDaoImpl(service);
    EutilsNcbiAssemblyDaoImpl dao = new EutilsNcbiAssemblyDaoImpl();
    EutilsAssemblyObject o = dao.getByEntry(entry);
    if (o != null) {
      RefseqGenomeEntity genome = Neo4jNcbiRefseqGenomeDaoImpl.convert(o);
      Long id = refseqDao.save(genome);
      integrateGenomeWithTaxonomy(service.getGenome(id));
    } else {
      logger.warn("failed to load {}", entry);
    }
  }
}
