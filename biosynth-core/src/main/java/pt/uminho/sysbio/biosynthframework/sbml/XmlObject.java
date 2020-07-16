package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.HashMap;
import java.util.Map;

public class XmlObject {
  
  /**
   * Return the line number where the current event ends, returns -1 if none is available.
   */
  public int lineNumber = -1;
  
  /**
   * Return the column number where the current event ends, returns -1 if none is available.
   */
  public int columnNumber = -1;
  
  private Map<String, String> attributes = new HashMap<> ();
  private String data;

  public Map<String, String> getAttributes() { return attributes;}
  public void setAttributes(Map<String, String> attributes) { this.attributes = attributes;}
  
  public String getData() { return data;}
  public void setData(String data) { this.data = data;}
  
  @Override
  public String toString() {
    return String.format("[L:%d,C:%d] - %s", lineNumber, columnNumber, attributes);
  }
}
