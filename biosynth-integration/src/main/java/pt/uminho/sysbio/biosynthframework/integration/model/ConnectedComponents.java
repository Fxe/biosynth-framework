package pt.uminho.sysbio.biosynthframework.integration.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectedComponents<T> extends HashSet<Set<T>> {

  private static final long serialVersionUID = 1L;
  
  protected Map<T, Set<T>> mapping = new HashMap<> ();
  
  @Override
  public boolean add(Set<T> e) {
    if (e == null || e.isEmpty()) {
      throw new IllegalArgumentException("invalid arg " + e);
    }
    for (T t : e) {
      if (mapping.containsKey(t)) {
        throw new IllegalArgumentException("duplicates " + t);
      }
    }

    for (T t : e) {
      mapping.put(t, e);
    }
    return super.add(e);

  }
  
  public boolean containsValue(T t) {
    return mapping.containsKey(t);
  }
  
  public Set<T> getConnectedComponentOf(T t) {
    return mapping.get(t);
  }
}
