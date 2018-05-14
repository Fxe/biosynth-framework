package pt.uminho.sysbio.biosynthframework.io.biodb.kegg;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Tuple2;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggFlatFileParser;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggModuleEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.AbstractRestfulKeggDao;

/**
 * 
 * @author Someone at Silico Life
 * @author Filipe Liu
 *
 */
public class RestKeggModuleDaoImpl extends AbstractRestfulKeggDao<KeggModuleEntity> {
  private static final Logger logger = LoggerFactory.getLogger(RestKeggModuleDaoImpl.class);
  private static final String query = "http://rest.kegg.jp/get/md:%s";
  
  @Override
  public KeggModuleEntity getByEntry(String entry) {
    String query = String.format(RestKeggModuleDaoImpl.query, entry);
    String localPath = getPath("md", entry);
    KeggModuleEntity entity = null;
    String response = null;
    
    try {
      logger.debug(query);
      logger.debug(localPath);
      response = this.getLocalOrWeb(query, localPath + ".txt");
      logger.debug("{}", response.getBytes().length);

      KeggFlatFileParser parser = new KeggFlatFileParser();
      Map<String, Object> odata = parser.parse(response);
      String name = getString(odata, "NAME");
      String definition = getString(odata, "DEFINITION");
      String mclass = getString(odata, "CLASS");
      Map<String, Set<String>> orthology = 
          KeggFlatFileParser.parseOrthology(getString(odata, "ORTHOLOGY"));
      Map<Tuple2<String>, Set<String>> orthologyReaction = 
          KeggFlatFileParser.parseOrthologyReaction(getString(odata, "REACTION"));
      Set<String> compounds = 
          KeggFlatFileParser.getIdentifers(getString(odata, "COMPOUND"), "C");
      Set<String> pathways = 
          KeggFlatFileParser.getIdentifers(getString(odata, "PATHWAY"), "map");
//      Map<String, Set<String>> dblinks = 
//          KeggFlatFileParser.parseDblinks(getString(odata, "DBLINKS"));
      entity = new KeggModuleEntity();
      entity.setEntry(entry);
      entity.setName(name);
      entity.setDefinition(definition);
      entity.setModuleClass(mclass);
      entity.setCompounds(compounds);
      entity.setPathways(pathways);
      entity.setOrthology(orthology);
      entity.setOrthologyReaction(orthologyReaction);
    } catch (IOException e) {
      logger.warn("error: {}", e.getMessage());
    }
    
    return entity;
  }

  @Override
  public Set<String> getAllEntries() {
    Set<String> ids = new HashSet<>();
    String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "md");
    String localPath = getPath("query", "md.txt");
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
