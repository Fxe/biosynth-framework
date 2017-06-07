package pt.uminho.sysbio.biosynthframework.io.biodb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class Bigg2JsonConverter implements Converter {

  private final ObjectMapper mapper;
  
  private final String cachePath; 
  
  public Bigg2JsonConverter(String cache) {
    cachePath = cache;
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//    mapper.configure(Feature.AUTO_CLOSE_TARGET, true);
  }
  
  public Bigg2JsonConverter() {
    cachePath = null;
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//    mapper.configure(Feature.AUTO_CLOSE_TARGET, true);
  }
  
  public<T> T convert(Class<T> clazz, InputStream is) {
    T result = null;
    try {
//      Class<?> clazz = Class.forName(type.getTypeName());
//      String jsonStr = StringUtils.join(IOUtils.readLines(body.in()), '\n');
//      if (cachePath != null) {
//        OutputStream os = null;
//        try {
//          os = new FileOutputStream(cachePath + "/reactions/")
//          IOUtils.write(jsonStr, os);
//        } catch (IOException e) {
//          e.printStackTrace();          
//        } finally {
//          IOUtils.closeQuietly(os);
//        }
//        
//      }
      result = mapper.readValue(is, clazz);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  @Override
  public Object fromBody(TypedInput body, Type type) throws ConversionException {
    Object result = null;
    try {
      Class<?> clazz = Class.forName(type.getTypeName());
      String jsonStr = StringUtils.join(IOUtils.readLines(body.in()), '\n');
//      if (cachePath != null) {
//        OutputStream os = null;
//        try {
//          os = new FileOutputStream(cachePath + "/reactions/")
//          IOUtils.write(jsonStr, os);
//        } catch (IOException e) {
//          e.printStackTrace();          
//        } finally {
//          IOUtils.closeQuietly(os);
//        }
//        
//      }
      result = mapper.readValue(jsonStr, clazz);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      throw new ConversionException(e);
    }
    return result;
  }

  @Override
  public TypedOutput toBody(Object object) {
    // TODO Auto-generated method stub
    return null;
  }

}
