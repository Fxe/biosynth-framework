package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.util.MapUtils;

/**
 * 
 * @author Filipe Liu
 *
 * @param <T>
 */
public class SimpleStoichiometry<T> {
  public Map<T, Double> stoichiometry = new HashMap<>();
  
  public SimpleStoichiometry() { }
  
  public SimpleStoichiometry(Map<T, Double> stoichiometry) {
    this.stoichiometry = new HashMap<>(stoichiometry);
  }
  
  public SimpleStoichiometry(Map<T, Double> l, Map<T, Double> r) {
    this.addLeft(l);
    this.addRight(r);
  }
  
  public double addLeft(T k, double value) {
    if (!stoichiometry.containsKey(k)) {
      stoichiometry.put(k, Math.abs(value) * -1);
      return value;
    } else {
      value = Math.abs(value) * -1 + stoichiometry.get(k);
      stoichiometry.put(k, value);
      return value;
    }
  }
  
  public double addRight(T k, double value) {
    if (!stoichiometry.containsKey(k)) {
      stoichiometry.put(k, Math.abs(value));
      return value;
    } else {
      value = Math.abs(value) + stoichiometry.get(k);
      stoichiometry.put(k, value);
      return value;
    }
  }
  
  public void addLeft(Map<T, Double> l) {
    for (T k : l.keySet()) {
      this.addLeft(k, l.get(k));
    }
  }
  
  public void addRight(Map<T, Double> r) {
    for (T k : r.keySet()) {
      this.addRight(k, r.get(k));
    }
  }
  
  public SimpleStoichiometry<T> sum(SimpleStoichiometry<T> stoichiometry) {
    Map<T, Double> r = MapUtils.sum(this.stoichiometry, stoichiometry.stoichiometry);
    return new SimpleStoichiometry<>(MapUtils.filterZero(r));
  }
  
  public SimpleStoichiometry<T> scale(double value) {
    Map<T, Double> r = MapUtils.scale(this.stoichiometry, value);
    return new SimpleStoichiometry<>(MapUtils.filterZero(r));
  }
  
  @Override
  public int hashCode() {
    return this.stoichiometry.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (o instanceof SimpleStoichiometry) {
      SimpleStoichiometry<?> stoich = (SimpleStoichiometry<?>) o;
      return this.stoichiometry.equals(stoich.stoichiometry);
    } else {
      return false;
    }
  }
  
  @Override
  public String toString() {
    return stoichiometry.toString();
  }
}
