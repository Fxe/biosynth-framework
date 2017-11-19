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
  
  public static boolean empty(Object string) {
    if (string == null || string.toString().trim().isEmpty()) {
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
  
  public static<X, Y, D> void printData(Map<X, Map<Y, D>> data) {
    MatrixSFactory<X, Y, D> f = new MatrixSFactory<>();
    f.withData(data);
    f.build().printMatrix();
  }
  
  public static<X, Y, D> String getTableStr(Map<X, Map<Y, D>> data, String yAxis, String...order) {
    MatrixSFactory<X, Y, D> f = new MatrixSFactory<>();
    f.withData(data)
     .withYAxisLabel(yAxis);
    return f.build().toTsv(order);
  }
  
  public static<X, Y, D> void printData(Map<X, Map<Y, D>> data, String yAxis, String...order) {
    System.out.println(getTableStr(data, yAxis, order));
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
