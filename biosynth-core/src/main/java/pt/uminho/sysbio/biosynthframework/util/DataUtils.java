package pt.uminho.sysbio.biosynthframework.util;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
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
  
  public static<T> T fromJson(String json, Class<T> clazz) throws IOException {
    ObjectMapper om = new ObjectMapper();
    T o =  om.readValue(json, clazz);
    return o;
  }
  
  public static String toJson(Object o) {
    return toJson(o, false);
  }
  
  public static String toJson(Object o, boolean allowNullKey) {
    ObjectMapper om = new ObjectMapper();
    if (allowNullKey) {
      om.getSerializerProvider().setNullKeySerializer(new JsonSerializer<Object>() {

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
          gen.writeFieldName(UUID.randomUUID().toString());
        }
        
      });
    }
    
    om.enable(SerializationFeature.INDENT_OUTPUT);
    String json = null;
    try {
      json = om.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return json;
  }
}
