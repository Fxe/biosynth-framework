package pt.uminho.sysbio.biosynthframework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class MapUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(MapUtils.class);
  
  public static<K> Map<K, Double> filterZero(Map<K, Double> s) {
    return filterByValue(s, 0.0);
  }
  
  public static<K> Map<K, Double> filterByValue(Map<K, Double> s, Double value) {
    Map<K, Double> result = new HashMap<> ();
    for (K key : s.keySet()) {
      double v = s.get(key);
      if (v != value) {
        result.put(key, v);
      }
    }
    return result;
  }
  
  public static<K> Map<K, Double> scale(Map<K, Double> s, double v) {
    Map<K, Double> s_ = new HashMap<> ();
    for (K k : s.keySet()) {
      s_.put(k, s.get(k) * v);
    }
    return s_;
  }
  
  public static<K> Map<K, Double> divide(Map<K, Double> s, double v) {
    Map<K, Double> s_ = new HashMap<> ();
    for (K k : s.keySet()) {
      s_.put(k, s.get(k) / v);
    }
    return s_;
  }
  
  public static<K> Map<K, Double> sum(Map<K, Double> s1, Map<K, Double> s2) {
    Map<K, Double> s_ = new HashMap<> ();
    Set<K> i = Sets.intersection(s1.keySet(), s2.keySet());
    for (K k : s1.keySet()) {
      double v = s1.get(k);
      if (i.contains(k)) {
        double v2 = s2.get(k);
        s_.put(k, v + v2);
      } else {
        s_.put(k, v);
      }
    }
    //put remaining
    for (K k : s2.keySet()) {
      double v = s2.get(k);
      if (!i.contains(k)) {
        s_.put(k, v);
      }
    }
    return s_;
  }
  
  public static<K> Map<K, Double> rxnMathSumNeu(Map<K, Double> rxn1, 
      Map<K, Double> rxn2,
      K n) {

    if (rxn1.containsKey(n) && rxn2.containsKey(n)) {
      double x = rxn1.get(n);
      double y = rxn2.get(n);
      logger.debug("{} [+{}+] {}", x, n, y);
      if (Math.abs(y) != 1) {
        rxn2 = divide(rxn2, y);
        y = rxn2.get(n);
      }
      // x + k * y = 0
      double scale = (-1 * x) / y;
      Map<K, Double> rxn2_ = scale(rxn2, scale);
      double y_ = rxn2_.get(n);
      logger.debug("Scale {} by {}", rxn2, scale);
      logger.debug("{} [+{}+] {}", x, n, y_);
      Map<K, Double> rxn3 = sum(rxn1, rxn2_);
      logger.debug("{}", rxn3);
      return rxn3;
    } else {
      logger.warn("specie {} not in both reactions");
      return null;
    }

  }
}
