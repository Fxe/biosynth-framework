package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.seed.ModelSeedMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.io.MetaboliteDao;

public class GithubModelSeedMetaboliteDaoImpl implements MetaboliteDao<ModelSeedMetaboliteEntity> {

  private Resource compounds;
  private Resource aliases;
  
  private String repository = "ModelSEED/ModelSEEDDatabase";
  
  public Map<String, ModelSeedMetaboliteEntity> data = null;
  public void pullData() throws IOException {
    compounds = new InputStreamResource(
        new URL(
            "https://raw.githubusercontent.com/" + repository + "/master/Biochemistry/compounds.json").openStream());
    aliases = new InputStreamResource(
        new URL(
            "https://raw.githubusercontent.com/" + repository + "/master/Biochemistry/Aliases/Compounds_Aliases.tsv").openStream());
    data = parse(compounds.getInputStream(), aliases.getInputStream());
  }
  
  public static Map<String, ModelSeedMetaboliteEntity> parse(InputStream is, InputStream isAliases) throws IOException {
    Map<String, List<ModelSeedMetaboliteCrossreferenceEntity>> xrefsMap = new HashMap<> ();
    try {
      List<String> lines = IOUtils.readLines(isAliases, Charset.defaultCharset());
      for (int i = 1; i < lines.size(); i++) {
//        MS ID [Old MS ID] External ID Source
        String[] k = lines.get(i).concat("\t!").split("\t");
        String seed = k[0].trim();
        String entry = k[2].trim();
        String db = k[3].trim();
        if ("MetaCyc".equals(db) && !entry.startsWith("META:")) {
          entry = "META:".concat(entry);
        }
        if ("PlantCyc".equals(db) && !entry.startsWith("PLANT:")) {
          entry = "PLANT:".concat(entry);
        }
//        if ("BiGG".equals(db)) {
//          db = MetaboliteMajorLabel.BiGGMetabolite.toString();
//        }
//        if ("BiGG1".equals(db)) {
//          db = MetaboliteMajorLabel.BiGG.toString();
//        }

        ModelSeedMetaboliteCrossreferenceEntity x = 
            new ModelSeedMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, db, entry);
        if (!xrefsMap.containsKey(seed)) {
          xrefsMap.put(seed, new ArrayList<ModelSeedMetaboliteCrossreferenceEntity>());
        }
        xrefsMap.get(seed).add(x);
        

      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    
    Map<String, ModelSeedMetaboliteEntity> data = new HashMap<> ();
    ObjectMapper m = new ObjectMapper();
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    
    try {
      @SuppressWarnings("unchecked")
      Map<String, Map<String, Object>> compounds = m.readValue(is, Map.class);
      for (String id : compounds.keySet()) {
        Map<String, Object> compoundData = compounds.get(id);
        ModelSeedMetaboliteEntity cpd = new ModelSeedMetaboliteEntity();
        cpd.setEntry(id);
        cpd.setName(getString(compoundData, "name"));
        cpd.setFormula(getString(compoundData, "formula"));
        cpd.setAbbreviation(getString(compoundData, "abbreviation"));
        cpd.setDefaultCharge(getInteger(compoundData, "charge"));
        cpd.setObsolete(getBoolean(compoundData, "is_obsolete"));
        cpd.setCore(getBoolean(compoundData, "is_core"));
        cpd.setCofactor(getBoolean(compoundData, "is_cofactor"));
        cpd.setDeltaG(getDouble(compoundData, "deltag"));
        cpd.setDeltaGErr(getDouble(compoundData, "deltagerr"));
        cpd.setStructure(getString(compoundData, "structure"));
        List<ModelSeedMetaboliteCrossreferenceEntity> xrefs = xrefsMap.get(id);
        if (xrefs != null) {
          cpd.setCrossreferences(xrefs);
        }
        
        data.put(id, cpd);
      }
    } catch (IOException e) {
      e.printStackTrace();
//      logger.error("IO Error: {}", e.getMessage());
    }
    
    is.close();
    isAliases.close();
    
    return data;
  }
  
  public static String getString(Map<String, Object> map, String k) {
    if (map.containsKey(k) && map.get(k) != null && !"null".equals(map.get(k))) {
      return map.get(k).toString();
    }
    return null;
  }
  
  public static Integer getInteger(Map<String, Object> map, String k) {
    if (map.containsKey(k) && map.get(k) != null && !"null".equals(map.get(k))) {
      return Integer.parseInt(map.get(k).toString());
    }
    return null;
  }
  
  public static Double getDouble(Map<String, Object> map, String k) {
    if (map.containsKey(k) && map.get(k) != null && !"null".equals(map.get(k))) {
      return Double.parseDouble(map.get(k).toString());
    }
    return null;
  }
  
  public static Boolean getBoolean(Map<String, Object> map, String k) {
    if (map.containsKey(k) && map.get(k) != null && !"null".equals(map.get(k))) {
      return Boolean.parseBoolean(map.get(k).toString());
    }
    return null;
  }
  
  public GithubModelSeedMetaboliteDaoImpl() {

  }
  
  @Override
  public ModelSeedMetaboliteEntity getMetaboliteById(Serializable id) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public ModelSeedMetaboliteEntity getMetaboliteByEntry(String entry) {
    if (data == null) {
      data = new HashMap<>();
      try {
        pullData();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return data.get(entry);
  }

  @Override
  public ModelSeedMetaboliteEntity saveMetabolite(ModelSeedMetaboliteEntity metabolite) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public Serializable saveMetabolite(Object metabolite) {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public List<Serializable> getAllMetaboliteIds() {
    throw new RuntimeException("Operation not supported");
  }

  @Override
  public List<String> getAllMetaboliteEntries() {
    if (data == null) {
      data = new HashMap<>();
      try {
        pullData();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return new ArrayList<> (data.keySet());
  }

  @Override
  public Serializable save(ModelSeedMetaboliteEntity entity) {
    throw new RuntimeException("Operation not supported");
  }

}
