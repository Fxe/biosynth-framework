package pt.uminho.sysbio.biosynthframework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pt.uminho.sysbio.biosynthframework.util.MapUtils;

/**
 * 
 * @author Filipe Liu
 *
 * @param <C> the type of compartment
 * @param <T> the type of stoichiometry
 */
public class CompartmentalizedStoichiometry<T, C> {
  public Map<Pair<T, C>, Double> stoichiometry = new HashMap<>();
  
  public CompartmentalizedStoichiometry() { }
  
  public Set<C> getCompartments() {
    Set<C> compartments = new HashSet<>();
    for (Pair<?, C> p  : stoichiometry.keySet()) {
      compartments.add(p.getRight());
    }
    return compartments;
  }
  
  public Set<T> getCompounds() {
    Set<T> compounds = new HashSet<>();
    for (Pair<T, ?> p  : stoichiometry.keySet()) {
      compounds.add(p.getLeft());
    }
    return compounds;
  }
  
  public double addLeft(T k, C c, double value) {
    Pair<T, C> p = new ImmutablePair<T, C>(k, c);
    if (!stoichiometry.containsKey(p)) {
      stoichiometry.put(p, Math.abs(value) * -1);
      return value;
    } else {
      value = Math.abs(value) * -1 + stoichiometry.get(p);
      stoichiometry.put(p, value);
      return value;
    }
  }
  
  public CompartmentalizedStoichiometry(Map<Pair<T, C>, Double> stoichiometry) {
    this.stoichiometry = new HashMap<>(stoichiometry);
  }
  
  public double addRight(T k, C c, double value) {
    Pair<T, C> p = new ImmutablePair<T, C>(k, c);
    if (!stoichiometry.containsKey(p)) {
      stoichiometry.put(p, Math.abs(value));
      return value;
    } else {
      value = Math.abs(value) + stoichiometry.get(p);
      stoichiometry.put(p, value);
      return value;
    }
  }
  
  public double add(T k, C c, double value) {
    Pair<T, C> p = new ImmutablePair<T, C>(k, c);
    stoichiometry.put(p, value);
    return value;
//    if (!stoichiometry.containsKey(p)) {
//      stoichiometry.put(p, value);
//      return value;
//    } else {
//      value = Math.abs(value) + stoichiometry.get(p);
//      stoichiometry.put(p, value);
//      return value;
//    }
  }
  
  public boolean remove(T compound) {
    Set<Pair<T, C>> remove = new HashSet<>();
    for (Pair<T, C> p : stoichiometry.keySet()) {
      if (p.getLeft().equals(compound)) {
        remove.add(p);
      }
    }
    
    stoichiometry.keySet().removeAll(remove);
    
    return !remove.isEmpty();
  }
  
  public void addLeft(Map<Pair<T, C>, Double> l) {
    for (Pair<T, C> p : l.keySet()) {
      this.addLeft(p.getLeft(), p.getRight(), l.get(p));
    }
  }
  
  public void addRight(Map<Pair<T, C>, Double> r) {
    for (Pair<T, C> p : r.keySet()) {
      this.addRight(p.getLeft(), p.getRight(), r.get(p));
    }
  }
  
  public CompartmentalizedStoichiometry<T, C> sum(CompartmentalizedStoichiometry<T, C> stoichiometry) {
    Map<Pair<T, C>, Double> r = MapUtils.sum(this.stoichiometry, stoichiometry.stoichiometry);
    return new CompartmentalizedStoichiometry<>(MapUtils.filterZero(r));
  }
  
  public CompartmentalizedStoichiometry<T, C> scale(double value) {
    Map<Pair<T, C>, Double> r = MapUtils.scale(this.stoichiometry, value);
    return new CompartmentalizedStoichiometry<>(MapUtils.filterZero(r));
  }
  
  @Override
  public int hashCode() {
    return this.stoichiometry.hashCode();
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (o instanceof CompartmentalizedStoichiometry) {
      CompartmentalizedStoichiometry<?, ?> stoich = (CompartmentalizedStoichiometry<?, ?>) o;
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
