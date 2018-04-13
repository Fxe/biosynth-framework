package pt.uminho.sysbio.biosynthframework.biodb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.ExternalReference;

public class ModelSeedAliasesAdapter {

  private static final Logger logger = LoggerFactory.getLogger(ModelSeedAliasesAdapter.class);

  public Map<String, Set<ExternalReference>> mseedRefMap;
  public Map<String, Set<ExternalReference>> omseedRefMap;
  public Map<String, Set<String>> multiSets = new HashMap<> ();

  public String header;
  //public BMap<ExternalReference, String> refToMseedMap = new BHashMap<>();

  public static ModelSeedAliasesAdapter fromModelSeedTsv(String path) {
    ModelSeedAliasesAdapter adapter = null;
    InputStream is = null;
    try {
      is = new FileInputStream(path);
      List<String> lines = IOUtils.readLines(is);

      //    System.out.println(lines.get(0));

      Map<String, Set<ExternalReference>> mseedEntryMap = new HashMap<> ();
      Map<String, Set<ExternalReference>> omseedEntryMap = new HashMap<> ();

      adapter = new ModelSeedAliasesAdapter();
      adapter.header = lines.get(0);

      for (int i = 1; i < lines.size(); i++) {
        String[] cols = lines.get(i).concat("\t").concat("!").split("\t");
        //      System.out.println( cols[0]);
        String msidStr = cols[0];
        String omsidStr = cols[1];
        String databaseEntry = cols[2];
        String database = cols[3];
        ExternalReference ref = null;

        if (databaseEntry != null && !databaseEntry.isEmpty() &&
            database != null && !database.isEmpty()) {
          ref = new ExternalReference(databaseEntry, database);
        } else {
          logger.info("!!");
        }

        Set<String> mset = new HashSet<> ();
        for (String mseedEntry : msidStr.split("\\|")) {
          //        if (adapter.refToMseedMap.put(ref, mseedEntry.trim()) != null) {
          //          logger.warn("duplicate ref {}", ref, mseedEntry);
          //        }
          if (!mseedEntryMap.containsKey(mseedEntry.trim())) {
            mseedEntryMap.put(mseedEntry.trim(), new HashSet<ExternalReference> ());
          }
          mseedEntryMap.get(mseedEntry.trim()).add(ref);
          mset.add(mseedEntry);
        }

        if (mset.size() > 1) {
          for (String e : mset) {
            adapter.multiSets.put(e, new TreeSet<>(mset));
          }
        }

        for (String mseedEntry : omsidStr.split("\\|")) {
          if (!omseedEntryMap.containsKey(mseedEntry.trim())) {
            omseedEntryMap.put(mseedEntry.trim(), new HashSet<ExternalReference> ());
          }
          omseedEntryMap.get(mseedEntry.trim()).add(ref);
        }

      }

      adapter.mseedRefMap = mseedEntryMap;
      adapter.omseedRefMap = omseedEntryMap;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
    }
    return adapter;
  }

  public Map<String, Map<String, Map<ExternalReference, Set<String>>>> structures = new HashMap<> ();

  public Map<String, Map<ExternalReference, Set<String>>> getStructures(String cpdEntry) {
    return structures.get(cpdEntry);
  }

  public Map<ExternalReference, Set<String>> getStructures(String cpdEntry, String type) {
    if (structures.containsKey(cpdEntry)) {
      return structures.get(cpdEntry).get(type);
    }
    return null;
  }

  public void getStructures(String cpdEntry, String type, String source) {
    structures.get(cpdEntry);
  }

