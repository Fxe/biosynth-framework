package pt.uminho.sysbio.biosynthframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import pt.uminho.sysbio.biosynthframework.Dataset;

public class DatasetFactory {
  
  private String splitRegex = "\t";
  private boolean header = true;
  private int key = 0;
  private String[] columnNames = new String[0];
  
  public Dataset<String, String, Object> buildFromFile(File file) {
    Dataset<String, String, Object> result = 
        readDataSet(file.getAbsolutePath(), splitRegex, header, key, columnNames);
    return result;
  }
  
  public static List<String> readFileAsLines(InputStream is) throws IOException {
    List<String> lines = null;
    lines = IOUtils.readLines(is, Charset.defaultCharset());
    return lines;
  }
  
  public static List<String> readFileAsLines(String file) {
    List<String> lines = null;
    InputStream is = null;
    
    try {
      is = new FileInputStream(file);
      lines = readFileAsLines(is);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return lines;
  }
  
  public static Dataset<String, String, Object> readDataSet(
      String file, 
      String splitRegex, 
      boolean header, 
      int key, String...columnNames) {
    
    Dataset<String, String, Object> result = new Dataset<>();
    
    List<String> data = readFileAsLines(file);
    Map<Integer, String> headerLabel = new HashMap<>();

    int start = 0;
    if (header) {
      String[] labels = data.get(0).split(splitRegex);
      for (int i = 0; i < labels.length; i++) {
        headerLabel.put(i, labels[i]);
      }
      start = 1;
    }
    
    if (columnNames != null) {
      for (int i = 0; i < columnNames.length; i++) {
        headerLabel.put(i, columnNames[i]);
      }
    }
    
    for (int i = start; i < data.size(); i++) {
      String[] p = data.get(i).split(splitRegex);
      String k = p[key];
      for (int j = 0; j < p.length; j++) {
        if (j != key) {
          if (!headerLabel.containsKey(j)) {
            headerLabel.put(j, String.format("column_%d", j));
          }
          String field = headerLabel.get(j);
          result.add(k, field, p[j]);
        }
      }
    }
    return result;
  }
}
