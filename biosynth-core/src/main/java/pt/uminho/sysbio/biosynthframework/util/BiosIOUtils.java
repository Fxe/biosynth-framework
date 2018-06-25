package pt.uminho.sysbio.biosynthframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.hssf.record.RecordInputStream.LeftoverDataException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

import pt.uminho.sysbio.biosynthframework.io.FileType;

public class BiosIOUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(BiosIOUtils.class);
  
  public static String getMD5(InputStream is) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      return getDigest(is, md, 2048);
    } catch (IOException | NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public static String getMD5(String string) {
    return getMD5(new ByteArrayInputStream(string.getBytes()));
  }
  
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
  
  public static boolean isSbml(String data) {
    return data.contains("<sbml") &&
           data.contains("<listOfSpecies>") && 
           data.contains("</sbml>") && 
           data.contains("</model>"); 
  }
  
  public static FileType detectType(File f) {
    if (f.exists() && f.isFile()) {
      try (InputStream is = new FileInputStream(f)) {
        StringWriter sw = new StringWriter();
        IOUtils.copy(is, sw, Charset.defaultCharset());
        String data = sw.toString();
        if (isSbml(data)) {
          return FileType.SBML;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      try (XSSFWorkbook is = new XSSFWorkbook(f)) {
        return FileType.XLSX;
      } catch (IOException e) {
//        System.out.println(e.getMessage());
        e.printStackTrace();
      } catch (InvalidFormatException e) {
//        System.out.println(e.getMessage());
//        e.printStackTrace();
      } catch (Exception e) {
//        System.out.println(e.getMessage());
      }
      
      try (
          InputStream fis = new FileInputStream(f);
          HSSFWorkbook is = new HSSFWorkbook(fis)) {
        return FileType.XLS;
      } catch (NotOLE2FileException | IllegalArgumentException | LeftoverDataException e) {
        //working as intended
      } catch (IOException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
      
      try (ZipContainer zip = new ZipContainer(f.getAbsolutePath())) {
        return FileType.ZIP;
      } catch (ZipException e) {
        //working as intended
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }


  
  public static void folderScan(Set<String> files, File folder, FileFilter filter) {
    if (!folder.isDirectory()) {
      return;
    }
    
    logger.debug("Enter folder {}", folder.getAbsolutePath());
    
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
  
  public static String download(String url) throws IOException {
    try {
      return download(new URI(url));
    } catch (URISyntaxException e) {
      throw new IOException(e);
    }
  }
  
  public static String download(URI uri) throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    RequestConfig config = RequestConfig.custom().setCircularRedirectsAllowed(true).build();
//    HttpUriRequest request = new HttpGet(uri);
    HttpGet get = new HttpGet(uri);
    get.setConfig(config);
    HttpResponse response = client.execute(get);
    
    HttpEntity entity = response.getEntity();
    String result = null;
    if (entity != null) {
      InputStream is = entity.getContent();
      StringWriter sw = new StringWriter();
      IOUtils.copy(is, sw, Charset.defaultCharset());
      is.close();
      result = sw.toString();
    }
    
    return result;
  }

  public static InputStream copyToByteArrayStream(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    IOUtils.copy(is, baos);
    return new ByteArrayInputStream(baos.toByteArray());
  }
}
