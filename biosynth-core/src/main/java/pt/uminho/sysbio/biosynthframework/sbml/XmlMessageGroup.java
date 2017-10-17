package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlMessageGroup {
  public MessageType type;
  public String message;
  public final List<Integer> lineNumber = new ArrayList<> ();
  public final List<Integer> columnNumber = new ArrayList<> ();
  
  public MessageCategory category;
//  public XmlObject cause;
  public final List<Long> time = new ArrayList<> ();

  public XmlMessageGroup(MessageCategory cat, MessageType type, String str, Object...args) {
    this.type = type;
    this.category = cat;
    this.message = String.format(str, args).replaceAll("\n", "");
  }
  
  public XmlMessageGroup(XmlObject xo, MessageCategory cat, MessageType type, String str, Object...args) {
    this(cat, type, str, args);
    this.add(xo);
  }
  
  public void add(XmlObject xo) {
    if (xo != null) {
      this.lineNumber.add(xo.lineNumber);
      this.columnNumber.add(xo.columnNumber);
    } else {
      this.lineNumber.add(null);
      this.columnNumber.add(null);
    }
    this.time.add(System.currentTimeMillis());
  }

  public Map<String, String> removeEndline(Map<String, String> attributes) {
    Map<String, String> noEndl = new HashMap<> ();
    
    for (String k : attributes.keySet()) {
      noEndl.put(k, attributes.get(k).replaceAll("\n", ""));
    }
    
    return noEndl;
  }
  
  @Override
  public String toString() {
    return String.format("[%s] %s count: %d - %s", category, type, time.size(), message);
  }
}
