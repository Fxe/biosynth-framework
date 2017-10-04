package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.HashMap;
import java.util.Map;

public class XmlMessage {
  public MessageType type;
  public String message;
  public final Integer lineNumber;
  public final Integer columnNumber;
  
  public MessageCategory category;
//  public XmlObject cause;
  public long time;
  
  @Deprecated
  public XmlMessage(XmlObject xo, MessageType type, String str, Object...args) {
    this(xo, MessageCategory.NULL, type, str, args);
  }

  public XmlMessage(XmlObject xo, MessageCategory cat, MessageType type, String str, Object...args) {
    this.type = type;
    this.category = cat;
    
    if (xo != null) {
      this.lineNumber = xo.lineNumber;
      this.columnNumber = xo.columnNumber;
    } else {
      this.lineNumber = null;
      this.columnNumber = null;
    }
//    this.cause = xo;
    this.message = String.format(str, args).replaceAll("\n", "");
    this.time = System.currentTimeMillis();
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
    return String.format("[%s] L:%d C:%d %s - %s", category, lineNumber, columnNumber, type, message);
  }
}
