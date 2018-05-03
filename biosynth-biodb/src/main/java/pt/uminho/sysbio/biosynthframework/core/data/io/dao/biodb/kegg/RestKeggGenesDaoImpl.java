package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGeneEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser.KeggGenericEntityFlatFileParser;
import pt.uminho.sysbio.biosynthframework.util.JsonMapUtils;


public class RestKeggGenesDaoImpl
extends AbstractRestfulKeggDao<KeggGeneEntity>  {

  public static boolean DELAY_ON_IO_ERROR = false;

  private static final Logger LOGGER = LoggerFactory.getLogger(RestKeggGenesDaoImpl.class);
  private static final String restGeneQuery = "http://rest.kegg.jp/get/%s";
  //	public boolean replace = false;

  public KeggGeneEntity getGeneByEntry(String entry) {
    String restGeneQuery = String.format(RestKeggGenesDaoImpl.restGeneQuery, entry);
    
    KeggGeneEntity geneEntity = null;

    String geneEntry = entry;
    String org = "default";
    if (entry.contains(":")) {
      geneEntry = entry.split(":")[1];
      org = entry.split(":")[0];
    }
    //		if (replace) {
    //		  localPath = localPath.replace(':', '_');
    //		}
    String localPath = String.format("%s/%s", getPathFolder(), org);
    try {
      LOGGER.debug(restGeneQuery);
      LOGGER.info(localPath);
//      String pathname = localPath;
      if (saveLocalStorage) {
        File f = new File(localPath);
//        System.out.println(f.getAbsolutePath());
        if (!f.exists() || !f.isDirectory()) {
          f.mkdirs();
        }
      }

      String rnFlatFile = this.getLocalOrWeb(restGeneQuery, localPath + "/" + geneEntry + ".txt");
      geneEntity = KeggGenericEntityFlatFileParser.parse(KeggGeneEntity.class, rnFlatFile);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }

    //		System.out.println(geneEntity.getDefinition());
    return geneEntity;
  }


  public Set<String> getAllGenesEntries(String genome) {
    Set<String> rnIds = new HashSet<>();
    String restListRnQuery = String.format("http://rest.kegg.jp/%s/%s", "list", genome);
    String localPath = this.getLocalStorage() + "query" + String.format("/genes_%s.txt", genome);
    try {
      String httpResponseString = getLocalOrWeb(restListRnQuery, localPath);
      String[] httpResponseLine = httpResponseString.split("\n");
      for ( int i = 0; i < httpResponseLine.length; i++) {
        String[] values = httpResponseLine[i].split("\\t");
        rnIds.add(values[0]);
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    return rnIds;
  }

  public String getPathFolder(){
    return this.getLocalStorage()  + "gene" + "/";
  }

  public void createFolder(){
    File f = new File(getPathFolder());
    f.mkdirs();
  }

  public static void main(String[] args) {
    RestKeggGenesDaoImpl gene = new RestKeggGenesDaoImpl();
    gene.setLocalStorage("/var/biodb/kegg2");
    gene.setSaveLocalStorage(true);
    gene.setUseLocalStorage(true);
    //		System.out.println(gene.getAllGenesEntries("T02080"));
    String k = "sce:YOR374W";
    k = "sce:YAL038W";
    k = "rsp:RSP_2904";
    k = "rsp:RSP_3178";
    KeggGeneEntity kgene = gene.getGeneByEntry(k);
    Map<String, Object> gproperties = new HashMap<> ();
    gproperties.put("entry", k);
    if (kgene.getDefinition() != null) {
      gproperties.put("definition", kgene.getDefinition().trim());
    }
    gproperties.put("name", StringUtils.join(kgene.getNames(), ';'));
    gproperties.put("module", StringUtils.join(kgene.getModules(), ';'));
    gproperties.put("pathway", StringUtils.join(kgene.getPathways(), ';'));
    gproperties.put("orthology", StringUtils.join(kgene.getOrthologs(), ';'));
    gproperties.put("motif", StringUtils.join(kgene.motif, ';'));
    gproperties.put("dblinks", StringUtils.join(kgene.dblinks, ';'));
    gproperties.put("organism", StringUtils.join(kgene.organism, ';'));
    gproperties.put("position", StringUtils.join(kgene.position, ';'));
    gproperties.put("structure", StringUtils.join(kgene.structure, ';'));
    gproperties = JsonMapUtils.cleanNullsAndStrings(gproperties);
    System.out.println(kgene.getProperties());
    
    System.out.println(Joiner.on('\n').withKeyValueSeparator("\t").join(gproperties));
    
//    String base = "/var/biodb/kegg2/gene";
//    File f = new File(base);
//    if (f.isDirectory()) {
//      for (File gf : f.listFiles()) {
//        if (gf.isFile()) {
//          String org = gf.getName().split("_")[0];
//          String rest = gf.getName().replace(org + "_", "");
//          File d = new File(base + "/" + org);
//          System.out.println(org + " " + rest + " " + gf.getName());
//          if (!d.exists() || !d.isDirectory()) {
//            d.mkdirs();
//          }
//          
//          File dst = new File(base + "/" + org + "/" + rest);
//          try {
//            Files.move(gf.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);
//          } catch (IOException e) {
//            e.printStackTrace();
//          }
//        }
//      }
//    }

  }


  @Override
  public KeggGeneEntity getByEntry(String entry) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Set<String> getAllEntries() {
    // TODO Auto-generated method stub
    return null;
  }
}
