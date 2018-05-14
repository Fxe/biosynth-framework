package pt.uminho.sysbio.biosynthframework.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BiosIOUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(BiosIOUtils.class);
  
  public static String digest(String algorithm, File file) 
      throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance(algorithm);
    String digest = null;
    
    try (InputStream is = new FileInputStream(file)) {
      digest = getDigest(new FileInputStream(file), md, 2048);
    } catch (IOException e) {
      logger.warn("{}", e.getMessage());
    }
    
    return digest;
  }

  public static String getDigest(InputStream is, MessageDigest md, int byteArraySize)
      throws NoSuchAlgorithmException, IOException {

    md.reset();
    byte[] bytes = new byte[byteArraySize];
    int numBytes;
    while ((numBytes = is.read(bytes)) != -1) {
      md.update(bytes, 0, numBytes);
    }
    byte[] digest = md.digest();
    String result = new String(Hex.encodeHex(digest));
    return result;
  }
  
  public static void folderScan(Set<String> files, File folder, FileFilter filter) {
    if (!folder.isDirectory()) {
      return;
    }
    
    logger.info("Enter folder {}", folder.getAbsolutePath());
    
    for (File file : folder.listFiles(filter)) {
      if (file.isDirectory()) {
        folderScan(files, file, filter);
      } else {
        files.add(file.getAbsolutePath());
      }
    }
  }
  
  public static String getUrlAsString(String url) {
    URLConnection connection = null;
    try {
      connection = new URL(url).openConnection();
      List<String> lines = org.apache.commons.io.IOUtils.readLines(
          connection.getInputStream(), Charset.defaultCharset());
      if (lines != null) {
        return StringUtils.join(lines, '\n');
      }
    } catch (IOException e) {
      logger.warn("unable to get {}", e.getMessage());
    } finally {
      org.apache.commons.io.IOUtils.close(connection);
    }
    
    return null;
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
    om.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    
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
