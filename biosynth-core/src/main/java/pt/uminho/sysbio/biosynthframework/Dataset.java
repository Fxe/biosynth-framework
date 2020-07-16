package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Dataset<A, B, D> implements Iterable<A> {
  public Map<A, Map<B, D>> dataset = new HashMap<> ();
  
  public Set<B> getColumns() {
    Set<B> cols = new HashSet<>();
    for (A a : this) {
      Map<B, D> data = this.get(a);
      if (data != null) {
        cols.addAll(data.keySet());
      }
    }
    return cols;
  }
  
  public void add(A a, B b, D data) {
    if (!dataset.containsKey(a)) {
      dataset.put(a, new HashMap<B, D> ());
    }
    dataset.get(a).put(b, data);
  }
  
  public Map<B, D> get(A a) {
    return this.dataset.get(a);
  }
  
  public void initIfNull(A a, B b, D data) {
    if (!dataset.containsKey(a)) {
      dataset.put(a, new HashMap<B, D> ());
    }
    if (!dataset.get(a).containsKey(b)) {
      dataset.get(a).put(b, data);
    }
  }
  
  public void renameColumn(B a, B b) {
    for (A r : dataset.keySet()) {
      Map<B, D> attributes = dataset.get(r);
      D data = attributes.remove(a);
      if (data != null) {
        attributes.put(b, data);
      }
    }
  }
  
  public Set<A> keySet() {
    return this.dataset.keySet();
  }

  @Override
  public Iterator<A> iterator() {
    return dataset.keySet().iterator();
  }
}