  public void loadStructures(String path) {
    InputStream is = null;
    try {
      is = new FileInputStream(path);
      List<String> lines = IOUtils.readLines(is);

      for (int i = 1; i < lines.size(); i++) {
        String[] cols = lines.get(i).concat("\t").concat("!").split("\t");
        String cpdEntry = cols[0].trim();
        String refEntry = cols[1].trim();
        String refDb = cols[2].trim();
        String stringStructure = cols[3].trim();
        ExternalReference eref = new ExternalReference(cpdEntry, refDb);
        String structureType = "SMILES";
        if (stringStructure.startsWith("InChI")) {
          structureType = "InChI";
        }

        if (!structures.containsKey(cpdEntry)) {
          structures.put(cpdEntry, new HashMap<String, Map<ExternalReference, Set<String>>> ());
        }
        if (!structures.get(cpdEntry).containsKey(structureType)) {
          structures.get(cpdEntry).put(structureType, new HashMap<ExternalReference, Set<String>> ());
        }
        if (!structures.get(cpdEntry).get(structureType).containsKey(eref)) {
          structures.get(cpdEntry).get(structureType).put(eref, new HashSet<String> ());
        }
        structures.get(cpdEntry).get(structureType).get(eref).add(stringStructure);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  public void addDatabaseReference(String mseedEntry, 
      String cpdEntry, String db) {
    ExternalReference refAdd = new ExternalReference(cpdEntry, db);
    mseedRefMap.get(mseedEntry).add(refAdd);
    logger.info("ADD {} -> {}", refAdd, mseedEntry);
  }

  public void addDatabaseReference(String cpdEntry1, String db1, 
      String cpdEntry2, String db2) {
    ExternalReference refLookup = buildExternalReference(cpdEntry1, db1);
    ExternalReference refAdd = buildExternalReference(cpdEntry2, db2);
    for (String mseedEntry : mseedRefMap.keySet()) {
      if (mseedRefMap.get(mseedEntry).contains(refLookup)) {
        mseedRefMap.get(mseedEntry).add(refAdd);
        logger.info("ADD {} -> {}", refAdd, mseedEntry);
      }
    }
  }

  public ExternalReference buildExternalReference(String cpdEntry, String db) {
    if ("LigandCompound".equals(db) ||
        "LigandGlycan".equals(db) ||
        "LigandDrug".equals(db)) {
      return new ExternalReference(cpdEntry, "KEGG");
    }
    return new ExternalReference(cpdEntry, db);
  }

//  public ExternalReference buildExternalReference(String cpdEntry, MetaboliteMajorLabel db) {
//    if (MetaboliteMajorLabel.LigandCompound.equals(db) ||
//        MetaboliteMajorLabel.LigandGlycan.equals(db) ||
//        MetaboliteMajorLabel.LigandDrug.equals(db)) {
//      return new ExternalReference(cpdEntry, "KEGG");
//    }
//    return new ExternalReference(cpdEntry, db.toString());
//  }

  public static ExternalReference convertToBiodb(ExternalReference eref) {
    String e = eref.entry;
    String s = eref.source;

    if (s.equals("KEGG")) {
      switch (e.charAt(0)) {
      case 'C': s = "LigandCompound"; break;
      case 'G': s = "LigandGlycan"; break;
      case 'D': s = "LigandDrug"; break;
      default: logger.warn("bad kegg: {}", s); break;
      }
    }
    if ("MetaCyc".equals(s)) {
      e = "META:".concat(e);
    }

    return new ExternalReference(e, s);
  }

  public boolean isMultiSet(Set<String> set) {
    if (set != null && !set.isEmpty()) {
      String e = set.iterator().next();
      Set<String> mset = this.multiSets.get(e);
      if (set.equals(mset)) {
        return true;
      }
    }

    return false;
  }

  public Set<ExternalReference> getExternalReferences(String mseedEntry, String db) {
    Set<ExternalReference> result = new HashSet<> ();
    Set<ExternalReference> erefs = this.mseedRefMap.get(mseedEntry);

    if (erefs != null) {
      for (ExternalReference eref : erefs) {
        if (eref.source.equals(db)) {
          result.add(eref);
        }
      }
    }
    return result;
  }

  public Set<String> findModelSeedByReferemce(ExternalReference eref) {
    Set<String> result = new HashSet<> ();
    for (String mseed : mseedRefMap.keySet()) {
      if (mseedRefMap.get(mseed).contains(eref)) {
        result.add(mseed);
      }
    }
    return result;
  }
}
