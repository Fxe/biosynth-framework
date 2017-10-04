package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.BFunction;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;
import pt.uminho.sysbio.biosynthframework.util.IntegrationMapUtils;

public class SpecieIntegrationFacade {
  private static final Logger logger = LoggerFactory.getLogger(SpecieIntegrationFacade.class);
  
  public IdPattern idPattern;
  protected Map<String, String> spiToCompartment = new HashMap<> ();
  protected Map<String, EntryPattern> patterns = new HashMap<> ();
  public Map<String, ?> names = new HashMap<> ();
  public IntegrationMap<String, MetaboliteMajorLabel> dbLinks;
  
  public ConflictResolver specieConflictResolve = null;
  public Map<MetaboliteMajorLabel, BFunction<List<Set<String>>, List<Set<String>>>> 
  matchResolver = new HashMap<> ();
  
  public Object stringTokenizer;
  public Object dictionary;
  
  public Map<String, Map<MetaboliteMajorLabel, String>> clean;
  
  public List<IntegrationMap<String, MetaboliteMajorLabel>> isets = new ArrayList<> ();
  public Map<String, Map<MetaboliteMajorLabel, List<Set<String>>>> mintegration;
  
  public Map<String, BaseIntegrationEngine> baseEngines = new HashMap<> ();
  public List<List<IntegrationEngine>> engines = new ArrayList<> ();
  
  public void generatePatterns() {
    IdTokenizer tokenizer = new IdTokenizer();
    idPattern = tokenizer.generatePattern(patterns.keySet());
    patterns = tokenizer.patterns;
  }
  
  public boolean valid(String id) {
    return id != null && !id.trim().isEmpty();
  }
  
  public void addSpecie(String id, String cmpId) {
    if (valid(id) && valid(cmpId)) {
      this.patterns.put(id, null);
      this.spiToCompartment.put(id, cmpId);
    }
  }

  public Map<String, EntryPattern> getPatterns() { return patterns;}

  public void status(IntegrationMap<String, MetaboliteMajorLabel> imap) {
    Map<Object, Integer> counter = new HashMap<> ();
    Map<Object, Double> cover = new HashMap<> ();
    
    for (String e : imap.keySet()) {
      Map<MetaboliteMajorLabel, Set<String>> a = imap.get(e);
      boolean any = false;
      for (MetaboliteMajorLabel db : a.keySet()) {
        Set<String> refs = a.get(db);
        if (refs != null && !refs.isEmpty()) {
          any = true;
          CollectionUtils.increaseCount(counter, db, 1);
        }
      }
      if (any) {
        CollectionUtils.increaseCount(counter, "has_reference", 1);
      }
    }
    
    for (Object k : counter.keySet()) {
      int count = counter.get(k);
      cover.put(k, (double) count / patterns.size());
    }
    logger.info("{}", Joiner.on(", ").withKeyValueSeparator(": ").join(cover));
  }
  
  public void status(Map<String, Map<MetaboliteMajorLabel, List<Set<String>>>> imap) {
    Map<Object, Integer> counter = new HashMap<> ();
    Map<Object, Double> cover = new HashMap<> ();
    
    for (String e : imap.keySet()) {
      Map<MetaboliteMajorLabel, List<Set<String>>> a = imap.get(e);
      boolean any = false;
      for (MetaboliteMajorLabel db : a.keySet()) {
        List<Set<String>> refs = a.get(db);
        if (refs != null && !refs.isEmpty()) {
          any = true;
          CollectionUtils.increaseCount(counter, db, 1);
        }
      }
      if (any) {
        CollectionUtils.increaseCount(counter, "has_reference", 1);
      }
    }
    
    for (Object k : counter.keySet()) {
      int count = counter.get(k);
      cover.put(k, (double) count / patterns.size());
    }
    logger.info("{}", Joiner.on(", ").withKeyValueSeparator(": ").join(cover));
  }
  
  public void status2(Map<String, Map<MetaboliteMajorLabel, String>> imap) {
    Map<Object, Integer> counter = new HashMap<> ();
    Map<Object, Double> cover = new HashMap<> ();
    
    for (String e : imap.keySet()) {
      Map<MetaboliteMajorLabel, String> a = imap.get(e);
      boolean any = false;
      for (MetaboliteMajorLabel db : a.keySet()) {
        String refs = a.get(db);
        if (refs != null && !refs.isEmpty()) {
          any = true;
          CollectionUtils.increaseCount(counter, db, 1);
        }
      }
      if (any) {
        CollectionUtils.increaseCount(counter, "has_reference", 1);
      }
    }
    
    for (Object k : counter.keySet()) {
      int count = counter.get(k);
      cover.put(k, (double) count / patterns.size());
    }
    logger.info("{}", Joiner.on(", ").withKeyValueSeparator(": ").join(cover));
  }
  
