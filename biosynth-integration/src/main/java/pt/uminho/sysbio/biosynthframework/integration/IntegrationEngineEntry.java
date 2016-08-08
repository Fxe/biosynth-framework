package pt.uminho.sysbio.biosynthframework.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynth.integration.BiodbService;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

/**
 * Generate an integration set by equal entry
 * @author Filipe Liu
 *
 */
public class IntegrationEngineEntry {
  
  private static final Logger logger = LoggerFactory.getLogger(IntegrationEngineEntry.class);
  
  private final BiodbService biodbService;
  
  public IntegrationEngineEntry(BiodbService biodbService) {
    this.biodbService = biodbService;
  }
  
  public List<List<Set<Long>>> integrate(
      List<String> databases, 
      List<String> types,
      List<Function<String, String>> t) {
    
    List<Map<Long, String>> idsToEntryList = new ArrayList<> ();
    for (int i = 0; i < databases.size(); i++) {
      Function<String, String> f = t.size() > i ? t.get(i) : null;
      String db = databases.get(i);
      Set<Long> idSet = biodbService.getIdsByDatabaseAndType(db, types.get(i));
      Map<Long, String> idToEntryMap = new HashMap<> ();
      for (long id : idSet) {
        String entry = biodbService.getEntryById(id);
        //apply t function
        if (f != null) {
          logger.debug("f(entry) {} -> {}", biodbService.getEntryById(id), entry);
          entry = f.apply(entry);
        }
        idToEntryMap.put(id, entry);
      }
      idsToEntryList.add(idToEntryMap);
    }

    List<List<Set<Long>>> result = CollectionUtils.sortMaps(idsToEntryList);

    return result;
  }
  

}
