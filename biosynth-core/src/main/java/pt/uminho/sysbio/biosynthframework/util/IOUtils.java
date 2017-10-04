package pt.uminho.sysbio.biosynthframework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
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
  
  public static String digest(String algorithm, File file) 
      throws NoSuchAlgorithmException, FileNotFoundException, IOException {
    MessageDigest md = MessageDigest.getInstance(algorithm);

    String digest = getDigest(new FileInputStream(file), md, 2048);
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

  public static String readFromFile(String path) throws FileNotFoundException, IOException {
    return readFromFile(new File(path));
  }

  public static String readFromFile(File file) throws FileNotFoundException, IOException {
    return readFromInputStream(new FileInputStream(file));
//    StringBuilder sb = new StringBuilder();
//
//    FileReader reader = new FileReader(file);
//    BufferedReader br = new BufferedReader(reader);
//    String line;
//    while ( (line = br.readLine()) != null ) {
//      sb.append(line).append('\n');
//    }
//
//    br.close();
//    reader.close();
//
//    return sb.toString();
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
    org.apache.commons.io.IOUtils.closeQuietly(inputStream);
//    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//    StringBuilder sb = new StringBuilder();
//    String line;
//    while ( (line = br.readLine()) != null ) {
//      sb.append(line).append('\n');
//    }
//
//    br.close();
//    inputStream.close();

    return StringUtils.join(lines, '\n');
  }
}