  public void run() {
    List<List<IntegrationMap<String, MetaboliteMajorLabel>>> mapping = new ArrayList<> ();
    
    
    mapping.add(new ArrayList<IntegrationMap<String, MetaboliteMajorLabel>> ());
    //do base
    for (String engineId : baseEngines.keySet()) {
      BaseIntegrationEngine engine = baseEngines.get(engineId);
      logger.debug("[BASE] {}", engineId);
      IntegrationMap<String, MetaboliteMajorLabel> ret = engine.integrate();
      status(ret);
      mapping.get(0).add(ret);
    }
    
    for (int i = 0; i < engines.size(); i++) {
      
      for (IntegrationEngine engine : engines.get(i)) {
        mapping.add(new ArrayList<IntegrationMap<String, MetaboliteMajorLabel>> ());
        for (IntegrationMap<String, MetaboliteMajorLabel> prev : mapping.get(i)) {
          logger.debug("[LAY{}] -> [LAY{}] {}", i+1, i, engine);
          IntegrationMap<String, MetaboliteMajorLabel> ret = engine.integrate(prev);
          status(ret);
          mapping.get(i + 1).add(ret);
        }
      }
    }
    
    isets.clear();
    for (List<IntegrationMap<String, MetaboliteMajorLabel>> isetsList : mapping) {
      for (IntegrationMap<String, MetaboliteMajorLabel> imap : isetsList) {
        if (!imap.isEmpty()) {
          isets.add(imap);
        }
      }
    }
    
    logger.info("[DONE] - {} imaps", isets.size());
  }
  

  
  public IntegrationMap<String, MetaboliteMajorLabel> biggSplash(
      IntegrationMap<String, MetaboliteMajorLabel> integration, BiodbService biodbService) {
    IntegrationMap<String, MetaboliteMajorLabel> result = new IntegrationMap<> ();
    
    SearchTable<MetaboliteMajorLabel, String> searchTable = new SearchTable<>();
    for (String entry : integration.keySet()) {
      Set<String> bigg = integration.get(entry).get(MetaboliteMajorLabel.BiGG);
      Set<String> bigg2 = integration.get(entry).get(MetaboliteMajorLabel.BiGG2);
      //bigg2 -> bigg
      if (bigg == null && bigg2 != null && !bigg2.isEmpty()) {
        for (String bigg2Entry : bigg2) {
          System.out.println(bigg2Entry);
        }
      }
      //bigg -> bigg2
      if (bigg2 == null && bigg != null && !bigg.isEmpty()) {
        for (String biggEntry : bigg) {
          Set<String> ret = searchTable.find(biggEntry, 
                                             MetaboliteMajorLabel.BiGG2, 
                                             "universalEntry");
          result.addIntegration(entry, MetaboliteMajorLabel.BiGG2, ret);
//          System.out.println(ret);
        }
      }
    }
//    searchMap.
//    Transaction dataTx = graphDataService.beginTx();
    
//    for (String e : integration.keySet()) {
//      Set<String> bigg = integration.get(e).get(MetaboliteMajorLabel.BiGG);
//      Set<String> bigg2 = integration.get(e).get(MetaboliteMajorLabel.BiGG2);
//      //bigg2 -> bigg
//      if (bigg == null && bigg2 != null && !bigg2.isEmpty()) {
//        for (String bigg2entry : bigg2) {
//          Node bigg2Node = Neo4jUtils.getNodeByEntry(MetaboliteMajorLabel.BiGG2, bigg2entry, graphDataService);
//          String uentry = (String) bigg2Node.getProperty("universalEntry");
//          Node biggNode = Neo4jUtils.getNodeByEntry(MetaboliteMajorLabel.BiGG, uentry, graphDataService);
//          if (biggNode != null) {
//            logger.debug("{} -> {}", bigg2entry, uentry);
//            result.addIntegration(e, MetaboliteMajorLabel.BiGG, uentry);
//          }
//        }
//      }
//    }
    
//    dataTx.failure();
//    dataTx.close();
    
    return result;
  }
  
  public Map<String, Map<MetaboliteMajorLabel, String>> build() {
    mintegration = IntegrationMapUtils.merge(isets);
    
    status(mintegration);
    
    Map<String, Map<MetaboliteMajorLabel, String>> consensus = 
        IntegrationMapUtils.consensus2(mintegration, matchResolver);
   
    status2(consensus);
    
    this.clean = cleanConflicts(consensus, null, spiToCompartment);
    
    status2(clean);
    return consensus;
  }
  
  public Map<String, Map<MetaboliteMajorLabel, String>> cleanConflicts(
      Map<String, Map<MetaboliteMajorLabel, String>> dbSwap, String model, Map<String, String> spiToCmp) {
    Map<String, Map<MetaboliteMajorLabel, String>> result = new HashMap<> ();

    //DATABASE -> SPI_OLD -> SPI_NEW
//    Map<String, String> oldToNewMap = new HashMap<> ();
    Map<MetaboliteMajorLabel, BMap<String, String>> dbMapping = new HashMap<>();
    for (String id : dbSwap.keySet()) {
        Map<MetaboliteMajorLabel, String> mapping = dbSwap.get(id);
        String cmpEntry = spiToCmp.get(id);
//        long cmpId = biodbService.getSpecieCompartmentId(spiNode.getId());
        for (MetaboliteMajorLabel database : mapping.keySet()) {
          if (!dbMapping.containsKey(database)) {
            dbMapping.put(database, new BHashMap<String, String>());
          }
          String cpdEntry = mapping.get(database);
          String dbSpiEntry = String.format("%s_%s", cpdEntry, cmpEntry);
          dbMapping.get(database).put(id, dbSpiEntry);
        }
    }
    
    for (String id : dbSwap.keySet()) {
      result.put(id, new HashMap<MetaboliteMajorLabel, String> ());
      Map<MetaboliteMajorLabel, String> mapping = dbSwap.get(id);
      for (MetaboliteMajorLabel database : mapping.keySet()) {
        BMap<String, String> dbMap = dbMapping.get(database);
        String dbSpiEntry = dbMap.get(id);
        Set<String> species = dbMap.bget(dbSpiEntry);
        if (specieConflictResolve != null) {
          species = specieConflictResolve.resolve(id, species);
        }
        if (species.size() == 1) {
          result.get(id).put(database, mapping.get(database));
        } else {
          logger.trace("s:{} dbSpiEntry: {}", species, dbSpiEntry);
        }
//        logger.info("{} : {} -> {} -> {}", id, database, dbSpiEntry, species);
      }
    }
    
    return result;
  }
}
