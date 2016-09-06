package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Filipe Liu
 *
 * @param <K>
 * @param <V>
 */
public class BHashMap<K, V> extends HashMap<K, V> implements BMap<K, V> {

  public Map<V, Set<K>> reverse = new HashMap<>();
  
  public BHashMap() { super();}
  
  public BHashMap(Map<K, V> map) {
    for (K k : map.keySet()) {
      V v = map.get(k);
      this.put(k, v);
    }
  }
  
  @Override
  public V put(K key, V value) {
    if (!reverse.containsKey(value)) {
      reverse.put(value, new HashSet<K> ());
    }
    reverse.get(value).add(key);
    return super.put(key, value);
  }
  
  @Override
  public V remove(Object key) {
    
    return super.remove(key);
  }
  
  @Override
  public void clear() {
    reverse.clear();
    super.clear();
  }
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public Map<V, Set<K>> sync() {
    Map<V, Set<K>> rev = new HashMap<> ();
    for (K key : this.keySet()) {
      V value = this.get(key);
      if (!rev.containsKey(value)) {
        rev.put(value, new HashSet<K> ());
      }
      rev.get(value).add(key);
    }
    this.reverse = rev;
    return rev;  
  }
  
  @Override
  public Map<V, Set<K>> getReverse() {
    return reverse;  
  }
  
  @Override
  public Set<V> bkeySet() {
    return this.reverse.keySet();
  }

  @Override
  public Set<K> bget(V value) {
    return this.reverse.get(value);
  }
}
