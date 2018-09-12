package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Function;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReactionCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReactionEntity;
import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedReactionReagentEntity;
import pt.uminho.sysbio.biosynthframework.io.AbstractReadOnlyReactionDao;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;

/**
 * 
 * @author Filipe Liu
 *
 */
public class GithubModelSeedReactionDaoImpl extends AbstractReadOnlyReactionDao<ModelSeedReactionEntity> implements BiosDao<ModelSeedReactionEntity> {
  
  private static final Logger logger = LoggerFactory.getLogger(GithubModelSeedReactionDaoImpl.class);
  
  private String repository = "ModelSEED/ModelSEEDDatabase";
  public String path = null;
  
  public Map<String, ModelSeedReactionEntity> data = null;
  
  public GithubModelSeedReactionDaoImpl(String version) {
    super(version);
  }
//  public Function<String, ModelSeedReactionReagentEntity> stoichParser = new ;
  
  public static Map<String, List<ModelSeedReactionCrossreferenceEntity>> parseAliases(InputStream is) {
    Map<String, List<ModelSeedReactionCrossreferenceEntity>> xrefsMap = new HashMap<> ();
    try {
      List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
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
        ModelSeedReactionCrossreferenceEntity x = 
            new ModelSeedReactionCrossreferenceEntity(ReferenceType.DATABASE, db, entry);
        if (!xrefsMap.containsKey(seed)) {
          xrefsMap.put(seed, new ArrayList<ModelSeedReactionCrossreferenceEntity>());
        }
        xrefsMap.get(seed).add(x);
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    
    return xrefsMap;
  }
  
  public static class ReactionJson {
    public String id;
    public String name;
    public String notes;
    public Object pathways;
    public String abbreviation;
    public Object abstract_reaction;
    public Object aliases;
    public String code;
    
    public Integer is_obsolete;
    public Object is_transport;
    
    public String compound_ids;
    public String linked_reaction;
    public String definition;
    public Object deltag;
    public Object deltagerr;
    public String direction;
    public String reversibility;
    public String equation;
    
    public String status;
    public String stoichiometry;
    
    public Object ec_numbers;
  }
  
  public static Function<String, ModelSeedReactionReagentEntity> stoichParser = 
      new Function<String, ModelSeedReactionReagentEntity>() {

    @Override
    public ModelSeedReactionReagentEntity apply(String t) {
      ModelSeedReactionReagentEntity reagent = 
          new ModelSeedReactionReagentEntity();
      String[] data = t.split(":");
      //STOICH:CPDXXXXX:CMP:??:NAME
      reagent.setStoichiometry(Math.abs(Double.parseDouble(data[0])));
      reagent.setCoefficient(Double.parseDouble(data[0]));
      reagent.setCpdEntry(data[1]);
      reagent.setCompartment(Integer.parseInt(data[2]));
      if (reagent.getCoefficient() == 0.0) {
        logger.warn("coefficient value {}", reagent.getStoichiometry());
      }
      return reagent;
    }
  };
  
  public static Double getDouble(Object o) {
    if (o == null) {
      return null;
    }
    if (o instanceof Double) {
      return (Double) o;
    } else {
      String str = o.toString();
      if (NumberUtils.isParsable(str)) {
        return Double.parseDouble(str);
      } else {
        return null;
      }
    }
  }
  
  public static Map<String, ModelSeedReactionEntity> parse(InputStream is, InputStream isAliases) throws IOException {
    Map<String, List<ModelSeedReactionCrossreferenceEntity>> xrefsMap = parseAliases(isAliases);
    
    Map<String, ModelSeedReactionEntity> data = new HashMap<> ();
    ObjectMapper m = new ObjectMapper();
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    
    try {
      JavaType javaType = TypeFactory.defaultInstance().constructMapType(
          Map.class, String.class, ReactionJson.class);
      Map<String, ReactionJson> reactions = m.readValue(is, javaType);
      for (String id : reactions.keySet()) {
        ReactionJson reactionData = reactions.get(id);
        ModelSeedReactionEntity rxn = new ModelSeedReactionEntity();
        rxn.setEntry(id);
        rxn.setName(reactionData.name);
        rxn.setEquation(reactionData.equation);
        rxn.setCode(reactionData.code);
        rxn.setAbbreviation(reactionData.abbreviation);
        rxn.setDeltag(getDouble(reactionData.deltag));
        rxn.setDeltagerr(getDouble(reactionData.deltagerr));
//        rxn.setDeltag(getreactionData.deltag);
        if (reactionData.is_obsolete != null) {
          if (reactionData.is_obsolete == 0) {
            rxn.setObsolete(false);
          }
          if (reactionData.is_obsolete == 1) {
            rxn.setObsolete(true);
          }
        }
        
//        rxn.setObsolete(reactionData.is_obsolete);
        List<ModelSeedReactionReagentEntity> reagents = new ArrayList<> ();
        if (reactionData.stoichiometry != null && !reactionData.stoichiometry.trim().isEmpty()) {
          for (String s : reactionData.stoichiometry.split(";")) {
            ModelSeedReactionReagentEntity reagent = stoichParser.apply(s.trim());
            reagents.add(reagent);
          }
          rxn.setReagents(reagents);
        } else {
          logger.warn("empty stoich: {}", reactionData.id);
        }

//        rxn.setFormula(getString(compoundData, "formula"));
//        rxn.setDefaultCharge(getInteger(compoundData, "charge"));
//        rxn.setObsolete(getBoolean(compoundData, "is_obsolete"));
//        rxn.setCore(getBoolean(compoundData, "is_core"));
//        rxn.setCofactor(getBoolean(compoundData, "is_cofactor"));
//        rxn.setDeltaG(getDouble(compoundData, "deltag"));
//        rxn.setDeltaGErr(getDouble(compoundData, "deltagerr"));
//        rxn.setStructure(getString(compoundData, "structure"));
        List<ModelSeedReactionCrossreferenceEntity> xrefs = xrefsMap.get(id);
        if (xrefs != null) {
          rxn.setCrossreferences(xrefs);
        }
        
        data.put(id, rxn);
      }
    } catch (IOException e) {
      e.printStackTrace();
//      logger.error("IO Error: {}", e.getMessage());
    }
    
    is.close();
    isAliases.close();
    
    return data;
  }
  

  public void loadData() throws IOException {
    if (path != null) {
      File base = new File(path + "/" + version);
      if (!base.exists()) {
        if (base.mkdirs()) {
          logger.info("created folders: {}", base);
        }
      }
      File reactionsFile = new File(base.getAbsolutePath() + "/reactions.json");
      File aliasesFile = new File(base.getAbsolutePath() + "/reactions_aliases.tsv");
      if (!reactionsFile.exists()) {
        FileOutputStream fos = new FileOutputStream(reactionsFile);
        URL urlReactions = new URL("https://raw.githubusercontent.com/" + repository + "/" + version + "/Biochemistry/reactions.json");
        IOUtils.copy(urlReactions.openStream(), fos);
        fos.close();
      }
      if (!aliasesFile.exists()) {
        FileOutputStream fos = new FileOutputStream(aliasesFile);
        URL urlReactionsAliases = new URL("https://raw.githubusercontent.com/" + repository + "/" + version + "/Biochemistry/Aliases/Reactions_Aliases.tsv");
        IOUtils.copy(urlReactionsAliases.openStream(), fos);
        fos.close();
      }
      
      if (reactionsFile.exists() && aliasesFile.exists()) {
        FileInputStream fisReactions = new FileInputStream(reactionsFile);
        FileInputStream fisAliases = new FileInputStream(aliasesFile);
        data = parse(fisReactions, fisAliases);
        fisReactions.close();
        fisAliases.close();
      } else {
        logger.error("failed to load data Reactions: {}, Aliases: {}", reactionsFile.exists(), aliasesFile.exists());
      }
    } else {
      //loadDataGithub();
    }
    
    if (data != null) {
      for (String k : data.keySet()) {
        data.get(k).setVersion(version);
      }
    }
  }

  @Override
  public ModelSeedReactionEntity getReactionById(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ModelSeedReactionEntity getReactionByEntry(String entry) {
    if (data == null) {
      data = new HashMap<>();
      try {
        loadData();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return data.get(entry);
  }

  @Override
  public Set<Long> getAllReactionIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllReactionEntries() {
    if (data == null) {
      data = new HashMap<>();
      try {
        loadData();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return new HashSet<> (data.keySet());
  }

  @Override
  public ModelSeedReactionEntity getByEntry(String entry) {
    return this.getReactionByEntry(entry);
  }

  @Override
  public ModelSeedReactionEntity getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(ModelSeedReactionEntity o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(ModelSeedReactionEntity o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Set<Long> getAllIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllEntries() {
    return new HashSet<>(this.getAllReactionEntries());
  }
}
