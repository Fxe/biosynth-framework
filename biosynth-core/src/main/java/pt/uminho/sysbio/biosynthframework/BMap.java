package pt.uminho.sysbio.biosynthframework;

import java.util.Map;
import java.util.Set;

public interface BMap<K, V> extends Map<K, V> {
  public Map<V, Set<K>> getReverse();

  public Set<V> bkeySet();

  public Set<K> bget(V value);
}
