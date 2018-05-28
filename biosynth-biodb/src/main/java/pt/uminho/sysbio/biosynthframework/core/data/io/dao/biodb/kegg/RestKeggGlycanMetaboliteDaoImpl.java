package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGlycanMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggGlycanMetaboliteFlatFileParser;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;
import pt.uminho.sysbio.biosynthframework.util.IOUtils;


public class RestKeggGlycanMetaboliteDaoImpl 
extends AbstractRestfulKeggDao<KeggGlycanMetaboliteEntity> implements MetaboliteDao<KeggGlycanMetaboliteEntity> {

  private static final String restGlQuery = "http://rest.kegg.jp/get/gl:%s";
  private static final String restGlMolQuery = "http://rest.kegg.jp/get/gl:%s/mol";

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    List<Serializable> cpdIds = new ArrayList<>();
    String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "gl");
    String localPath = this.getLocalStorage() + "query" + "/glycan.txt";
    try {
      String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        //				dr:D10517\tCrisantaspase (JAN)
        String[] values = httpResponseLine[i].split("\\t");
        //				remove dr:
        cpdIds.add(values[0].substring(3));
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return cpdIds;
  }

  public static KeggGlycanMetaboliteEntity convert(String glFlatFile, String glMolFile) {
    KeggGlycanMetaboliteFlatFileParser parser = new KeggGlycanMetaboliteFlatFileParser(glFlatFile);
    //    System.out.println(parser.getTabs());
    KeggGlycanMetaboliteEntity cpd = new KeggGlycanMetaboliteEntity();

    cpd.setEntry(parser.getEntry());
    cpd.setName(parser.getName());
    cpd.setComposition(parser.getComposition());
    cpd.setFormula(parser.getFormula());
    cpd.setMass(parser.getMass());
    cpd.setCompoundClass(parser.getMetaboliteClass());
    cpd.setRemark(parser.getRemark());
    cpd.setComment(parser.getComment());
    if (glMolFile != null && !glMolFile.isEmpty() && !glMolFile.startsWith("null")) {
      cpd.setMol2d(glMolFile);
    }
    cpd.setCrossReferences(parser.getCrossReferences());
    cpd.setReactions(parser.getReactions());
    cpd.setEnzymes(parser.getEnzymes());
    cpd.setPathways(parser.getPathways());
    return cpd;
  }

  @Override
  public KeggGlycanMetaboliteEntity getMetaboliteById(Serializable id) {
    return this.getMetaboliteByEntry(id.toString());
  }

  public String getMetaboliteFlatFile(Serializable id) {
    String restDrQuery = String.format(RestKeggGlycanMetaboliteDaoImpl.restGlQuery, id);
    String localPath = this.getLocalStorage() + "gl" + "/" + id;

    String drFlatFile = null;
    try {
      drFlatFile = getLocalOrWeb(restDrQuery, localPath + ".txt");
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }

    return drFlatFile;
  }


  @Override
  public KeggGlycanMetaboliteEntity getMetaboliteByEntry(String entry) {
    String restGlQuery = String.format(RestKeggGlycanMetaboliteDaoImpl.restGlQuery, entry);
    String restGlMolQuery = String.format(RestKeggGlycanMetaboliteDaoImpl.restGlMolQuery, entry);

    String localPath = getPath("gl", entry); //this.getLocalStorage() + "gl" + "/" + id;

    String glFlatFile = null;
    String glMolFile = null;
    try {
      glFlatFile = getLocalOrWeb(restGlQuery, localPath + ".txt");
      if (glFlatFile == null) return null;

      glMolFile = getLocalOrWeb(restGlMolQuery, localPath + ".mol");
      if (glMolFile == null) {
        IOUtils.writeToFile("null", localPath + ".mol");
      }
      //	          System.out.println(drFlatFile);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }


    KeggGlycanMetaboliteEntity cpd = convert(glFlatFile, glMolFile);
    cpd.setVersion(databaseVersion);
    return cpd;
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    return new ArrayList<> (getAllEntries());
  }

  @Override
  public KeggGlycanMetaboliteEntity saveMetabolite(
      KeggGlycanMetaboliteEntity metabolite) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public Serializable saveMetabolite(Object entity) {
    throw new RuntimeException("Unsupported Operation");
  }

  @Override
  public KeggGlycanMetaboliteEntity getByEntry(String entry) {
    return this.getMetaboliteByEntry(entry);
  }

  @Override
  public Set<String> getAllEntries() {
    Set<String> cpdIds = new HashSet<>();
    String restListDrQuery = String.format("http://rest.kegg.jp/%s/%s", "list", "gl");
    String localPath = getPath("query", "glycan.txt"); //this.getLocalStorage() + "query" + "/glycan.txt";
    try {
      String httpResponseString = getLocalOrWeb(restListDrQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        //              dr:D10517\tCrisantaspase (JAN)
        String[] values = httpResponseLine[i].split("\\t");
        //              remove dr:
        cpdIds.add(values[0].substring(3));
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return cpdIds;
  }
}