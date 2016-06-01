package pt.uminho.sysbio.biosynthframework.biodb.eutils;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class EntrezTaxonomyConverter implements Converter {

  private ObjectMapper mapper = new XmlMapper();
  
  public EntrezTaxonomyConverter() {
    mapper = new XmlMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(Feature.AUTO_CLOSE_TARGET, true);
  }
  
  @Override
  public Object fromBody(TypedInput body, Type type) throws ConversionException {
    Object result = null;
    try {
    // TODO Auto-generated method stub
      Class<?> clazz = Class.forName(type.getTypeName());
//      System.out.println(clazz);
//      System.out.println(IOUtils.readLines(body.in()));
      result = mapper.readValue(body.in(), clazz);
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
