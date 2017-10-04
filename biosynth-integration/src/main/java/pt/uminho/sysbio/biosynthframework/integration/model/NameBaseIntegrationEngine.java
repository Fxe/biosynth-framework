package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class NameBaseIntegrationEngine implements BaseIntegrationEngine {
  
  private static final Logger logger = LoggerFactory.getLogger(NameBaseIntegrationEngine.class);
  
//  private final Map<Set<String>, String> nameData;
  private final Map<String, Set<Set<String>>> nameToCpd;
  private final Map<Integer, Set<String>> sizeToString = new HashMap<> ();
  private final Map<Long, Pair<String, MetaboliteMajorLabel>> idToDbPair;
  
  public Set<String> mapped = new HashSet<> ();
  public Set<String> unmapped = new HashSet<> ();
  
  public BMap<String, String> spiEntryToName = new BHashMap<> ();
//  Set<String> spiEntrySet = new HashSet<> ();
  
  public NameBaseIntegrationEngine(Map<String, Set<Set<String>>> nameToCpd,
      Map<Long, Pair<String, MetaboliteMajorLabel>> idToDbPair) {
    this.nameToCpd = nameToCpd;
    this.idToDbPair = idToDbPair;
//    this.nameData = nameData;
//    nameToCpd = CollectionUtils.reverseMap(this.nameData);
    for (String name : nameToCpd.keySet()) {
      int l = name.length();
      if (!sizeToString.containsKey(l)) {
        sizeToString.put(l, new HashSet<String> ());
      }
      
      sizeToString.get(l).add(name);
    }
    
    logger.debug("size: {}", sizeToString.size());
  }
  
  @Override
  public IntegrationMap<String, MetaboliteMajorLabel> integrate() {
    IntegrationMap<String, MetaboliteMajorLabel> imap = new IntegrationMap<>();
    
    Set<String> mapped = new HashSet<> ();
    Set<String> unmapped = new HashSet<> (spiEntryToName.bkeySet());
    for (String n : spiEntryToName.bkeySet()) {
      int l = n.length();
      if (sizeToString.get(l) != null && sizeToString.get(l).contains(n)) {
//        System.out.println(n);
        Set<Set<String>> cpdIds = nameToCpd.get(n);
        List<Pair<String, MetaboliteMajorLabel>> refs = new ArrayList<> ();
        for (Set<String> s : cpdIds) {
          for (String e : s) {
            long cpdId = Long.parseLong(e);
            if (!idToDbPair.containsKey(cpdId)) {
              logger.warn("id {} not found", cpdId);
            } else {
              String cpdEntry = idToDbPair.get(cpdId).getLeft();
              MetaboliteMajorLabel database = idToDbPair.get(cpdId).getRight();
              refs.add(new ImmutablePair<String, MetaboliteMajorLabel>(cpdEntry, database));
            }
          }
        }
        
        Set<String> entries = spiEntryToName.bget(n);
        for (String e : entries) {
          for (Pair<String, MetaboliteMajorLabel> p : refs) {
            imap.addIntegration(e, p.getRight(), p.getLeft());  
          }
          
        }
        
        mapped.add(n);
      }
    }
    unmapped.removeAll(mapped);
    
    return imap;
  }

}
