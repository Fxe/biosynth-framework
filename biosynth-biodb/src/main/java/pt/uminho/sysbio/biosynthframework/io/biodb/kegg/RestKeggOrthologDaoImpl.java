package pt.uminho.sysbio.biosynthframework.io.biodb.kegg;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggFlatFileParser;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggOrthologyEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.AbstractRestfulKeggDao;

public class RestKeggOrthologDaoImpl extends AbstractRestfulKeggDao<KeggOrthologyEntity> {

  private static final Logger logger = LoggerFactory.getLogger(RestKeggOrthologDaoImpl.class);
  private static final String restRxnQuery = "http://rest.kegg.jp/get/ko:%s";

  @Override
  public KeggOrthologyEntity getByEntry(String entry) {
    String query = String.format(restRxnQuery, entry);
    String localPath = getPath("ko", entry);
    KeggOrthologyEntity ko = null;

    try {
      logger.debug(restRxnQuery);
      logger.debug(localPath);
      String data = this.getLocalOrWeb(query, localPath + ".txt");
      KeggFlatFileParser parser = new KeggFlatFileParser();
      Map<String, Object> odata = parser.parse(data);
      String name = getString(odata, "NAME");
      String definition = getString(odata, "DEFINITION");
      Set<String> pathways = 
          KeggFlatFileParser.getIdentifers(getString(odata, "PATHWAY"), "ko");
      Set<String> modules = 
          KeggFlatFileParser.getIdentifers(getString(odata, "MODULE"), "M");
      Map<String, Set<String>> dblinks = 
          KeggFlatFileParser.parseDblinks(getString(odata, "DBLINKS"));
//      String genes = getString(odata, "GENES");
      logger.trace("name: {}, definition: {}", name, definition);
      ko = new KeggOrthologyEntity();
      ko.setEntry(entry);
      ko.setName(name);
      ko.setDefinition(definition);
      ko.setModules(modules);
      ko.setPathways(pathways);
      ko.setReactions(dblinks.get("RN"));
      ko.setGo(dblinks.get("GO"));
      ko.setCog(dblinks.get("COG"));
      ko.setVersion(databaseVersion);
    } catch (Exception e) {
      e.printStackTrace();
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
