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

public class IntegrationEngineName {

  private static final Logger logger = LoggerFactory.getLogger(IntegrationEngineName.class);
  
  private final BiodbService biodbService;
  
  public IntegrationEngineName(BiodbService biodbService) {
    this.biodbService = biodbService;
  }
  
  public List<List<Set<Long>>> integrate(
      List<String> databases, 
      List<String> types,
      List<Function<Long, String>> t) {
    
    List<Map<Long, String>> idsToNameList = new ArrayList<> ();
    for (int i = 0; i < databases.size(); i++) {
      Function<Long, String> f = t.size() > i ? t.get(i) : null;
      String db = databases.get(i);
      Set<Long> idSet = biodbService.getIdsByDatabaseAndType(db, types.get(i));
      Map<Long, String> idToNameMap = new HashMap<> ();
      for (long id : idSet) {
        String name = biodbService.getNamePropertyById(id);
        //apply t function
        if (f != null) {
          logger.debug("f(name) {} -> {}", biodbService.getNamePropertyById(id), name);
          name = f.apply(id);
        }
        idToNameMap.put(id, name);
      }
      idsToNameList.add(idToNameMap);
    }
    
    List<List<Set<Long>>> result = CollectionUtils.sortMaps(idsToNameList);

    return result;
  }
}
