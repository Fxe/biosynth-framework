package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import pt.uminho.sysbio.biosynthframework.io.biodb.modelseed.JsonModelSeedRoleDao;
import pt.uminho.sysbio.biosynthframework.util.CollectionUtils;

public class ModelSeedUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(ModelSeedUtils.class);
  
  /**
   * Loads Roles.json mapping rxnXXXXX to [fr.XXXX]
   * @param rolesJson Roles.json
   * @return a map: f: rxn -> [role]
   */
  public static Map<String, Set<String>> getReactionRoles(File rolesJson) {
    Map<String, Set<String>> result = new HashMap<> ();
    Resource roleJson = new FileSystemResource(rolesJson);
    JsonModelSeedRoleDao roleDao = new JsonModelSeedRoleDao.Builder().build(roleJson);
    for (String roleEntry : roleDao.getAllEntries()) {
      ModelSeedRole role = roleDao.getByEntry(roleEntry);
      for (String rxnEntry : role.reactions) {
        rxnEntry = rxnEntry.split(";")[0].trim();
        CollectionUtils.insertHS(rxnEntry, roleEntry, result);
      }
    }
    return result;
  }
  
  /**
   * Loads Roles.json mapping function(String) to [fr.XXXX]
   * @param rolesJson Roles.json
   * @return
   */
  public static Map<String, Set<String>> getRolesMap(File rolesJson) {
    Map<String, Set<String>> result = new HashMap<> ();
    Resource roleJson = new FileSystemResource(rolesJson);
    JsonModelSeedRoleDao roleDao = new JsonModelSeedRoleDao.Builder().build(roleJson);
    
    for (String roleEntry : roleDao.getAllEntries()) {
      ModelSeedRole role = roleDao.getByEntry(roleEntry);
      String function = role.name.trim().toLowerCase();
      CollectionUtils.insertHS(function, roleEntry, result);
    }
    
    int good = 0;
    int bad = 0;
    for (String k : result.keySet()) {
      if (result.get(k).size() > 1) {
        bad++;
      } else {
        good++;
      }
    }
    
    if (bad > 0) {
      logger.debug("roles good: {}, bad: {}", good, bad);      
    }

    return result;
  }
  
  /**
   * Loads Roles.json mapping rxnXXXXX to [function(String)]
   * @param rolesJson Roles.json
   * @return
   */
  public static Map<String, Set<String>> loadFunctionToRxn(File rolesJson) {
    Map<String, Set<String>> functionToFr = getRolesMap(rolesJson);
    Map<String, Set<String>> rxnRoles = getReactionRoles(rolesJson);
    Map<String, Set<String>> rolesToRxn = new HashMap<> ();
    for (String rxnEntry : rxnRoles.keySet()) {
      for (String roleEntry : rxnRoles.get(rxnEntry)) {
        CollectionUtils.insertHS(roleEntry, rxnEntry, rolesToRxn);
      }
    }
    
    Map<String, Set<String>> rastToRxn = new HashMap<>();
    for (String rast : functionToFr.keySet()) {
      for (String fr : functionToFr.get(rast)) {
        Set<String> rxnEntrySet = rolesToRxn.get(fr);
        if (rxnEntrySet != null && !rxnEntrySet.isEmpty()) {
          if (!rastToRxn.containsKey(rast)) {
            rastToRxn.put(rast.toLowerCase().trim(), new HashSet<String>());
          }
          rastToRxn.get(rast.toLowerCase().trim()).addAll(rxnEntrySet);
        }
      }
    }
    
    return rastToRxn;
  }
  
  public static Set<String> annotationSplit(String str) {
    Set<String> result = new HashSet<> ();
    for (String s : str.split(" @ |; | / ")) {
      result.add(s.trim());
    }
    
    logger.trace("Split: {} - {}", str, result);
    
    return result;
  };
  
  public static int getIdNumberValue(String id) {
    int value = -1;
    
    if (id != null && (id.startsWith("cpd") || id.startsWith("rxn"))) {
      value = Integer.parseInt(id.substring(3));
    }
    
    return value;
  }
  
  public static String selectLowestId(Collection<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return null;
    }
    
    Iterator<String> it = ids.iterator();
    String id = it.next();
    int low = getIdNumberValue(id);
    
    logger.debug("id: {}, low: {}", id, low);
    
    while (it.hasNext()) {
      String i = it.next();
      int v = getIdNumberValue(i);
      logger.debug("id: {}, low: {}", i, v);
      if (low < 0 || (v > 0 && (v < low || low <= 0))) {
        low = v;
        id = i;
      }
    }
    
    return id;
  }
}
