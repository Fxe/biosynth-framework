package pt.uminho.sysbio.biosynthframework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class BiosStringUtils {
  
//  public static<K> String wawawa(CStoichiometry<K> cstoich, Map<K, String> alias) {
//  Map<K, Double> map = new HashMap<> ();
//  for (Pair<K, SubcellularCompartment> p : cstoich.map.keySet()) {
//    map.put(p.getKey(), cstoich.map.get(p));
//  }
//  return mapToString(map, alias);
//  }

  
  public static<K> String wawawa(List<Pair<K, Double>> lstoich, Map<K, String> alias) {
    Map<K, Double> map = new HashMap<> ();
    for (Pair<K, Double> p : lstoich) {
      map.put(p.getLeft(), p.getRight());
    }
    return mapToString(map, alias);
  }
  
  public static String pairToString(String a, double v) {
    if (v != 1) {
      return v + " " + a;
    }
    return a;
  }
  
  public static<K> String mapToString(Map<K, Double> map, Map<K, String> alias) {
    Map<K, Double> lhsMap = new HashMap<> ();
    Map<K, Double> rhsMap = new HashMap<> ();
    
    for (K compId : map.keySet()) {
      double v = map.get(compId);
      if (v > 0) {
        rhsMap.put(compId, Math.abs(v));
      } else if (v < 0) {
        lhsMap.put(compId, Math.abs(v));
      } else {
//        logger.debug("zero value stoich");
      }
    }
    
    return mapToString(lhsMap, rhsMap, alias);
  }
  
  public static<K> String mapToString(Map<K, Double> lhsMap, Map<K, Double> rhsMap, Map<K, String> alias) {
    if (alias == null) {
      alias = new HashMap<> ();
    }
    
    List<K> lhs = new ArrayList<> (lhsMap.keySet());
    List<K> rhs = new ArrayList<> (rhsMap.keySet());
    StringBuilder sb = new StringBuilder();
    if (lhs.isEmpty()) {
      sb.append("");
    } else {
      {
        String str = alias.get(lhs.get(0));
        if (str == null) {
          str = lhs.get(0).toString();
        }
        sb.append(pairToString(str, lhsMap.get(lhs.get(0))));
      }
      for (int i = 1; i < lhs.size(); i++) {
        String str = alias.get(lhs.get(i));
        if (str == null) {
          str = lhs.get(i).toString();
        }
        sb.append(" + ");
        sb.append(pairToString(str, lhsMap.get(lhs.get(i))));
      }
    }
    sb.append(" <=> ");
    if (rhs.isEmpty()) {
      sb.append("");
    } else {
      {
        String str = alias.get(rhs.get(0));
        if (str == null) {
          str = rhs.get(0).toString();
        }
        sb.append(pairToString(str, rhsMap.get(rhs.get(0))));
      }
      for (int i = 1; i < rhs.size(); i++) {
        String str = alias.get(rhs.get(i));
        if (str == null) {
          str = rhs.get(i).toString();
        }
        sb.append(" + ");
        sb.append(pairToString(str, rhsMap.get(rhs.get(i))));
      }
    }
    return sb.toString();
  }
}
