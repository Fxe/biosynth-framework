package pt.uminho.sysbio.biosynthframework.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMapUtils {
  public static String getString(Map<String, Object> map, String key) {
    if (map == null || !map.containsKey(key)) {
      return null;
    }
    return (String) map.get(key);
  }

  public static String getString(Map<String, Object> map, String...keys) {
    Map<String, Object> o = map;
    for (int i = 0; i < keys.length - 1; i++) {
      o = getMap(keys[i], o);
    }
    return getString(o, keys[keys.length - 1]);
  }

  public static Long getLong(String key, Map<String, Object> map) {
    return Math.round( (Double) map.get(key));
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> getMap(String key, Map<String, Object> map) {
    if (map == null || !map.containsKey(key)) {
      return null;
    }
    
    return (Map<String, Object> ) map.get(key);
  }

  @SuppressWarnings("unchecked")
  public static List<Object> getList(String key, Map<String, Object> map) {
    return (List<Object>) map.get(key);
  }

  public static Map<String, Object> cleanNullsAndStrings(Map<String, Object> map) {
    Map<String, Object> result = new HashMap<> ();
    
    for (String k : map.keySet()) {
      Object o = map.get(k);
      
      if (o != null) {
        boolean isString = o instanceof String;
        if (isString && !o.toString().trim().isEmpty()) {
          result.put(k, o.toString().trim());
        } else if (!isString) {
          result.put(k, o);
        }
      }
    }
    
    return result;
  }
}
