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
  
  public static String toString(Map<?, ?> map, String sep, String kv) {
    return Joiner.on(sep).withKeyValueSeparator(kv).join(map);
  }
}
