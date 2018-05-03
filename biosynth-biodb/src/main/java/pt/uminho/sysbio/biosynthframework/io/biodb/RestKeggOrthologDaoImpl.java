package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggKOEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.AbstractRestfulKeggDao;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggGenericEntityFlatFileParser;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggOrthologyFlatFileParser;

public class RestKeggOrthologDaoImpl extends AbstractRestfulKeggDao<KeggKOEntity> {
  
  private static final Logger logger = LoggerFactory.getLogger(RestKeggOrthologDaoImpl.class);
  private static final String restRxnQuery = "http://rest.kegg.jp/get/ko:%s";

  @Override
  public KeggKOEntity getByEntry(String entry) {
    String query = String.format(restRxnQuery, entry);
    String localPath = getPath("ko", entry);
    KeggKOEntity ko = null;
    
    try {
      logger.debug(restRxnQuery);
      logger.debug(localPath);
        String koFlatFile = this.getLocalOrWeb(query, localPath + ".txt");
        KeggOrthologyFlatFileParser parser = new KeggOrthologyFlatFileParser(koFlatFile);
        
        ko = KeggGenericEntityFlatFileParser.parse(KeggKOEntity.class, koFlatFile);
        ko.g.addAll(parser.getGenes());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    
    return ko;
  }


  @Override
  public Set<String> getAllEntries() {
    Set<String> ids = new HashSet<>();
    String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "ko");
    String localPath = getPath("query", "ortholog.txt");
    try {
      String httpResponseString = getLocalOrWeb(restListRnQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        String[] values = httpResponseLine[i].split("\\t");
        ids.add(values[0].substring("ko:".length()));
      }
    } catch (IOException e) {
      logger.warn("error: {}", e.getMessage());
    }
    
    return ids;
  }
}
