package pt.uminho.sysbio.biosynthframework.io.biodb.kegg;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGenomeEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.AbstractRestfulKeggDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggGenomeFlatFileParser;

/**
 * 
 * @author Someone at Silico Life
 * @author Filipe Liu
 *
 */
public class RestKeggGenomeDaoImpl
extends AbstractRestfulKeggDao<KeggGenomeEntity> {

  private static final Logger logger = LoggerFactory.getLogger(RestKeggGenomeDaoImpl.class);
  public static boolean DELAY_ON_IO_ERROR = false;
  private static final String restRxnQuery = "http://rest.kegg.jp/get/gn:%s";



  @Override
  public KeggGenomeEntity getByEntry(String entry) {
    String restRxnQuery = String.format(RestKeggGenomeDaoImpl.restRxnQuery, entry);
    String localPath = getPath("gn", entry);
    KeggGenomeEntity genome = new KeggGenomeEntity();
    String rnFlatFile = null; 
    try {
      logger.debug(restRxnQuery);
      logger.debug(localPath);
      rnFlatFile = this.getLocalOrWeb(restRxnQuery, localPath +".txt");

      logger.debug("{}", rnFlatFile.getBytes().length);

      KeggGenomeFlatFileParser parser = new KeggGenomeFlatFileParser(rnFlatFile);
      genome.setEntry(parser.getEntry());
      genome.setName(parser.getName());
      genome.setDefinition(parser.getDefinition());
      genome.setTaxonomy(parser.getTaxonomy());
      genome.setAnnotation(parser.getAnnotation());
      genome.setDataSource(parser.getDataSource());
      String lineage = genome.getTaxonomy();
      if (lineage != null) {
        lineage = lineage.split("\n")[1].trim().substring("LINEAGE   ".length());
      }
      genome.setLineage(lineage);
    } catch (IOException e) {
      logger.warn("error: {}", e.getMessage());
    }
    
    return genome;
  }
  


  @Override
  public Set<String> getAllEntries() {
    Set<String> ids = new HashSet<>();
    String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "genome");
    String localPath = getPath("query", "genome.txt");
    try {
      String httpResponseString = getLocalOrWeb(restListRnQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        String[] values = httpResponseLine[i].split("\\t");
        ids.add(values[0].substring(3));
      }
    } catch (IOException e) {
      logger.warn("error: {}", e.getMessage());
    }
    return ids;
  }
}
