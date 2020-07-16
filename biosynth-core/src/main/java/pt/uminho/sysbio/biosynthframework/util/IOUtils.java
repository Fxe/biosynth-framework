package pt.uminho.sysbio.biosynthframework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtils {

  private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
  
  @Deprecated
  public static String digest(String algorithm, File file) 
      throws NoSuchAlgorithmException, FileNotFoundException, IOException {
    MessageDigest md = MessageDigest.getInstance(algorithm);

    String digest = getDigest(new FileInputStream(file), md, 2048);
    return digest;
  }

  @Deprecated
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
  
  @Deprecated
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

  @Deprecated
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
//      e.printStackTrace();
    } finally {
      org.apache.commons.io.IOUtils.close(connection);
    }
    
    return null;
  }
  
  public static void writeToFile(String data, String path, boolean createDir) throws IOException {
    File fileDir = new File(new File(path).getParent() + "/");
    if (!fileDir.exists() && createDir) {
      boolean mkdirs = fileDir.mkdirs();
      if (mkdirs) {
        logger.debug("created path {}", fileDir.getAbsolutePath());
      }
    }
    writeToFile(data, path);
  }

  public static void printDir(String dir) {
    File f = new File(dir); // current directory

    File[] files = f.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        System.out.print("directory:");
      } else {
        System.out.print("     file:");
      }

      try {
        System.out.println(file.getCanonicalPath());
      } catch (IOException ioEx) {
        System.out.println(ioEx);
      }
    }
  }

  public static void printDir() {
    printDir(".");
  }

  public static String readFromFile(String path) throws IOException {
    return readFromFile(new File(path));
  }

  public static String readFromFile(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      String result = readFromInputStream(fis);
      return result;
    }
  }
  
  public static void writeToFile(InputStream is, File file) throws IOException {
    InputStreamReader inputStreamReader = new InputStreamReader(is);
    BufferedReader reader = new BufferedReader(inputStreamReader);
    FileWriter fileWriter = new FileWriter(file);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    for(String line = reader.readLine(); line != null; line = reader.readLine()) {
      bufferedWriter.write(line);
      bufferedWriter.newLine();
    }
    bufferedWriter.flush();
    fileWriter.flush();
    bufferedWriter.close();
    fileWriter.close();
    reader.close();
    inputStreamReader.close();
  }

  public static void writeToFile(String data, File file) throws IOException {
    StringReader stringReader = new StringReader(data);
    BufferedReader bufferedReader = new BufferedReader(stringReader);
    FileWriter fileWriter = new FileWriter(file);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
      bufferedWriter.write(line);
      bufferedWriter.newLine();
    }
    bufferedReader.close();
    bufferedWriter.close();
    fileWriter.close();
    bufferedReader.close();
    stringReader.close();
  }

  public static void writeToFile(String data, String file) throws IOException {
    writeToFile(data, new File(file));
  }

  public static String readFromInputStream(InputStream inputStream) throws IOException {
    List<String> lines = org.apache.commons.io.IOUtils.readLines(inputStream, "UTF-8");
    return StringUtils.join(lines, '\n');
  }
}
