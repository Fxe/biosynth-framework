package pt.uminho.sysbio.biosynthframework.core.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggGenomeEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggKOEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggModuleEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGenesDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggGenomeDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggKOsDaoImpl;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggModuleDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.biodb.BiocycClassNames;
import pt.uminho.sysbio.biosynthframework.io.biodb.BiocycXmlQueryResult;

@Deprecated
public class GeneralDriver {
  
  private static final Logger logger = LoggerFactory.getLogger(GeneralDriver.class);
  
  public static void keggDriver() {
    String dbPath = "/var/biodb/kegg2";
    
    RestKeggModuleDaoImpl moduleDao = new RestKeggModuleDaoImpl();
    moduleDao.setLocalStorage(dbPath);
    moduleDao.setSaveLocalStorage(true);
    moduleDao.setUseLocalStorage(true);
    
    RestKeggKOsDaoImpl koDao = new RestKeggKOsDaoImpl();
    koDao.setLocalStorage(dbPath);
    koDao.setSaveLocalStorage(true);
    koDao.setUseLocalStorage(true);
    
    RestKeggGenesDaoImpl geneDao = new RestKeggGenesDaoImpl();
    geneDao.setLocalStorage(dbPath);
    geneDao.setSaveLocalStorage(true);
    geneDao.setUseLocalStorage(true);
//    geneDao.replace = true;
    
    RestKeggGenomeDaoImpl genomeDao = new RestKeggGenomeDaoImpl();
    genomeDao.setLocalStorage(dbPath);
    genomeDao.setSaveLocalStorage(true);
    genomeDao.setUseLocalStorage(true);
    
    KeggModuleEntity module = moduleDao.getModuleByEntry("M00124");
    for (String koEntry : module.getOrthologs()) {
      KeggKOEntity ko = koDao.getKOByEntry(koEntry);
      for (Pair<String, String> gpair : ko.g) {
        System.out.println(gpair);
        try {
          KeggGenomeEntity g = genomeDao.getGenomeByEntry(gpair.getLeft());

          System.out.println(g.getTaxonomy());
          
          geneDao.getGeneByEntry(String.format("%s:%s", gpair.getKey(), gpair.getRight()));
          
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    
    
    //      System.out.println(k.getAllKOEntries());
    
  }
  
  public static String xmlget(String id) {
    String base = "https://websvc.biocyc.org/getxml?" + id;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    URLConnection connection = null;
    try {
      URL url = new URL(base);
      connection = url.openConnection();
      int bytes = IOUtils.copy(connection.getInputStream(), baos);
      logger.info("{} read {} bytes", base, bytes);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.close(connection);
      IOUtils.closeQuietly(baos);
    }
    
    return baos.toString();
  }
  
  public static Map<String, Object> deserializeXml(String xmlStr) throws IOException {
    ObjectMapper om = new XmlMapper();
    @SuppressWarnings("unchecked")
    Map<String, Object> queryResult = om.readValue(xmlStr, Map.class);
    return queryResult;
  }
  
  public static<T> T deserializeXml(String xmlStr, Class<T> clazz) throws IOException {
    ObjectMapper om = new XmlMapper();
    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    T queryResult = om.readValue(xmlStr, clazz);
    return queryResult;
  }
  
  public static String getCache(String base, String db, String bclass, String id) {
//    String path = String.format("%s/%s/%s/", base, db, bclass);
    id = id.replaceAll(":", "_");
    String fileName = String.format("%s.xml", id);
    File path = new File(String.format("%s/%s/%s/", base, db, bclass));
    File file = new File(path.getAbsolutePath() + "/" + fileName);
//    if (!path.exists()) {
//      RestBiocycMetaboliteDaoImpl.createFolderIfNotExists(path.getAbsolutePath());
//    }
    String resultstr = null;
    if (!file.exists()) {
      OutputStream os = null;
      try {
        os = new FileOutputStream(file);
        resultstr = xmlget(String.format("%s:%s", db, id));
        IOUtils.write(resultstr, os);
        System.out.println(file.getAbsolutePath());
      } catch (IOException e) {
        logger.error(e.getMessage());
      } finally {
        IOUtils.closeQuietly(os);
      }
    } else {
      InputStream is = null;
      OutputStream os = null;
      try {
        is = new FileInputStream(file);
        os = new ByteArrayOutputStream();
        IOUtils.copy(is, os);
        resultstr = os.toString();
      } catch (IOException e) {
        logger.error(e.getMessage());
      } finally {
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(os);
      }
    }
    
    return resultstr;
  }
  
  public static class BiocycProtein {
    public String ID;
    public String orgid;
    public String frameid;
    public String detail;
    
    public Object parent;
    
    
    private Object gene;
    
    public Object getGene() { return gene;}

    public void setGene(Object gene) {
      if (this.gene != null) {
        logger.warn("duplicate gene {} => {}", gene, this.gene);
      }
      this.gene = gene;
    }



    public List<String> synonyms = new ArrayList<> ();
    
    
    
    public List<String> getSynonyms() {
      return synonyms;
    }
    
    @JsonProperty("synonym")
    public void setSynonyms(Object data) {
      String str = getStringDataType(data);
      synonyms.add(str);
    }



    @JsonProperty("has-go-term")
    public Object has_go_term;
    
    private String comment;
    public String getComment() { return comment;}
    
    @JsonProperty("comment")
    public void setComment(Object data) {
      if (this.comment != null) {
        logger.warn("duplicate comment {}", data);
      }
      this.comment = getStringDataType(data);
    }



    public String commonName;

    public String getCommonName() {
      return commonName;
    }

    @JsonProperty("common-name")
    public void setCommonName(Object data) {
      if (this.commonName != null) {
        logger.warn("duplicate common-name {}", data);
      }
      commonName = getStringDataType(data);
    }
    


    public Object location;
    
    @JsonProperty("molecular-weight-seq")
    public Object molecular_weight_seq;
    
    @JsonProperty("molecular-weight-exp")
    public Object molecular_weight_exp;
    
    public Object credits;
    public Object citation;
    public Object pi;
    
    @JsonProperty("has-feature")
    public Object has_feature;
    
    public List<Map<String, String>> dblinks = new ArrayList<>();
    
    public List<Map<String, String>> getDblinks() { return dblinks;}

    @JsonProperty("dblink")
    public void setDblinks(Map<String, String> data) {
      dblinks.add(data);
    }
    
    public Map<String, String> getDbLinkByDb(String db) {
      Map<String, String> result = null;
      for (Map<String, String> dblink : dblinks) {
        if (db.equals(dblink.get("dblink-db"))) {
          result = dblink;
          break;
        }
      }
      
      return result;
    }



    public Object evidence;
    
    public List<Pair<Object, Double>> components = new ArrayList<>();
    public List<Pair<Object, Double>> getComponents() { return components;}
    
    @JsonProperty("component")
    public void setComponents(Map<String, Object> data) {
      Double coefficient = null;
      Object object = null;
      Object citation = null;
      String comment = null;
      if (data.containsKey("coefficient")) {
        coefficient = getNumberDataType(data.remove("coefficient"));
      }
      if (data.containsKey("comment")) {
        comment = getStringDataType(data.remove("comment"));
        logger.debug("dropped comment {}", comment);
      }
      
      if (data.containsKey("citation")) {
        citation = data.remove("citation");
        logger.debug("dropped citation {}", citation);
      }
      
      if (data.size() == 1) {
        object = data.remove(data.keySet().iterator().next());
      }
      
      if (object != null) {
        components.add(new ImmutablePair<Object, Double>(object, coefficient));
      } else {
        logger.warn("unable to extract object {}", data);
      }
//      this.components = components;
    }



    public Object catalyzes;
    public Object species;
    public Object symmetry;
    public Object cml;
    public Object inchi;
    
    @JsonProperty("inchi-key")
    public Object inchi_key;
    
    @JsonProperty("gibbs-0")
    public Object gibbs_0;
    
    @JsonProperty("monoisotopic-mw")
    public Object monoisotopic_mw;
    
    @JsonProperty("molecular-weight")
    public Object molecular_weight;
    
    @JsonProperty("abbrev-name")
    public Object abbrev_name;
    
    
    private List<Map<String, Map<String, Object>>> componentOf = new ArrayList<> ();

    public Object getComponentOf() { return componentOf;}
    
    @JsonProperty("component-of")
    public void setComponentOf(Map<String, Map<String, Object>> data) {
      componentOf.add(data);
//      System.out.println(data);
//      this.component_of = component_of;
    }



    @JsonProperty("dna-footprint-size")
    public Object dna_footprint_size;
    
    @JsonProperty("consensus-sequence")
    public Object consensus_sequence;
    
    @JsonProperty("recognized-promoters")
    public Object recognized_promoters;
    
    @JsonProperty("modified-form")
    public Object modified_form;
    
    @JsonProperty("unmodified-form")
    public Object unmodified_form;
    
    @JsonProperty("isozyme-sequence-similarity")
    public Object isozyme_sequence_similarity;
    
    @JsonProperty("appears-in-left-side-of")
    public Object appears_in_left_side_of;
    
    @JsonProperty("appears-in-right-side-of")
    public Object appears_in_right_side_of;
    
    public Object regulates;
    
    @JsonProperty("regulated-by")
    public Object regulated_by;
    
    @JsonProperty("class")
    public Object bclass;
    
    public Object subclass;
    
    public Object instance;
    
    @JsonProperty("intron-or-removed-segment")
    public Object intron_or_removed_segment;
    
    public String getStringDataType(Object o) {
      String value = null;

      if (o instanceof Map) {
        Map<?, ?> data = Map.class.cast(o);
        data.get("datatype");
        if (data.containsKey("")) {
          value = data.get("").toString();
        }
      } else {
        logger.warn("unknown type {}", o.getClass().getSimpleName());
      }
      
      return value;
    }
    
    public Double getNumberDataType(Object o) {
      Double value = null;

      if (o instanceof Map) {
        Map<?, ?> data = Map.class.cast(o);
        data.get("datatype");
        if (data.containsKey("")) {
          value = Double.parseDouble(data.get("").toString());
        }
      } else {
        logger.warn("unknown type {}", o.getClass().getSimpleName());
      }
      
      return value;
    }
  }
  
  public static class BiocycObject {
    @JsonProperty("ptools-version")
    public Object ptools_version;
    
//    @JsonProperty("base")
    public Object base;
    
//    @JsonProperty("metadata")
    public Map<String, Object> metadata;
    
    public BiocycProtein Protein;
  }
  
  public static void main(String[] args) {
    String pgdb = "ECOLI";
    String base = "/var/biodb/biocyc2";
    
//    String bclassStr = "Gene";
    String bclassStr = "Protein";
//    RestBiocycMetaboliteDaoImpl cpdDao = new RestBiocycMetaboliteDaoImpl();
////    cpdDao.getAllMetaboliteEntries();
////    String xmlResponse = "";
//    
////    InputStream is = new ByteArrayInputStream(xmlResponse.getBytes());
    ObjectMapper om = new XmlMapper();
    InputStream is = null;
    BiocycClassNames bclass = BiocycClassNames.Proteins;
    
    String path = String.format("/var/biodb/biocyc2/ECOLI/query/%s.xml", bclass);
    System.out.println(path);
    
    try {
      is = new FileInputStream(path);
//      JsonNode node = om.readTree(is);
//      ObjectMapper jsonMapper = new ObjectMapper();
//      String json = jsonMapper.writeValueAsString(node);
//      System.out.println(json);
      XmlMapper xmlMapper = new XmlMapper();
//      JsonNode node = xmlMapper.readTree(xml.getBytes());
//
//      ObjectMapper jsonMapper = new ObjectMapper();
//      String json = jsonMapper.writeValueAsString(node);
      
      BiocycXmlQueryResult queryResult = om.readValue(is, BiocycXmlQueryResult.class);
      System.out.println(queryResult.base);
      System.out.println(queryResult.ptools_version);
      System.out.println(queryResult.metadata.keySet());
      System.out.println(queryResult.metadata.get("num_results"));
      System.out.println(queryResult.getProtein().size());
      System.out.println(queryResult.Error.size());
      
      Map<String, Boolean> isEnzymaticMap = new HashMap<> ();
      Map<String, BiocycObject> aa = new HashMap<> ();
      for (Map<String, Object> o : queryResult.getProtein()) {
        String frameid = o.get("frameid").toString();
        String orgid = o.get("orgid").toString();
        String entry = String.format("%s:%s", orgid, frameid);
        if (orgid.equals(pgdb)) {
          String a = getCache(base, pgdb, bclassStr.toLowerCase(), frameid);
          if (a != null && !a.trim().isEmpty()) {
            BiocycObject data = deserializeXml(a, BiocycObject.class);
            aa.put(entry, data);
            if (data != null && data.Protein != null) {
              isEnzymaticMap.put(entry, false);
              if (data.Protein.catalyzes != null) {
                isEnzymaticMap.put(entry, true);
              }
            }
          }
        } else {
          logger.warn("ignored [{}] pgdb [{}] mismatch orgid [{}]", o.get("ID"), pgdb, orgid);
        }
        break;
      }
      logger.info("{}", aa.size());
      
      for (String entry : aa.keySet()) {
        BiocycObject data = aa.get(entry);
        //        String frameid = o.get("frameid").toString();
        //        String orgid = o.get("orgid").toString();
        String comment = "";
        String locus = "";
        String ncbi = "";
        String annotation = "";
        Boolean isEnzymatic = isEnzymaticMap.get(entry);
        Boolean isComplexComponent = false;
        //        System.out.println(o.keySet());
        //        System.out.println(orgid + ":" + frameid);
        //        if (orgid.equals(pgdb)) {
        //          String a = getCache(base, pgdb, bclassStr.toLowerCase(), frameid);
        //          System.out.println(a.length());
        //          if (a != null && !a.trim().isEmpty()) {
        //            BiocycObject data = deserializeXml(a, BiocycObject.class);
        //            data = JsonMapUtils.getMap(bclassStr, data);
        if (data != null && data.Protein != null) {
          //              System.out.println(data.keySet());
          //              System.out.println(data.Protein.getCommonName());
          //              System.out.println(data.Protein.getGene());
          //              System.out.println(data.Protein.getDblinks());
          annotation = data.Protein.getCommonName();
          comment = data.Protein.getComment();

          if (comment != null) {
            comment = comment.replaceAll("\n", "");
            comment = comment.replaceAll("\t", "");
          }
          Map<String, ?> bnumber = data.Protein.getDbLinkByDb("ECOLIWIKI");
          if (bnumber != null && bnumber.containsKey("dblink-oid")) {
            locus = bnumber.get("dblink-oid").toString();
          }

          Map<String, ?> refseq = data.Protein.getDbLinkByDb("REFSEQ");
          if (refseq != null && refseq.containsKey("dblink-oid")) {
            ncbi = refseq.get("dblink-oid").toString();
          }

          for (Map<String, Map<String, Object>> comp : data.Protein.componentOf) {
            if (comp.containsKey("Protein")) {
              Map<String, Object> resource = comp.remove("Protein");
              String rframeid = resource.get("frameid").toString();
              logger.debug("check if {} is enzymatic mark this as complex", rframeid);
              isComplexComponent = true;
              isEnzymatic = isEnzymaticMap.get(pgdb + ":" + rframeid);
            }
            if (!comp.isEmpty()) {
              logger.warn("unknown componentOf keys {}", comp.keySet());
            }
          }
          //              System.out.println(data.Protein.catalyzes);
          //              System.out.println(data.Protein.getDbLinkByDb("ECOLIWIKI"));
          //              System.out.println(data.Protein.getDbLinkByDb("REFSEQ"));
        }



        List<Object> output = new ArrayList<> ();
        output.add(entry);
        //          output.add(frameid);
        //          output.add(comment);
        output.add(annotation);
        output.add(locus);
        output.add(ncbi);
        output.add(isEnzymatic);
        output.add(isComplexComponent);
        System.out.println(StringUtils.join(output, '\t'));

      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}


