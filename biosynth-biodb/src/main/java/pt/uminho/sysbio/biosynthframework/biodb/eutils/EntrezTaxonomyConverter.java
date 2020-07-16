package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Joiner;

import okhttp3.ResponseBody;
import retrofit2.Converter;
//import retrofit.converter.ConversionException;
//import retrofit.converter.Converter;
//import retrofit.mime.TypedInput;
//import retrofit.mime.TypedOutput;
import retrofit2.Retrofit;

public class EntrezTaxonomyConverter extends Converter.Factory implements Converter<ResponseBody, Object> {

  private final ObjectMapper mapper;
  private final Class<?> clazz;
  
  public EntrezTaxonomyConverter(Class<?> clazz) {
    mapper = new XmlMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(Feature.AUTO_CLOSE_TARGET, true);
    this.clazz = clazz;
  }
  
  public static String getXmlStr(InputStream is) throws IOException {
    List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
    return Joiner.on('\n').join(lines);
  }
  
  public static String stripFormat(InputStream is) throws IOException {
    boolean ignore = false;
    List<String> lines = new ArrayList<>();
    for (String l : IOUtils.readLines(is, Charset.defaultCharset())) {
      l = l.replace("<i>", "");
      l = l.replace("</i>", "");
      l = l.replace("<sup>", "");
      l = l.replace("</sup>", "");

      if (!ignore) {
        lines.add(l);
      }
    }
    return Joiner.on('\n').join(lines);
  }
  
  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
    try {
      Class<?> clazz = Class.forName(type.getTypeName());
      return new EntrezTaxonomyConverter(clazz);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(e);
    }
  }
  
//  @Override
//  public Object fromBody(TypedInput body, Type type) throws ConversionException {
//    Object result = null;
//    try {
//      Class<?> clazz = Class.forName(type.getTypeName());
//      result = mapper.readValue(body.in(), clazz);
//    } catch (IOException | ClassNotFoundException e) {
//      e.printStackTrace();
//      throw new ConversionException(e);
//    }
//    return result;
//  }
//
//  @Override
//  public TypedOutput toBody(Object object) {
//    System.out.println("!!");
//    return null;
//  }

  @Override
  public Object convert(ResponseBody value) throws IOException {
    Object result = null;
    String xml = null;
    try (InputStream is = value.byteStream()) {
      xml = getXmlStr(is);
    }
    
    ObjectMapper mapper = new XmlMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(Feature.AUTO_CLOSE_TARGET, true);
    
    try {
      result = mapper.readValue(new ByteArrayInputStream(xml.getBytes()), clazz);
    } catch (IOException ex1) {
      xml = stripFormat(new ByteArrayInputStream(xml.getBytes()));
      result = mapper.readValue(new ByteArrayInputStream(xml.getBytes()), clazz);
    }
    
    return result;
  }

}
