package pt.uminho.sysbio.biosynthframework.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollectionUtils {

  public static<K> void increaseCount(Map<K, Integer> map, K key, int amount) {
    if (!map.containsKey(key)) {
      map.put(key, 0);
    }

    Integer total = map.get(key);
    total += amount;
    map.put(key, total);
  }

  public static<C extends Collection<T>, T> Collection<T> fromArray(T[] array, Class<C> clazz) {
    try {
      Collection<T> collection = clazz.newInstance();
      for (T o : array) {
        collection.add(o);
      }
      return collection;
    } catch (Exception e) {
      return null;	
    }
  }

  //	public static inters

  public static<C extends Set<T>, T> Set<T> toSet(T[] array, Class<C> clazz) {
    try {
      Set<T> set = clazz.newInstance();
      for (T o : array) {
        set.add(o);
      }
      return set;
    } catch (Exception e) {
      return null;	
    }
  }
  
  public static<K> void mmul(Map<K, Double> map, Double val) {
    for (K o : map.keySet()) {
      double v = map.get(o); 
      map.put(o, v * val);
    }
  }
  
  public static<K, V> Map<V, Set<K>> reverseMap(Map<K, V> map) {
    Map<V, Set<K>> rev = new HashMap<> ();
    for (K key : map.keySet()) {
      V value = map.get(key);
      if (!rev.containsKey(value)) {
        rev.put(value, new HashSet<K> ());
      }
      rev.get(value).add(key);
    }
    return rev;
  }
  
  public static<E> double jaccard(Collection<E> a, Collection<E> b) {
    if (a.isEmpty() && b.isEmpty()) return 1.0;

    Set<E> A_union_B = new HashSet<> (a);
    A_union_B.addAll(b);
    Set<E> A_intersect_B = new HashSet<> (a);
    A_intersect_B.retainAll(b);

    return A_intersect_B.size() / (double)A_union_B.size();
  }
}
