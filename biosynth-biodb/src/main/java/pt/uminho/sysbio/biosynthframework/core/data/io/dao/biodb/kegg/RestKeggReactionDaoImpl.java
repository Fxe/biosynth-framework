package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.Orientation;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggReactionEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggReactionFlatFileParser;
import pt.uminho.sysbio.biosynthframework.io.ReactionDao;

public class RestKeggReactionDaoImpl
extends AbstractRestfulKeggDao<KeggReactionEntity> implements ReactionDao<KeggReactionEntity> {

  public static boolean DELAY_ON_IO_ERROR = false;

  private static final Logger logger = LoggerFactory.getLogger(RestKeggReactionDaoImpl.class);
  private static final String restRxnQuery = "http://rest.kegg.jp/get/rn:%s";

  @Override
  public KeggReactionEntity getReactionByEntry(String entry) {
    String restRxnQuery = String.format(RestKeggReactionDaoImpl.restRxnQuery, entry);
    String localPath = getPath("rn", entry); //this.getLocalStorage()  + "rn" + "/" + entry ;
    KeggReactionEntity rxn = new KeggReactionEntity();

    String rnFlatFile = null;

    try {
      logger.info(restRxnQuery);
      logger.info(localPath);
      rnFlatFile = this.getLocalOrWeb(restRxnQuery, localPath + ".txt");

      KeggReactionFlatFileParser parser = new KeggReactionFlatFileParser(rnFlatFile);
      rxn.setEntry(parser.getEntry());
      rxn.setName(parser.getName());
      rxn.setOrientation(Orientation.Reversible);
      rxn.setComment(parser.getComment());
      rxn.setRemark(parser.getRemark());
      rxn.setDefinition(parser.getDefinition());
      rxn.setEquation(parser.getEquation());
      rxn.setEnzymes(parser.getEnzymes());
      rxn.setPathways(parser.getPathways());
      rxn.setRpairs(parser.getRPairs());
      rxn.setOrthologies(parser.getOrthologies());
      rxn.setLeft(parser.getLeft());
      rxn.setRight(parser.getRight());
      rxn.setVersion(databaseVersion);
    } catch (IOException e) {
      logger.error(e.getMessage());

      if (DELAY_ON_IO_ERROR) {
        try {
          Thread.sleep(300000);
        } catch (Exception es) {
          System.out.println(es.getMessage());
        }
      }
      //			LOGGER.debug(e.getStackTrace());
      return null;
    }
    return rxn;
  }

  @Override
  public Set<String> getAllReactionEntries() {
    Set<String> rnIds = new HashSet<>();
    String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "rn");
    //		String localPath = this.getLocalStorage() + "query" + "/reaction.txt";
    String localPath = getPath("query", "reaction.txt");
    try {
      String httpResponseString = getLocalOrWeb(restListRnQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        String[] values = httpResponseLine[i].split("\\t");
        rnIds.add(values[0].substring(3));
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return rnIds;
  }
  
  @Override
  public KeggReactionEntity getReactionById(Long id) {
    throw new RuntimeException("Unsupported Operation.");
  }
  
  @Override
  public KeggReactionEntity saveReaction(KeggReactionEntity reaction) {
    throw new RuntimeException("Unsupported Operation.");
  }

  @Override
  public Set<Long> getAllReactionIds() {
    throw new RuntimeException("Unsupported Operation.");
  }

  @Override
  public KeggReactionEntity getByEntry(String entry) {
    return this.getReactionByEntry(entry);
  }

  @Override
  public Set<String> getAllEntries() {
    // TODO Auto-generated method stub
    return null;
  }
}
