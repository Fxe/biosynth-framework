package pt.uminho.sysbio.biosynthframework.biodb.uniprot;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.core.convert.ConversionException;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import retrofit2.Converter;

public class UniprotEntryConverter extends Converter.Factory {

  private final ObjectMapper mapper;
  
  public UniprotEntryConverter() {
    mapper = new XmlMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(Feature.AUTO_CLOSE_TARGET, true);
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
//    // TODO Auto-generated method stub
//    return null;
//  }

}
