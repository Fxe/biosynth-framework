package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntegrationMap<T, C> extends HashMap<T, Map<C, Set<T>>> {

  private static final long serialVersionUID = 1L;

  public void addIntegration(T object, 
                             C database, 
                             T ref) {
    
    if (!this.containsKey(object)) {
      this.put(object, new HashMap<C, Set<T>> ());
    }
    if (!this.get(object).containsKey(database)) {
      this.get(object).put(database, new HashSet<T> ());
    }
    this.get(object).get(database).add(ref);
  }
  
  public void addIntegration(T object, 
      C database, 
      Set<T> ref) {
    if (ref != null && !ref.isEmpty()) {
      if (!this.containsKey(object)) {
        this.put(object, new HashMap<C, Set<T>> ());
      }
      if (!this.get(object).containsKey(database)) {
        this.get(object).put(database, new HashSet<T> ());
      }
      this.get(object).get(database).addAll(ref);
    }
  }

  public Set<T> keyLookUp(C db, T id) {
    Set<T> ks = new HashSet<> ();
    for (T k : this.keySet()) {
      Map<C, Set<T>> mapping = this.get(k);
      Set<T> ids = mapping.get(db);
      if (ids != null && ids.contains(id)) {
        ks.add(k);
      }
    }
    return ks;
  }
}
