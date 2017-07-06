package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.BHashMap;
import pt.uminho.sysbio.biosynthframework.BMap;

public class SearchTable<D, K> {
  public Map<D, Map<String, BMap<K, String>>> searchMap = new HashMap<> ();
  
  public Set<D> get() {
    return new HashSet<> (searchMap.keySet());
  }
  
  public Set<String> get(D d) {
    if (searchMap.containsKey(d)) {
      return new HashSet<> (searchMap.get(d).keySet());
    }
    return null;
  }
  
  public Set<K> find(String value, D database, String ns) {
    Set<K> result = null;
    Map<String, BMap<K, String>> databaseSubspace = null;
    BMap<K, String> searchValuesSubspace = null;
    if ((databaseSubspace = searchMap.get(database)) != null &&
        (searchValuesSubspace = databaseSubspace.get(ns)) != null) {
      result = searchValuesSubspace.bget(ns);
    }
    
    return result;
  }
  
  public void add(K k, String value, D database, String ns) {
    if (!searchMap.containsKey(database)) {
      searchMap.put(database, new HashMap<String, BMap<K, String>> ());
    }
    Map<String, BMap<K, String>> databaseSubspace = searchMap.get(database);
    if (!databaseSubspace.containsKey(ns)) {
      databaseSubspace.put(ns, new BHashMap<K, String>());
    }
    
    databaseSubspace.get(ns).put(k, value);
  }
}
