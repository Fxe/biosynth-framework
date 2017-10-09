package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggCompoundFlatFileParser;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.util.IOUtils;

@Repository
public class RestKeggCompoundMetaboliteDaoImpl 
extends AbstractRestfulKeggDao implements MetaboliteDao<KeggCompoundMetaboliteEntity> {

  private static final Logger logger = LoggerFactory.getLogger(RestKeggCompoundMetaboliteDaoImpl.class);

  private static final String restCpdQuery = "http://rest.kegg.jp/get/cpd:%s";
  private static final String restCpdMolQuery = "http://rest.kegg.jp/get/cpd:%s/mol";

  @Override
  public KeggCompoundMetaboliteEntity getMetaboliteById(Serializable id) {
    return this.getMetaboliteByEntry(id.toString());
  }

  public String getMetaboliteFlatFile(Serializable id) {
    String restCpdQuery = String.format(RestKeggCompoundMetaboliteDaoImpl.restCpdQuery, id);
    String localPath = this.getLocalStorage()  + "cpd" + "/" + id;

    String flatFile = null;
    try {
      flatFile = getLocalOrWeb(restCpdQuery, localPath + ".txt");
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }

    return flatFile;
  }

  @Override
  public KeggCompoundMetaboliteEntity saveMetabolite(
      KeggCompoundMetaboliteEntity metabolite) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    List<Serializable> cpdIds = new ArrayList<>();
    String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "cpd");
    String localPath = getPath("query", "compound.txt");
    try {
      String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        String[] values = httpResponseLine[i].split("\\t");
        cpdIds.add(values[0].substring(4));
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return cpdIds;
  }

  @Override
  public Serializable save(KeggCompoundMetaboliteEntity entity) {
    throw new RuntimeException("Unsupported Operation");
  }
  
  public static KeggCompoundMetaboliteEntity convert(String cpdFlatFile, String cpdMolFile) {
    KeggCompoundFlatFileParser parser = new KeggCompoundFlatFileParser(cpdFlatFile);
    KeggCompoundMetaboliteEntity cpd = new KeggCompoundMetaboliteEntity();

    cpd.setEntry(parser.getEntry());
    cpd.setName(parser.getName());
    cpd.setFormula(parser.getFormula());
    cpd.setMass(parser.getMass());
    cpd.setRemark(parser.getRemark());
    cpd.setComment(parser.getComment());
    if (cpdMolFile != null && !cpdMolFile.isEmpty() && !cpdMolFile.startsWith("null")) {
      cpd.setMol2d(cpdMolFile);
    }
    cpd.setCrossReferences(parser.getCrossReferences());
    cpd.setReactions(parser.getReactions());
    cpd.setEnzymes(parser.getEnzymes());
    cpd.setPathways(parser.getPathways());
    return cpd;
  }
  //}
  //	
  //	private static final String restDrQuery = "http://rest.kegg.jp/get/dr:%s";
  //	private static final String restDrMolQuery = "http://rest.kegg.jp/get/dr:%s/mol";

  @Override
  public Serializable saveMetabolite(Object entity) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public KeggCompoundMetaboliteEntity getMetaboliteByEntry(String entry) {
    String restCpdQuery = String.format(RestKeggCompoundMetaboliteDaoImpl.restCpdQuery, entry);
    String restCpdMolQuery = String.format(RestKeggCompoundMetaboliteDaoImpl.restCpdMolQuery, entry);
    String localPath = getPath("cpd", entry);

    String cpdFlatFile = null;
    String cpdMolFile = null;
    try {
      cpdFlatFile = getLocalOrWeb(restCpdQuery, localPath + ".txt");
      if (cpdFlatFile == null) return null;

      cpdMolFile = getLocalOrWeb(restCpdMolQuery, localPath + ".mol");
      if (cpdMolFile == null) {
        IOUtils.writeToFile("null", localPath + ".mol");
      }
    } catch (IOException e) {
      logger.error("{}", e.getMessage());
    }

    KeggCompoundMetaboliteEntity cpd = convert(cpdFlatFile, cpdMolFile);
    return cpd;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    List<String> cpdIds = new ArrayList<>();
    String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "cpd");
    
    String localPath = getPath("query", "compound.txt");
    logger.debug("LocalPath: " + localPath);
    try {
      String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        String[] values = httpResponseLine[i].split("\\t");
        cpdIds.add(values[0].substring(4));
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return cpdIds;
  }


}
