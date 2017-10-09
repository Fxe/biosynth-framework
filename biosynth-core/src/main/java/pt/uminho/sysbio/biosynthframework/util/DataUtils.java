package pt.uminho.sysbio.biosynthframework.util;

import java.util.Map;

import com.google.common.base.Joiner;

public class DataUtils {
  
  public static boolean empty(String string) {
    if (string == null || string.trim().isEmpty()) {
      return true;
    }
    
    return false;
  }
  
  public static String trim(String string) {
    if (string != null) {
      return string.trim();
    }
    
    return null;
  }
  
  public static String toString(Map<?, ?> map, String sep, String kv) {
    return Joiner.on(sep).withKeyValueSeparator(kv).join(map);
  }
  
  public static<A> A getArray(A[] array, int index) {
    if (array == null) {
      return null;
    }
    
    if (index < array.length) {
      return array[index];
    }
    
    return null;
  }
  
  public static<A> A getArray(A[] array, int index, A defaultValue) {
    if (array == null) {
      return defaultValue;
    }
    
    if (index < array.length) {
      return array[index];
    }
    
    return defaultValue;
  }
}
