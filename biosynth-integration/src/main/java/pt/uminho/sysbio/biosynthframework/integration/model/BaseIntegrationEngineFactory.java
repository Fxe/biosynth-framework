package pt.uminho.sysbio.biosynthframework.integration.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class BaseIntegrationEngineFactory {
  
  private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationEngineFactory.class);
  
  private Map<Long, Pair<String, MetaboliteMajorLabel>> idToDbPair;
  private Map<String, Set<Set<String>>> nameToCpd;
  private Map<MetaboliteMajorLabel, Set<String>> entries = new HashMap<>();
  private final BiodbService biodbService;
  
  public BaseIntegrationEngineFactory(BiodbService biodbService) {
    this.biodbService = biodbService;
  }
  public static String SEP = "\t";
  
  public static void importNameData(Map<Long, String> idToName, 
      Map<Long, Set<Long>> nameIdToCpdSet, InputStream is) throws IOException {
    
    List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());

    for (int i = 1; i < lines.size(); i++) {
      //add ! at end to guarantee split
      try {
        String line = lines.get(i).concat(SEP + "!");
        String[] col = line.split(SEP);
        long id = Long.parseLong(col[0]);
        String name = col[1];
        String cpdArrayStr = col[2];
        Set<Long> cpdSet = new HashSet<> ();
        if (!cpdArrayStr.trim().isEmpty()) {
          for (String s : cpdArrayStr.split(" ")) {
            if (!s.trim().isEmpty()) {
              cpdSet.add(Long.parseLong(s));
            }
          }
        }
        idToName.put(id, name);
        nameIdToCpdSet.put(id, cpdSet);
      } catch (Exception e) {
        logger.warn("invalid line [{}] - {} : {}", i, lines.get(i), e.getMessage());
      }
    }
  }
    
  /**
   * 
   * @return Map Compound[Set] -> Name
   */
  public static Map<Set<String>, String> buildNameDictionary(InputStream is) throws IOException {
    logger.info("build name dictionary ...");
    Map<Long, String> nameIdToName = new HashMap<> ();
    Map<Long, Set<Long>> nameIdToCpdSet = new HashMap<> ();
    importNameData(nameIdToName, nameIdToCpdSet, is);
    Map<Set<String>, String> nameDictionary = new HashMap<> ();
    for (long nameId : nameIdToName.keySet()) {
      Set<String> g = new HashSet<> ();
      for (long cpdId : nameIdToCpdSet.get(nameId)) {
        g.add(Long.toString(cpdId));
      }
      nameDictionary.put(g, nameIdToName.get(nameId).trim().toLowerCase());
    }
    
    return nameDictionary;
  }
  
  public void setupNameData(String nameFile) {
    
    logger.info("loading information for name matching ...");
    try (InputStream is = new FileInputStream(nameFile)) {
      Map<Set<String>, String> nameData = buildNameDictionary(is);
      nameToCpd = CollectionUtils.reverseMap(nameData);
      
      idToDbPair = new HashMap<> ();
      for (Set<String> ids : nameData.keySet()) {
        for (String id : ids) {
          long cpdId = Long.parseLong(id);
          String cpdEntry = biodbService.getEntryById(cpdId);
          String databaseStr = biodbService.getDatabaseById(cpdId);
          Pair<String, MetaboliteMajorLabel> p = 
              new ImmutablePair<>(cpdEntry, MetaboliteMajorLabel.valueOf(databaseStr));
          idToDbPair.put(cpdId, p);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
  
  public NameBaseIntegrationEngine buildNameBaseIntegrationEngine(String nameFile) {
    if (idToDbPair == null || nameToCpd == null) {
      setupNameData(nameFile);
    }
    
    NameBaseIntegrationEngine e = new NameBaseIntegrationEngine(nameToCpd, idToDbPair);
    return e;
  }
  
  public TrieIdBaseIntegrationEngine buildTrieIdBaseIntegrationEngine() {
    TrieIdBaseIntegrationEngine e = new TrieIdBaseIntegrationEngine();
    e.ignoreCase = false;
    Set<MetaboliteMajorLabel> dbs = new HashSet<> ();
    dbs.add(MetaboliteMajorLabel.Seed);
    dbs.add(MetaboliteMajorLabel.ModelSeed);
    dbs.add(MetaboliteMajorLabel.BiGG);
    dbs.add(MetaboliteMajorLabel.BiGG2);
    dbs.add(MetaboliteMajorLabel.LigandCompound);
    dbs.add(MetaboliteMajorLabel.LigandGlycan);
    dbs.add(MetaboliteMajorLabel.LigandDrug);
    
    for (MetaboliteMajorLabel db : dbs) {
      if (!entries.containsKey(db)) {
        Set<String> dict = new HashSet<> ();
        for (long id : biodbService.getIdsByDatabaseAndType(db.toString(), "Metabolite")) {
          if (MetaboliteMajorLabel.BiGG2.equals(db)) {
            String alias = biodbService.getEntityProperty(id, "alias");
            if (alias != null) {
              dict.add(alias);
            }
          } else {
            dict.add(biodbService.getEntryById(id));
          }
        }
        entries.put(db, dict);
      }
      e.setup(db, entries.get(db));
    }
    
    return e;
  }
  
  public IdBaseIntegrationEngine buildIdBaseIntegrationEngine() {
    SearchTable<MetaboliteMajorLabel, String> searchTable = new SearchTableFactory(biodbService)
        .withDatabase(MetaboliteMajorLabel.BiGG)
//        .withDatabase(MetaboliteMajorLabel.BiGG2)
        .withDatabase(MetaboliteMajorLabel.ModelSeed)
        .withDatabase(MetaboliteMajorLabel.Seed)
        .withDatabase(MetaboliteMajorLabel.LigandCompound)
        .withDatabase(MetaboliteMajorLabel.LigandGlycan)
        .build();
    
    TokenSwapLookupMethod tkLookupMethod = new TokenSwapLookupMethod();
    tkLookupMethod.acceptedTokens.add("_DASH");
    tkLookupMethod.acceptedTokens.add("_L");
    tkLookupMethod.acceptedTokens.add("_D");
    tkLookupMethod.acceptedTokens.add("_R");
    tkLookupMethod.acceptedTokens.add("_S");
    tkLookupMethod.acceptedTokens.add("_bD");
    tkLookupMethod.addSwap("_DASH_", "_");
    tkLookupMethod.addSwap("_D", "-D");
    tkLookupMethod.addSwap("_R", "-R");
    tkLookupMethod.addSwap("_S", "-S");
    tkLookupMethod.addSwap("_bD", "-bD");
    tkLookupMethod.addSwap("_", "-");
    
    
    IdBaseIntegrationEngine be1 = new IdBaseIntegrationEngine(searchTable);
    be1.lookupMethods.put(MetaboliteMajorLabel.BiGG, tkLookupMethod);
//    be1.lookupMethods.put(MetaboliteMajorLabel.BiGG2, tkLookupMethod2);
    be1.lookupMethods.put(MetaboliteMajorLabel.Seed, 
        new PrefixNumberSequenceLookupMethod("cpd"));
    be1.lookupMethods.put(MetaboliteMajorLabel.ModelSeed, 
        new PrefixNumberSequenceLookupMethod("cpd"));
    be1.lookupMethods.put(MetaboliteMajorLabel.LigandCompound, 
        new PrefixNumberSequenceLookupMethod("C"));
    be1.lookupMethods.put(MetaboliteMajorLabel.LigandGlycan, 
        new PrefixNumberSequenceLookupMethod("G"));
    be1.lookupMethods.put(MetaboliteMajorLabel.LigandDrug, 
        new PrefixNumberSequenceLookupMethod("D"));
    
    return be1;
  }
}
